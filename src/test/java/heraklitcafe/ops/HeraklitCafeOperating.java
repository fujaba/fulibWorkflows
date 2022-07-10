package heraklitcafe.ops;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import org.fulib.FulibTables;
import org.fulib.FulibTools;
import org.fulib.patterns.PatternBuilder;
import org.fulib.patterns.PatternMatcher;
import org.fulib.patterns.model.Pattern;
import org.fulib.patterns.model.PatternObject;
import org.fulib.reachable.Graph;
import org.fulib.reachable.Op;
import org.fulib.reachable.Reacher;
import org.fulib.reachable.Rule;
import org.fulib.tables.ObjectTable;
import org.fulib.workflows.generators.BoardGenerator;
import org.fulib.yaml.Yaml;

import heraklitcafe.data.Client;
import heraklitcafe.data.Place;
import heraklitcafe.data.Table;

public class HeraklitCafeOperating {

    private Map<String, Object> objMap;
    private Graph reachableGraph;
    private Rule enterRule;

    public static void main(String[] args) {
        new HeraklitCafeOperating().init();
    }

    public void init() {
        // es.yaml laden
        BoardGenerator boardGenerator = new BoardGenerator().setStandAlone();
        boardGenerator.generateBoardFromFile(Path.of("src/gen/resources/heraklit-restaurant.es.yaml"));

        // execute init workflow
        objMap = boardGenerator.loadObjectStructure(Place.class.getPackage().getName(), "default");
        String yaml = Yaml.encode(objMap.values().toArray());
        Graph graph = new Graph().setName("G0").setLabel("start").setObjMap(objMap);

        Reacher reacher = new Reacher().setStartGraph(graph);

        addRules(reacher);

        // start graph and rules, lets reach
        Graph reachables = reacher.reach();
        reacher.draw("tmp/reachable");

        // try enter rule
        // load graph G15
        boardGenerator = new BoardGenerator().setStandAlone();
        boardGenerator.generateBoardFromFile(Path.of("tmp/reachable/G15.es.yaml"));
        objMap = boardGenerator.loadObjectStructure(Place.class.getPackage().getName(), "default");

        // Pattern pattern = enterRule.getPattern();
        // PatternMatcher matcher = FulibTables.matcher(pattern);
        // matcher.withRootPatternObjects(pattern.getObjects());
        // matcher.withRootObjects(objMap.values());
        // matcher.match();
        // ObjectTable matchTable = matcher.getMatchTable(pattern.getObjects().get(pattern.getObjects().size()-1));
        // System.out.println(matchTable);
    }

    private void addRules(Reacher reacher) {
        // offer rule
        PatternBuilder pb = FulibTables.patternBuilder();
        PatternObject freeTablesVar = pb.buildPatternObject("freeTables");
        pb.buildAttributeConstraint(freeTablesVar, Place.class, p -> p.getName().equals("freeTables"));
        PatternObject tableVar = pb.buildPatternObject("table");
        pb.buildPatternLink(freeTablesVar, "place", "tables", tableVar);
        Rule offerTable = new Rule().setName("offer").setPattern(pb.getPattern()).setOp(this::offerTable);
        reacher.withRule(offerTable);

        // enter rule
        pb = FulibTables.patternBuilder();
        PatternObject offeredTablesVar = pb.buildPatternObject("offeredTables");
        pb.buildAttributeConstraint(offeredTablesVar, Place.class, p -> p.getName().equals("offeredTables"));
        PatternObject clientsEntryVar = pb.buildPatternObject("clientsEntry");
        pb.buildAttributeConstraint(clientsEntryVar, Place.class, p -> p.getName().equals("clientsEntry"));
        tableVar = pb.buildPatternObject("table");
        PatternObject clientVar = pb.buildPatternObject("client");
        pb.buildPatternLink(offeredTablesVar, "place", "tables", tableVar);
        pb.buildPatternLink(clientsEntryVar, "place", "clients", clientVar);
        enterRule = new Rule().setName("enter").setPattern(pb.getPattern()).setOp(this::enter);
        reacher.withRule(enterRule);

    }

    private void offerTable(Graph graph, ArrayList<Object> row) {
        Table t = (Table) row.get(1);
        Place offeredTables = (Place) graph.objMap().get("offeredTables");
        t.setPlace(offeredTables);
        graph.setLabel("offeredTables: " + offeredTables.getTables());
    }

    private void enter(Graph graph, ArrayList<Object> row) {
        Table t = (Table) row.get(1);
        Client c = (Client) row.get(3);
        Place clientsReady = (Place) graph.objMap().get("clientsReady");
        t.setPlace(clientsReady);
        c.setPlace(clientsReady);
        c.setTable(t);
        graph.setLabel("clientsReady: " + clientsReady.getClients());
    }

}
