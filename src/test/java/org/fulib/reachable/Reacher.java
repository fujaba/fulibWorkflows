package org.fulib.reachable;

import java.util.ArrayList;
import java.util.Map;

import org.fulib.FulibTables;
import org.fulib.patterns.PatternMatcher;
import org.fulib.patterns.model.Pattern;
import org.fulib.patterns.model.PatternObject;
import org.fulib.tables.ObjectTable;
import org.fulib.yaml.Yaml;
import org.fulib.yaml.YamlIdMap;

public class Reacher {

    private Graph startGraph;
    private Graph reachableGraph;
    ArrayList<Rule> ruleSet = new ArrayList<>();
    private ArrayList<Graph> todoList;
    private Graph currentGraph;
    private Rule currentRule;
    private Map<String, Object> cloneMap;
    private ArrayList<Object> row;
    private YamlIdMap origIdMap;

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
        ObjectTable matchTable = matcher.match(rootName, currentGraph.theObjMap().get(rootName));

        System.out.println(matchTable);
        // for all matches
        for (Object obj : matchTable.getTable()) {
            row = (ArrayList<Object>) obj;
            // create clone
            Object[] objArray = currentGraph.theObjMap().values().toArray();
            origIdMap = new YamlIdMap(row.get(0).getClass().getPackage().getName());
            String yaml = origIdMap.encode(objArray);
            cloneMap = Yaml.decode(yaml);

            // create clone row
            ArrayList<Object> cloneRow = createCloneRow();

            // modify clone
            currentRule.getOp().accept(currentGraph, cloneRow);

            Graph cloneGraph = new Graph().setName("G" + reachableGraph.theObjMap().size()).setObjMap(cloneMap).setLabel("offered tables: " + cloneRow.get(cloneRow.size()-1));
            reachableGraph.withGraph(cloneGraph);
            new Op().setName("offer").setSrc(currentGraph).setTgt(cloneGraph);
        }

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
