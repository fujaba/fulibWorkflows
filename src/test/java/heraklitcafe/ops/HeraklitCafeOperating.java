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

import heraklitcafe.data.Place;
import heraklitcafe.data.Table;

public class HeraklitCafeOperating {

    private Map<String, Object> objMap;
    private Graph reachableGraph;

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

        // add first rule
        PatternBuilder pb = FulibTables.patternBuilder();
        PatternObject freeTablesVar = pb.buildPatternObject("freeTables");
        PatternObject tableVar = pb.buildPatternObject("table");
        pb.buildPatternLink(freeTablesVar,  "place",  "tables",tableVar);

        Rule offerTable = new Rule().setPattern(pb.getPattern()).setOp(this::offerTable);
        reacher.withRule(offerTable);

        // start graph and rules, lets reach
        Graph reachables = reacher.reach();

        // compute reachbility graph
        FulibTools.objectDiagrams().dumpSVG("tmp/reachable/reachable.svg", graph);

        // compute trace graphs
    }

    private void offerTable(Graph graph, ArrayList<Object> row) {
        Table t = (Table) row.get(1);
        Place offeredTables = (Place) graph.theObjMap().get("offeredTables");
        t.setPlace(offeredTables);
    }

}
