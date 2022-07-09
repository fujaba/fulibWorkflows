package org.fulib.reachable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import org.fulib.FulibTables;
import org.fulib.patterns.PatternMatcher;
import org.fulib.patterns.model.Pattern;
import org.fulib.patterns.model.PatternObject;
import org.fulib.tables.ObjectTable;
import org.fulib.yaml.*;

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

    public Reacher withRule(Rule rule) {
        ruleSet.add(rule);
        return this;
    }

    public Reacher setStartGraph(Graph graph) {
        this.startGraph = graph;
        return this;
    }

    public Graph reach() {
        reachableGraph = new Graph().setName("reachable").setLabel("reachable heraklit cafe states");
        certify(startGraph);
        certificateMap.put(startGraph.certificate(), startGraph);
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
        PatternObject rootPO = pattern.getObjects().get(0);
        String rootName = rootPO.getName();
        PatternMatcher matcher = FulibTables.matcher(pattern);
        ObjectTable matchTable = matcher.match(rootName, currentGraph.objMap().get(rootName));

        System.out.println(matchTable);
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
            Graph cloneGraph = new Graph().setName("G" + reachableGraph.objMap().size()).setObjMap(cloneMap).setLabel("offered tables: " + cloneRow.get(cloneRow.size()-1));

            // modify clone
            currentRule.getOp().accept(cloneGraph, cloneRow);

            // compute certificate
            certify(cloneGraph);
            Graph oldGraph = certificateMap.get(cloneGraph.certificate());
            if (oldGraph != null) {
                // just add op link
                new Op().setName(currentRule.getName()).setSrc(currentGraph).setTgt(oldGraph);
            }
            else {
                certificateMap.put(cloneGraph.certificate(), cloneGraph);
                reachableGraph.withGraph(cloneGraph);
                new Op().setName(currentRule.getName()).setSrc(currentGraph).setTgt(cloneGraph);
                todoList.add(cloneGraph);
            }
        }

    }

    private Reacher certify(Graph g) {
        // for all objects
        Collection<Object> objects = g.objMap().values();
        packageName = objects.iterator().next().getClass().getPackage().getName();
        reflectorMap = new ReflectorMap(packageName);
        TreeSet<String> dataNotes = new TreeSet<>();
        for (Object obj : objects) {
            String oneData = certifyProperties(dataNotes, obj);
            dataNotes.add(oneData);
        }

        String certificate = String.join("\n", dataNotes);
        g.setCertificate(certificate);
        try {
            Files.writeString(Path.of("tmp/"+g.getName()+".es.yaml"), certificate);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    private String certifyProperties(TreeSet<String> dataNotes, Object obj) {
        Reflector reflector = reflectorMap.getReflector(obj);
        // for each property
        String result = String.format("- data: %s %s\n",  obj.getClass().getSimpleName(),  reflector.getValue(obj, "name"));

        for (String prop : reflector.getAllProperties()) {
            Object value = reflector.getValue(obj, prop);
            String valueString = "";
            if (value instanceof Collection valueList) {
                if (valueList.isEmpty()) {
                    continue;
                }
                valueString = certifyCollection(valueList);
            }
            else if (value.getClass().getPackage().getName().equals(packageName)) {
                Reflector valueReflector = reflectorMap.getReflector(value);
                valueString = (String) valueReflector.getValue(value, "name");
            }
            else {
                valueString = "" + value;
            }
            String line = String.format("  %s: %s\n", prop, valueString);
            result += line;
        }
        return result;
    }

    private String certifyCollection(Collection valueList) {
        TreeSet<String> valueSet = new TreeSet<>();
        for (Object value : valueList) {
            Reflector valueReflector = reflectorMap.getReflector(value);
            String name = (String) valueReflector.getValue(value, "name");
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
