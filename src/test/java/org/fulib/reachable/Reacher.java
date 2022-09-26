package org.fulib.reachable;

import guru.nidi.graphviz.attribute.Font;
import guru.nidi.graphviz.attribute.Label;
import guru.nidi.graphviz.attribute.Shape;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.model.Node;
import org.fulib.FulibTables;
import org.fulib.FulibTools;
import org.fulib.patterns.PatternMatcher;
import org.fulib.patterns.model.Pattern;
import org.fulib.patterns.model.PatternObject;
import org.fulib.patterns.model.RoleObject;
import org.fulib.tables.ObjectTable;
import org.fulib.tools.GraphDiagram;
import org.fulib.yaml.Reflector;
import org.fulib.yaml.ReflectorMap;
import org.fulib.yaml.Yaml;
import org.fulib.yaml.YamlIdMap;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;

import static guru.nidi.graphviz.attribute.Attributes.attr;
import static guru.nidi.graphviz.model.Factory.*;
import static guru.nidi.graphviz.model.Link.to;

public class Reacher {

    private Graph startGraph;
    private Graph reachableGraph;
    private Map<String, Graph> certificateMap = new TreeMap<>();
    ArrayList<Rule> ruleSet = new ArrayList<>();
    private ArrayList<Graph> todoList;
    private Graph currentGraph;
    private Rule currentRule;
    private Map<String, Object> cloneMap;
    private ArrayList<Object> row;
    private YamlIdMap origIdMap;
    private ReflectorMap reflectorMap;
    private String packageName;
    private String drawPath;
    private boolean certifierIgnoreNames = false;
    private String nameIgnoredClasses = "Table Client ItemRef MealItem";

    public ArrayList<Rule> getRuleSet() {
        return ruleSet;
    }

    public Reacher withRule(Rule rule) {
        ruleSet.add(rule);
        return this;
    }

    public Reacher setDrawPath(String drawPath) {
        this.drawPath = drawPath;
        return this;
    }

    public Reacher setCertifierIgnoreNames(boolean certifierIgnoreNames) {
        this.certifierIgnoreNames = certifierIgnoreNames;
        return this;
    }

    public Reacher setStartGraph(Graph graph) {
        this.startGraph = graph;
        return this;
    }

