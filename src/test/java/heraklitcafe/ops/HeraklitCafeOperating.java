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

import heraklitcafe.data.*;

public class HeraklitCafeOperating {

    private Map<String, Object> objMap;
    private Graph reachableGraph;
    private Rule enterRule;

    public static void main(String[] args) {
        new HeraklitCafeOperating().runExperiments();
    }

    public void runExperiments() {
        moreRulesNoNames();
        // offerTablesAndEnterRulesNoNames();
        // offerTablesAndEnterRulesWithTableAndCustomerNames();

    }

    private void moreRulesNoNames() {
        BoardGenerator boardGenerator = new BoardGenerator().setStandAlone();
        boardGenerator.generateBoardFromFile(Path.of("src/gen/resources/heraklit-restaurant.es.yaml"));

        // execute init workflow
        objMap = boardGenerator.loadObjectStructure(Place.class.getPackage().getName(), "default");
        String yaml = Yaml.encode(objMap.values().toArray());
        Graph graph = new Graph().setName("G0").setLabel("start").setObjMap(objMap);

        Reacher reacher = new Reacher()
                .setStartGraph(graph)
                .setDrawPath("tmp/reachable/moreRulesNoNames")
                .setCertifierIgnoreNames(true);

        addSelectRule(reacher);
        addOfferAndEnterRules(reacher);

        // start graph and rules, lets reach
        Graph reachables = reacher.reach();
        reacher.draw();
    }

    private void offerTablesAndEnterRulesNoNames() {
        BoardGenerator boardGenerator = new BoardGenerator().setStandAlone();
        boardGenerator.generateBoardFromFile(Path.of("src/gen/resources/heraklit-restaurant.es.yaml"));

        // execute init workflow
        objMap = boardGenerator.loadObjectStructure(Place.class.getPackage().getName(), "default");
        String yaml = Yaml.encode(objMap.values().toArray());
        Graph graph = new Graph().setName("G0").setLabel("start").setObjMap(objMap);

        Reacher reacher = new Reacher()
                .setStartGraph(graph)
                .setDrawPath("tmp/reachable/offerTablesAndEnterRulesNoNames")
                .setCertifierIgnoreNames(true);

        addOfferAndEnterRules(reacher);

        // start graph and rules, lets reach
        Graph reachables = reacher.reach();
        reacher.draw();
    }

    private void offerTablesAndEnterRulesWithTableAndCustomerNames() {
        BoardGenerator boardGenerator = new BoardGenerator().setStandAlone();
        boardGenerator.generateBoardFromFile(Path.of("src/gen/resources/heraklit-restaurant.es.yaml"));

        // execute init workflow
        objMap = boardGenerator.loadObjectStructure(Place.class.getPackage().getName(), "default");
        String yaml = Yaml.encode(objMap.values().toArray());
        Graph graph = new Graph().setName("G0").setLabel("start").setObjMap(objMap);

        Reacher reacher = new Reacher()
                .setStartGraph(graph)
                .setDrawPath("tmp/reachable/offerTablesAndEnterRulesWithTableAndCustomerNames");

        addOfferAndEnterRules(reacher);

        // start graph and rules, lets reach
        Graph reachables = reacher.reach();
        reacher.draw();
    }

    private void addOfferAndEnterRules(Reacher reacher) {
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

    private void addSelectRule(Reacher reacher) {
        // offer rule
        PatternBuilder pb = FulibTables.patternBuilder();
        PatternObject clientsReadyPlaceVar = pb.buildPatternObject("clientsReady");
        PatternObject menuPlaceVar = pb.buildPatternObject("menu");
        PatternObject ordersPlaceVar = pb.buildPatternObject("orders");
        PatternObject waitingClientsPlaceVar = pb.buildPatternObject("waitingClients");
        PatternObject tableVar = pb.buildPatternObject("table");
        PatternObject mealVar = pb.buildPatternObject("meal");


        pb.buildAttributeConstraint(menuPlaceVar, Place.class, p -> p.getName().equals("menu"));
        pb.buildAttributeConstraint(clientsReadyPlaceVar, Place.class, p -> p.getName().equals("clientsReady"));
        pb.buildAttributeConstraint(ordersPlaceVar, Place.class, p -> p.getName().equals("orders"));
        pb.buildAttributeConstraint(waitingClientsPlaceVar, Place.class, p -> p.getName().equals("waitingClients"));

        pb.buildPatternLink(clientsReadyPlaceVar, "place", "tables", tableVar);
        pb.buildPatternLink(menuPlaceVar, "place", "meals", mealVar);

        Rule selectRule = new Rule().setName("select").setPattern(pb.getPattern()).setOp(this::selectOp);
        reacher.withRule(selectRule);


    }


    private void selectOp(Graph graph, ArrayList<Object> row) {
        Table t = (Table) row.get(1);
        Client c = t.getClient();
        Meal m = (Meal) row.get(3);
        Place orders = (Place) row.get(4);
        Place waitingClients = (Place) row.get(5);

        long orderNum = graph.objMap().values().stream().filter(o -> (o instanceof Order)).count();

        Order order = new Order().setName("order" + orderNum);
        order.setTable(t);
        order.setMeal(m);
        order.withPlace(orders, waitingClients);

        waitingClients.withClients(c);
        waitingClients.withTables(t);

        // create an Order and add it to
        graph.setLabel("orders: " + orders.getOrders());
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