    public void drawRules()
    {

        for (Rule r : this.getRuleSet()) {
            String ruleName = r.getName();
            String prefix = "lhs.";

            MutableGraph lhs = mutGraph("LHS").setDirected(true).setCluster(true);
            lhs.graphAttrs().add(Label.of("LHS"));
            lhs.nodeAttrs().add(Shape.BOX);

            for(PatternObject po : r.getPattern().getObjects()) {
                Node node = node(prefix + po.getName()).with(Label.of(po.getName()));
                lhs = lhs.add(node);
            }

            if (r.getPatternConstraint() != null) {
                Node node = node(prefix + "constraint").with(Label.of(r.getPatternConstraint()), Shape.NONE);
                lhs.add(node);
            }

            ArrayList<RoleObject> doneRoles = new ArrayList<>();

            for( RoleObject role : r.getPattern().getRoles()) {
                if (doneRoles.contains(role)) {
                    continue;
                }
                Node link = node(prefix + role.getObject().getName())
                      .link(to(node(prefix + role.getOther().getObject().getName()))
                            .with(attr("label", Label.of(role.getOther().getName()))));
                lhs.add(link);
                doneRoles.add(role);
                doneRoles.add(role.getOther());
            }


            MutableGraph rhs = mutGraph("RHS").setDirected(true).setCluster(true);
            rhs.graphAttrs().add(Label.of("RHS"));
            rhs.nodeAttrs().add(Shape.BOX);

            if (r.getRhs() != null) {
                for(PatternObject po : r.getRhs().getObjects()) {
                    Node node = node(po.getName()).with(Label.of(po.getName()));
                    rhs = rhs.add(node);
                }

                doneRoles = new ArrayList<>();

                for( RoleObject role : r.getRhs().getRoles()) {
                    if (doneRoles.contains(role)) {
                        continue;
                    }
                    Node link = node(role.getObject().getName())
                          .link(to(node(role.getOther().getObject().getName())).with(attr("label", Label.of(role.getOther().getName()))));
                    rhs.add(link);
                    doneRoles.add(role);
                    doneRoles.add(role.getOther());
                }
            }

            guru.nidi.graphviz.model.Graph g = graph(ruleName).directed()
                  .graphAttr().with(Label.of(ruleName))
                  .nodeAttr().with(Font.name("arial"))
                  .linkAttr().with("class", "link-class")
                  .with(lhs, rhs);
            try {
                Graphviz.fromGraph(g).height(100).render(Format.SVG).toFile(new File(String.format("tmp/reachable/rules/%s.svg", ruleName)));
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Drawing " +  r.getName());
        }
    }


    public void draw() {
        // FulibTools.objectDiagrams().dumpSVG(drawPath + "/reachable.svg", reachableGraph.objMap().get("G0"));

        System.out.println("Number of reachable graphs: " + reachableGraph.objMap().size());
        // draw reachable with links
        GraphDiagram diag = new GraphDiagram();
        for (Object object : reachableGraph.objMap().values()) {
            Graph g = (Graph) object;
            diag.addNode(g.getName(), g.getName(), g.getLabel())
                    .addHref(g.getName(), String.format("href=\"%s.svg\"", g.getName()));

            for (Op op : g.getCons()) {
                Graph tgt = op.getTgt();
                diag.addEdge(g.getName(), op.getName(), tgt.getName(), "");
            }
        }
        String svg = diag.toSVG();

        try {
            Files.createDirectories(Path.of(drawPath));
            Files.writeString(Path.of(drawPath + "/linkedReachable.svg"), svg);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // draw states
        for (Object obj : reachableGraph.objMap().values()) {
            Graph graph = (Graph) obj;
            TreeMap<String, Object> treeMap = new TreeMap();
            treeMap.putAll(graph.objMap());
            FulibTools.objectDiagrams().dumpSVG(String.format("%s/%s.svg", drawPath, graph.getName()),
                    treeMap.values());
        }
    }

    public Graph reach() {
        reachableGraph = new Graph().setName("reachable").setLabel("reachable heraklit cafe states");
        certify(startGraph);
        certificateMap.put(startGraph.certificate(), startGraph);
        try {
            Files.createDirectories(Path.of(this.drawPath));
            Files.writeString(Path.of(this.drawPath + "/startGraphCertificate.txt"), startGraph.certificate());
            Files.writeString(Path.of(this.drawPath + "/matchTables.txt"), "");
        } catch (IOException e) {
            e.printStackTrace();
        }
        reachableGraph.withGraph(startGraph);

        todoList = new ArrayList<>();
        todoList.add(startGraph);

        while (!todoList.isEmpty()) {
            currentGraph = todoList.remove(0);

            reachAllRules();
        }

        return reachableGraph;
    }

    private void reachAllRules() {
        // for all rules
        for (Rule r : ruleSet) {
            currentRule = r;
            reachAllMatches();
        }
    }

    private void reachAllMatches() {
        Pattern pattern = currentRule.getPattern();
        PatternMatcher matcher = FulibTables.matcher(pattern);
        matcher.withRootPatternObjects(pattern.getObjects());
        matcher.withRootObjects(currentGraph.objMap().values());
        matcher.match();
        ObjectTable matchTable = matcher.getMatchTable(pattern.getObjects().get(0));

        try {
            Files.writeString(Path.of(this.drawPath + "/matchTables.txt"), matchTable.toString(),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // System.out.println(matchTable);
        // for all matches
        for (Object obj : matchTable.getTable()) {
            row = (ArrayList<Object>) obj;
            // create clone
            Object[] objArray = currentGraph.objMap().values().toArray();
            origIdMap = new YamlIdMap(objArray[0].getClass().getPackage().getName());
            String yaml = origIdMap.encode(objArray);
            cloneMap = Yaml.decode(yaml);

            // create clone row
            ArrayList<Object> cloneRow = createCloneRow();
            Graph cloneGraph = new Graph().setName("G" + reachableGraph.objMap().size()).setObjMap(cloneMap)
                    .setLabel("offered tables: " + cloneRow.get(cloneRow.size() - 1));

            // modify clone
            currentRule.getOp().accept(cloneGraph, cloneRow);

            // compute certificate
            certify(cloneGraph);
            Graph oldGraph = certificateMap.get(cloneGraph.certificate());
            if (oldGraph != null) {
                // just add op link
                new Op().setName(currentRule.getName()).setSrc(currentGraph).setTgt(oldGraph);
            } else {
                certificateMap.put(cloneGraph.certificate(), cloneGraph);
                reachableGraph.withGraph(cloneGraph);
                new Op().setName(currentRule.getName()).setSrc(currentGraph).setTgt(cloneGraph);
                todoList.add(cloneGraph);
                try {
                    Files.writeString(Path.of(this.drawPath + "/" + cloneGraph.getName() + ".cert.txt"),
                            cloneGraph.certificate());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private Reacher certify(Graph g) {
        // for all objects
        Collection<Object> objects = g.objMap().values();
        packageName = objects.iterator().next().getClass().getPackage().getName();
        reflectorMap = new ReflectorMap(packageName);
        TreeSet<String> dataNotes = new TreeSet<>((a, b) -> a.compareTo(b) < 0 ? -1 : 1);
        dataNotes.add("- workflow: state\n");
        int numOfItemRef = 1;
        for (Object obj : objects) {
            String oneData = certifyProperties(dataNotes, obj);
            dataNotes.add(oneData);
        }

        String certificate = String.join("\n", dataNotes);
        g.setCertificate(certificate);
        try {
            Files.createDirectories(Path.of(this.drawPath));
            Files.writeString(Path.of(this.drawPath + "/" + g.getName() + ".es.yaml"), certificate);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    private String getCertifierName(Reflector reflector, Object obj) {
        if (this.certifierIgnoreNames) {
            if (nameIgnoredClasses.indexOf(obj.getClass().getSimpleName()) >= 0) {
                return "nn";
            }
            return reflector.getValue(obj, "name").toString();
        } else {
            return reflector.getValue(obj, "name").toString();
        }
    }

    private String certifyProperties(TreeSet<String> dataNotes, Object obj) {
        Reflector reflector = reflectorMap.getReflector(obj);
        // for each property
        String result = String.format("- data: %s %s\n", obj.getClass().getSimpleName(),
                getCertifierName(reflector, obj));

        for (String prop : reflector.getAllProperties()) {
            if (prop.equals("name") &&
                    (nameIgnoredClasses.indexOf(obj.getClass().getSimpleName()) >= 0)) {
                continue;
            }
            Object value = reflector.getValue(obj, prop);
            if (value == null) {
                continue;
            }
            String valueString = "";
            if (value instanceof Collection valueList) {
                if (valueList.isEmpty()) {
                    continue;
                }
                valueString = certifyCollection(valueList);
            } else if (value.getClass().getPackage().getName().equals(packageName)) {
                Reflector valueReflector = reflectorMap.getReflector(value);
                valueString = (String) getCertifierName(valueReflector, value);
            } else {
                valueString = "" + value;
            }
            String line = String.format("  %s: %s\n", prop, valueString);
            result += line;
        }
        return result;
    }

    private String certifyCollection(Collection valueList) {
        TreeSet<String> valueSet = new TreeSet<>((a, b) -> a.compareTo(b) < 0 ? -1 : 1);
        for (Object value : valueList) {
            Reflector valueReflector = reflectorMap.getReflector(value);
            String name = (String) getCertifierName(valueReflector, value);
            valueSet.add(name);
        }
        String line = String.join(", ", valueSet);
        return String.format("[%s]", line);
    }

    private ArrayList<Object> createCloneRow() {
        ArrayList<Object> result = new ArrayList<>();
        for (Object object : row) {
            String id = origIdMap.getId(object);
            Object clone = cloneMap.get(id);
            result.add(clone);
        }
        return result;
    }

}
