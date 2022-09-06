package heraklitcafe.ops;

import java.nio.file.Path;
import java.util.*;
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
        if (args != null && args.length > 0 && args[0].equals("drawRules")) {
            new HeraklitCafeOperating().drawRules();
        }
        else {
            new HeraklitCafeOperating().runExperiments();
        }
    }

    private void drawRules()
    {
        Reacher reacher = new Reacher();

        addClientsRule(reacher);
        releaseTableRule(reacher);
        addLeaveRule(reacher);
        handOverRule(reacher);
        addOfferAndEnterRules(reacher);
        addSelectRule(reacher);
        addUnfoldRule(reacher);
        addCookRule(reacher);

        reacher.drawRules();
    }

    public void runExperiments() {
        moreRulesNoNamesReduceByModules();
        moreRulesNoNamesNoClients();
        moreRulesNoNames();
        offerTablesAndEnterRulesNoNames();
        offerTablesAndEnterRulesWithTableAndCustomerNames();

    }

    private void moreRulesNoNamesReduceByModules() {
        System.out.println("doing moreRulesNoNamesReduceByModules");
        BoardGenerator boardGenerator = new BoardGenerator().setStandAlone();
        boardGenerator.generateBoardFromFile(Path.of("src/gen/resources/heraklit-restaurant.es.yaml"));

        // execute init workflow
        objMap = boardGenerator.loadObjectStructure(Place.class.getPackage().getName(), "default");
        String yaml = Yaml.encode(objMap.values().toArray());
        Graph graph = new Graph().setName("G0").setLabel("start").setObjMap(objMap);

        Reacher reacher = new Reacher()
                .setStartGraph(graph)
                .setDrawPath("tmp/reachable/moreRulesNoNamesReduceByModules")
                .setCertifierIgnoreNames(true);

        addClientsRule(reacher);
        releaseTableRule(reacher);
        addLeaveRule(reacher);
        handOverRule(reacher);
        addOfferAndEnterRules(reacher);
        addSelectRule(reacher);
        addUnfoldRule(reacher);
        addCookRule(reacher);

        // start graph and rules, lets reach
        Graph reachables = reacher.reach();

        // try to remove inner transitions
        // find all transitions gp -enter-> gx
        removeListOfInnerStates(reachables, "enter", "unfold", "cook", "releaseTable");

        reacher.draw();
        System.out.println(" ....  moreRulesNoNamesReduceByModules done");
    }

    private void removeListOfInnerStates(Graph reachables, String... labelList) {
        for (String label : labelList) {
            removeInnerStates(reachables, label);
        }
    }

    private void removeInnerStates(Graph reachables, String label) {
        ArrayList<Object> stateList = new ArrayList<>(reachables.objMap().values());
        for (Object state : stateList) {
            Graph gx = (Graph) state;
            if ( ! hasProd(gx, label)) {
                continue;
            }

            // remove incoming ops, store gp -enter-> gx
            Graph gp = null;
            ArrayList<Op> opList = new ArrayList<>(gx.getProds());
            for (Op op : opList) {
                if (op.getName().equals(label)) {
                    gp = op.getSrc();
                }
                op.removeYou();
            }

            // for all gx -> gn replace by gp -> gn
            opList = new ArrayList<>(gx.getCons());
            for (Op op : opList) {
                new Op().setName(op.getName())
                .setSrc(gp).setTgt(op.getTgt());
                op.removeYou();
            }
            // remove gx
            reachables.objMap().remove(gx.getName());
        }
    }


    private boolean hasProd(Graph gx, String label) {
        for (Op op : gx.getProds()) {
            if (op.getName().equals(label)) {
                return true;
            }
        }
        return false;
    }

    private void moreRulesNoNamesNoClients() {
        BoardGenerator boardGenerator = new BoardGenerator().setStandAlone();
        boardGenerator.generateBoardFromFile(Path.of("src/gen/resources/heraklit-restaurantNoClients.es.yaml"));

        // execute init workflow
        objMap = boardGenerator.loadObjectStructure(Place.class.getPackage().getName(), "default");
        String yaml = Yaml.encode(objMap.values().toArray());
        Graph graph = new Graph().setName("G0").setLabel("start").setObjMap(objMap);

        Reacher reacher = new Reacher()
                .setStartGraph(graph)
                .setDrawPath("tmp/reachable/moreRulesNoNamesNoClients")
                .setCertifierIgnoreNames(true);

        addClientsRule(reacher);
        releaseTableRule(reacher);
        addLeaveRule(reacher);
        handOverRule(reacher);
        addOfferAndEnterRules(reacher);
        addSelectRule(reacher);
        addUnfoldRule(reacher);
        addCookRule(reacher);

        // start graph and rules, lets reach
        Graph reachables = reacher.reach();
        reacher.draw();
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

        addClientsRule(reacher);
        releaseTableRule(reacher);
        addLeaveRule(reacher);
        handOverRule(reacher);
        addOfferAndEnterRules(reacher);
        addSelectRule(reacher);
        addUnfoldRule(reacher);
        addCookRule(reacher);

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
        PatternObject offeredTablesVar = pb.buildPatternObject("offeredTables");
        pb.buildAttributeConstraint(freeTablesVar, Place.class, p -> p.getName().equals("freeTables"));
        pb.buildAttributeConstraint(offeredTablesVar, Place.class, p -> p.getName().equals("offeredTables"));
        PatternObject tableVar = pb.buildPatternObject("table");
        pb.buildPatternLink(freeTablesVar, "place", "tables", tableVar);
        Rule offerTable = new Rule().setName("offer")
              .setPattern(pb.getPattern());

        pb = FulibTables.patternBuilder();
        freeTablesVar = pb.buildPatternObject("freeTables");
        offeredTablesVar = pb.buildPatternObject("offeredTables");
        pb.buildAttributeConstraint(freeTablesVar, Place.class, p -> p.getName().equals("freeTables"));
        pb.buildAttributeConstraint(offeredTablesVar, Place.class, p -> p.getName().equals("offeredTables"));
        tableVar = pb.buildPatternObject("table");
        pb.buildPatternLink(offeredTablesVar, "place", "tables", tableVar);

        offerTable.setRhs(pb.getPattern()).setOp(this::offerTable);
        reacher.withRule(offerTable);

        // enter rule
        pb = FulibTables.patternBuilder();
        offeredTablesVar = pb.buildPatternObject("offeredTables");
        pb.buildAttributeConstraint(offeredTablesVar, Place.class, p -> p.getName().equals("offeredTables"));
        PatternObject clientsEntryVar = pb.buildPatternObject("clientsEntry");
        pb.buildAttributeConstraint(clientsEntryVar, Place.class, p -> p.getName().equals("clientsEntry"));
        PatternObject clientsReadyVar = pb.buildPatternObject("clientsReady");
        pb.buildAttributeConstraint(clientsReadyVar, Place.class, p -> p.getName().equals("clientsReady"));
        tableVar = pb.buildPatternObject("table");
        PatternObject clientVar = pb.buildPatternObject("client");
        pb.buildPatternLink(offeredTablesVar, "place", "tables", tableVar);
        pb.buildPatternLink(clientsEntryVar, "place", "clients", clientVar);
        enterRule = new Rule().setName("enter").setPattern(pb.getPattern()).setOp(this::enter);

        pb = FulibTables.patternBuilder();
        offeredTablesVar = pb.buildPatternObject("offeredTables");
        pb.buildAttributeConstraint(offeredTablesVar, Place.class, p -> p.getName().equals("offeredTables"));
        clientsEntryVar = pb.buildPatternObject("clientsEntry");
        pb.buildAttributeConstraint(clientsEntryVar, Place.class, p -> p.getName().equals("clientsEntry"));
        clientsReadyVar = pb.buildPatternObject("clientsReady");
        pb.buildAttributeConstraint(clientsReadyVar, Place.class, p -> p.getName().equals("clientsReady"));
        tableVar = pb.buildPatternObject("table");
        clientVar = pb.buildPatternObject("client");
        //        t.setPlace(clientsReady);
        //        c.setPlace(clientsReady);
        //        c.setTable(t);
        pb.buildPatternLink(clientsReadyVar, "place", "tables", tableVar);
        pb.buildPatternLink(clientsReadyVar, "place", "clients", clientVar);
        pb.buildPatternLink(clientVar, "client", "table", tableVar);
        enterRule.setRhs(pb.getPattern());



        reacher.withRule(enterRule);

    }

    private void addClientsRule(Reacher reacher) {
        // offer rule
        PatternBuilder pb = FulibTables.patternBuilder();
        PatternObject offeredTablesVar = pb.buildPatternObject("offeredTables");
        pb.buildAttributeConstraint(offeredTablesVar, Place.class, p -> p.getName().equals("offeredTables"));
        PatternObject clientsEntryPlaceVar = pb.buildPatternObject("clientsEntry");
        pb.buildAttributeConstraint(clientsEntryPlaceVar, Place.class, p -> p.getName().equals("clientsEntry"));
        pb.buildMatchConstraint(map -> (((Place) map.get("offeredTables")).getTables().size() == 4
                && ((Place) map.get("clientsEntry")).getClients().size() == 0),
                offeredTablesVar, clientsEntryPlaceVar);
        Rule rule = new Rule().setName("addClients").setPattern(pb.getPattern()).setOp(this::addClientsOp);
        rule.setPatternConstraint("{offeredTables.tables.size() == 4}");

        pb = FulibTables.patternBuilder();
        offeredTablesVar = pb.buildPatternObject("offeredTables");
        pb.buildAttributeConstraint(offeredTablesVar, Place.class, p -> p.getName().equals("offeredTables"));
        clientsEntryPlaceVar = pb.buildPatternObject("clientsEntry");
        pb.buildAttributeConstraint(clientsEntryPlaceVar, Place.class, p -> p.getName().equals("clientsEntry"));
        PatternObject aliceVar = pb.buildPatternObject("Clara");
        pb.buildPatternLink(clientsEntryPlaceVar, "place", "clients", aliceVar);
        PatternObject bobVar = pb.buildPatternObject("Dora");
        pb.buildPatternLink(clientsEntryPlaceVar, "place", "clients", bobVar);

        rule.setRhs(pb.getPattern());


        reacher.withRule(rule);
    }

    int nextClientNum = 3;

    private void addClientsOp(Graph graph, ArrayList<Object> row) {
        System.out.println(row);
        Place clientsEntry = (Place) row.get(1);

        Client client = new Client().setName("client" + nextClientNum++);
        client.setPlace(clientsEntry);
        graph.objMap().put(client.getName(), client);
        client = new Client().setName("client" + nextClientNum++);
        client.setPlace(clientsEntry);
        graph.objMap().put(client.getName(), client);

        // create an Order and add it to
        graph.setLabel("addClients: " + clientsEntry.getClients());
    }

    private void releaseTableRule(Reacher reacher) {
        // offer rule
        PatternBuilder pb = FulibTables.patternBuilder();
        PatternObject vacatedTablesPlaceVar = pb.buildPatternObject("vacatedTables");
        pb.buildAttributeConstraint(vacatedTablesPlaceVar, Place.class, p -> p.getName().equals("vacatedTables"));
        PatternObject freeTablesPlaceVar = pb.buildPatternObject("freeTables");
        pb.buildAttributeConstraint(freeTablesPlaceVar, Place.class, p -> p.getName().equals("freeTables"));
        PatternObject tableVar = pb.buildPatternObject("table");
        pb.buildPatternLink(vacatedTablesPlaceVar, "place", "tables", tableVar);
        Rule rule = new Rule().setName("releaseTable").setPattern(pb.getPattern()).setOp(this::releaseTableOp);

        pb = FulibTables.patternBuilder();
        vacatedTablesPlaceVar = pb.buildPatternObject("vacatedTables");
        freeTablesPlaceVar = pb.buildPatternObject("freeTables");
        tableVar = pb.buildPatternObject("table");
        pb.buildPatternLink(freeTablesPlaceVar, "place", "tables", tableVar);
        rule.setRhs(pb.getPattern());

        reacher.withRule(rule);
    }

    private void releaseTableOp(Graph graph, ArrayList<Object> row) {
        // System.out.println(row);
        Place vacatedPlace = (Place) row.get(0);
        Table table = (Table) row.get(1);
        Place freePlace = (Place) row.get(2);

        table.setPlace(freePlace);

        // create an Order and add it to
        graph.setLabel("release: " + freePlace.getTables());
    }

    private void addLeaveRule(Reacher reacher) {
        // offer rule
        PatternBuilder pb = FulibTables.patternBuilder();
        PatternObject diningClientsPlaceVar = pb.buildPatternObject("diningClients");
        pb.buildAttributeConstraint(diningClientsPlaceVar, Place.class, p -> p.getName().equals("diningClients"));
        PatternObject vacatedTablesPlaceVar = pb.buildPatternObject("vacatedTables");
        pb.buildAttributeConstraint(vacatedTablesPlaceVar, Place.class, p -> p.getName().equals("vacatedTables"));
        PatternObject clientsEntryPlaceVar = pb.buildPatternObject("clientsEntry");
        pb.buildAttributeConstraint(clientsEntryPlaceVar, Place.class, p -> p.getName().equals("clientsEntry"));
        PatternObject orderVar = pb.buildPatternObject("order");
        pb.buildPatternLink(diningClientsPlaceVar, "place", "orders", orderVar);
        PatternObject tableVar = pb.buildPatternObject("table");
        PatternObject clientVar = pb.buildPatternObject("client");
        pb.buildPatternLink(orderVar, "order", "table", tableVar);
        pb.buildPatternLink(tableVar, "table", "client", clientVar);
        pb.buildPatternLink(diningClientsPlaceVar, "place", "tables", tableVar);
        pb.buildPatternLink(diningClientsPlaceVar, "place", "clients", clientVar);
        Rule rule = new Rule().setName("leave").setPattern(pb.getPattern()).setOp(this::leaveOp);
        rule.setPatternConstraint("eatMealItems(order)");
        pb = FulibTables.patternBuilder();
        diningClientsPlaceVar = pb.buildPatternObject("diningClients");
        vacatedTablesPlaceVar = pb.buildPatternObject("vacatedTables");
        clientsEntryPlaceVar = pb.buildPatternObject("clientsEntry");
        tableVar = pb.buildPatternObject("table");
        pb.buildPatternLink(vacatedTablesPlaceVar, "place", "tables", tableVar);
        rule.setRhs(pb.getPattern());
        reacher.withRule(rule);
    }

    private void leaveOp(Graph graph, ArrayList<Object> row) {
        // System.out.println(row);
        Order order = (Order) row.get(1);
        String orderName = order.getName();
        Place vacatedPlace = (Place) row.get(2);
        Place clientsEntryPlace = (Place) row.get(3);

        ArrayList<MealItem> mealItemsList = new ArrayList<>(order.getMealItems());
        for (MealItem mi : mealItemsList) {
            mi.removeYou();
            graph.objMap().remove(mi.getName());
        }

        Table t = order.getTable();
        t.setPlace(vacatedPlace);
        Client c = t.getClient();
        c.setTable(null);
        // c.setPlace(clientsEntryPlace);
        String clientName = c.getName().toLowerCase();
        c.removeYou();
        Object clientFromObjMap = graph.objMap().get(clientName);
        graph.objMap().remove(clientName);
        Object clientAfterRemove = graph.objMap().get(clientName);

        order.removeYou();
        Object orderInObjMap = graph.objMap().get(orderName);
        graph.objMap().remove(orderName);
        Object orderAfterRemove = graph.objMap().get(orderName);

        // create an Order and add it to
        graph.setLabel("leave: " + vacatedPlace.getTables());
    }

    private void handOverRule(Reacher reacher) {
        // offer rule
        PatternBuilder pb = FulibTables.patternBuilder();
        PatternObject pendingOrdersPlaceVar = pb.buildPatternObject("pendingOrders");
        pb.buildAttributeConstraint(pendingOrdersPlaceVar, Place.class, p -> p.getName().equals("pendingOrders"));
        PatternObject mealItemsPlaceVar = pb.buildPatternObject("mealItems");
        pb.buildAttributeConstraint(mealItemsPlaceVar, Place.class, p -> p.getName().equals("mealItems"));
        PatternObject waitingClientsPlaceVar = pb.buildPatternObject("waitingClients");
        pb.buildAttributeConstraint(waitingClientsPlaceVar, Place.class, p -> p.getName().equals("waitingClients"));
        PatternObject diningClientsPlaceVar = pb.buildPatternObject("diningClients");
        pb.buildAttributeConstraint(diningClientsPlaceVar, Place.class, p -> p.getName().equals("diningClients"));
        PatternObject orderVar = pb.buildPatternObject("order");
        PatternObject selectionVar = pb.buildPatternObject("selection");
        PatternObject tableVar = pb.buildPatternObject("table");
        PatternObject clientVar = pb.buildPatternObject("client");
        pb.buildPatternLink(pendingOrdersPlaceVar, "place", "orders", orderVar);
        pb.buildPatternLink(orderVar, "order", "selection", selectionVar);
        pb.buildPatternLink(waitingClientsPlaceVar, "place", "tables", tableVar);
        pb.buildPatternLink(waitingClientsPlaceVar, "place", "clients", clientVar);
        pb.buildPatternLink(tableVar, "table", "client", clientVar);
        pb.buildPatternLink(orderVar, "order", "table", tableVar);
        pb.buildMatchConstraint(this::handOverConstraint, orderVar, mealItemsPlaceVar);
        Rule rule = new Rule().setName("handOver").setPattern(pb.getPattern()).setOp(this::handOverOp);
        rule.setPatternConstraint("moveMealItemsToOrderItem(mealItems, selection)");


        pb = FulibTables.patternBuilder();
        pendingOrdersPlaceVar = pb.buildPatternObject("pendingOrders");
        mealItemsPlaceVar = pb.buildPatternObject("mealItems");
        waitingClientsPlaceVar = pb.buildPatternObject("waitingClients");
        diningClientsPlaceVar = pb.buildPatternObject("diningClients");
        orderVar = pb.buildPatternObject("order");
        selectionVar = pb.buildPatternObject("selection");
        tableVar = pb.buildPatternObject("table");
        clientVar = pb.buildPatternObject("client");
        pb.buildPatternLink(diningClientsPlaceVar, "place", "orders", orderVar);
        pb.buildPatternLink(orderVar, "order", "selection", selectionVar);
        pb.buildPatternLink(diningClientsPlaceVar, "place", "tables", tableVar);
        pb.buildPatternLink(diningClientsPlaceVar, "place", "clients", clientVar);
        pb.buildPatternLink(tableVar, "table", "client", clientVar);
        pb.buildPatternLink(orderVar, "order", "table", tableVar);
        rule.setRhs(pb.getPattern());

        reacher.withRule(rule);
    }

    private boolean handOverConstraint(Map<String, Object> map) {
        Order order = (Order) map.get("order");
        Place mealItems = (Place) map.get("mealItems");

        for (OrderItem orderItem : order.getSelection().getItems()) {
            ObjectTable<OrderItem> objectTable = new ObjectTable<>("orderItem", orderItem);
            ObjectTable<MealItem> mealItemTable = objectTable.expandAll("mealItem", oi -> oi.getMealItems());
            mealItemTable.filter(mi -> mi.getPlace() != null);
            // System.out.println(mealItemTable);
            if (mealItemTable.getTable().size() == 0) {
                return false;
            }
        }
        return true;
    }

    private void handOverOp(Graph graph, ArrayList<Object> row) {
        Order order = (Order) row.get(1);
        Place diningPlace = (Place) row.get(4);

        // select mealItems
        for (OrderItem orderItem : order.getSelection().getItems()) {
            ObjectTable<OrderItem> objectTable = new ObjectTable<>("orderItem", orderItem);
            ObjectTable<MealItem> mealItemTable = objectTable.expandAll("mealItem", oi -> oi.getMealItems());
            mealItemTable.filter(mi -> mi.getPlace() != null);
            ObjectTable<Place> mealItemsPlaceTable = mealItemTable.expand("mealItemsPlace", mi -> mi.getPlace());
            // System.out.println(mealItemsPlaceTable);
            MealItem mi = mealItemTable.toList().get(0);
            mi.setPlace(null);
            order.withMealItems(mi);
        }

        order.withoutPlace(order.getPlace());
        order.withPlace(diningPlace);
        order.getTable().setPlace(diningPlace);
        order.getTable().getClient().setPlace(diningPlace);

        // create an Order and add it to
        graph.setLabel("handOver: " + diningPlace.getTables());
    }

    private void addCookRule(Reacher reacher) {
        // offer rule
        PatternBuilder pb = FulibTables.patternBuilder();
        PatternObject orderItemsPlaceVar = pb.buildPatternObject("orderItems");
        pb.buildAttributeConstraint(orderItemsPlaceVar, Place.class, p -> p.getName().equals("orderItems"));
        PatternObject mealItemsPlaceVar = pb.buildPatternObject("mealItems");
        pb.buildAttributeConstraint(mealItemsPlaceVar, Place.class, p -> p.getName().equals("mealItems"));
        PatternObject itemRefVar = pb.buildPatternObject("itemRef");
        pb.buildPatternLink(orderItemsPlaceVar, "place", "itemRefs", itemRefVar);
        PatternObject orderItemVar = pb.buildPatternObject("orderItem");
        pb.buildPatternLink(itemRefVar, "itemRefs", "orderItem", orderItemVar);
        Rule rule = new Rule().setName("cook").setPattern(pb.getPattern()).setOp(this::cookOp);

        pb = FulibTables.patternBuilder();
        orderItemsPlaceVar = pb.buildPatternObject("orderItems");
        pb.buildAttributeConstraint(orderItemsPlaceVar, Place.class, p -> p.getName().equals("orderItems"));
        mealItemsPlaceVar = pb.buildPatternObject("mealItems");
        pb.buildAttributeConstraint(mealItemsPlaceVar, Place.class, p -> p.getName().equals("mealItems"));
        PatternObject mealItemVar = pb.buildPatternObject("mealItem");
        pb.buildPatternLink(mealItemsPlaceVar, "place", "mealItems", mealItemVar);
        orderItemVar = pb.buildPatternObject("orderItem");
        pb.buildPatternLink(mealItemVar, "mealItem", "orderItem", orderItemVar);
        rule.setRhs(pb.getPattern());


        reacher.withRule(rule);
    }

    int mealItemNum = 1;

    private void cookOp(Graph graph, ArrayList<Object> row) {
        ItemRef itemRef = (ItemRef) row.get(1);
        OrderItem orderItem = (OrderItem) row.get(2);
        Place mealItems = (Place) row.get(3);

        itemRef.removeYou();
        graph.objMap().remove(itemRef.getName());

        MealItem mi = new MealItem().setName("mi" + mealItemNum++)
                .setOrderItem(orderItem)
                .setPlace(mealItems);
        graph.objMap().put(mi.getName(), mi);

        // create an Order and add it to
        graph.setLabel("mealItems: " + mealItems.getMealItems());
    }

    private void addUnfoldRule(Reacher reacher) {
        // offer rule
        PatternBuilder pb = FulibTables.patternBuilder();
        PatternObject ordersPlaceVar = pb.buildPatternObject("orders");
        pb.buildAttributeConstraint(ordersPlaceVar, Place.class, p -> p.getName().equals("orders"));
        PatternObject orderItemsPlaceVar = pb.buildPatternObject("orderItems");
        pb.buildAttributeConstraint(orderItemsPlaceVar, Place.class, p -> p.getName().equals("orderItems"));
        PatternObject pendingOrdersPlaceVar = pb.buildPatternObject("pendingOrders");
        pb.buildAttributeConstraint(pendingOrdersPlaceVar, Place.class, p -> p.getName().equals("pendingOrders"));
        PatternObject orderVar = pb.buildPatternObject("order");
        PatternObject selectionVar = pb.buildPatternObject("selection");
        pb.buildPatternLink(ordersPlaceVar, "place", "orders", orderVar);
        pb.buildPatternLink(orderVar, "order", "selection", selectionVar);
        Rule rule = new Rule().setName("unfold").setPattern(pb.getPattern()).setOp(this::unfoldOp);
        rule.setPatternConstraint("copyAllItems(selection.getItems(), orderItems)");

        pb = FulibTables.patternBuilder();
        ordersPlaceVar = pb.buildPatternObject("orders");
        pb.buildAttributeConstraint(ordersPlaceVar, Place.class, p -> p.getName().equals("orders"));
        orderItemsPlaceVar = pb.buildPatternObject("orderItems");
        pb.buildAttributeConstraint(orderItemsPlaceVar, Place.class, p -> p.getName().equals("orderItems"));
        pendingOrdersPlaceVar = pb.buildPatternObject("pendingOrders");
        pb.buildAttributeConstraint(pendingOrdersPlaceVar, Place.class, p -> p.getName().equals("pendingOrders"));
        orderVar = pb.buildPatternObject("order");
        selectionVar = pb.buildPatternObject("selection");
        pb.buildPatternLink(pendingOrdersPlaceVar, "place", "orders", orderVar);
        pb.buildPatternLink(orderVar, "order", "selection", selectionVar);
        rule.setRhs(pb.getPattern());


        reacher.withRule(rule);
    }

    int itemRefNum = 1;

    private void unfoldOp(Graph graph, ArrayList<Object> row) {
        Place ordersPlace = (Place) row.get(0);
        Order order = (Order) row.get(1);
        Place orderItemsPlace = (Place) row.get(2);
        Place pendingPlace = (Place) row.get(3);

        ordersPlace.withoutOrders(order);

        for (OrderItem orderItem : order.getSelection().getItems()) {
            ItemRef ir = new ItemRef().setName("ir" + itemRefNum++)
                    .setOrderItem(orderItem)
                    .setPlace(orderItemsPlace);
            graph.objMap().put(ir.getName(), ir);
        }

        pendingPlace.withOrders(order);

        // create an Order and add it to
        graph.setLabel("pending: " + pendingPlace.getOrders());
    }

    private void addSelectRule(Reacher reacher) {
        // offer rule
        PatternBuilder pb = FulibTables.patternBuilder();
        PatternObject clientsReadyPlaceVar = pb.buildPatternObject("clientsReady");
        PatternObject menuPlaceVar = pb.buildPatternObject("menu");
        PatternObject ordersPlaceVar = pb.buildPatternObject("orders");
        PatternObject waitingClientsPlaceVar = pb.buildPatternObject("waitingClients");
        pb.buildAttributeConstraint(waitingClientsPlaceVar, Place.class, p -> p.getName().equals("waitingClients"));
        PatternObject tableVar = pb.buildPatternObject("table");
        PatternObject clientVar = pb.buildPatternObject("client");
        PatternObject selectionVar = pb.buildPatternObject("selection");
        pb.buildAttributeConstraint(menuPlaceVar, Place.class, p -> p.getName().equals("menu"));
        pb.buildAttributeConstraint(clientsReadyPlaceVar, Place.class, p -> p.getName().equals("clientsReady"));
        pb.buildAttributeConstraint(ordersPlaceVar, Place.class, p -> p.getName().equals("orders"));
        pb.buildPatternLink(clientsReadyPlaceVar, "place", "tables", tableVar);
        pb.buildPatternLink(menuPlaceVar, "place", "selections", selectionVar);
        pb.buildPatternLink(tableVar, "table", "client", clientVar);
        Rule selectRule = new Rule().setName("select").setPattern(pb.getPattern()).setOp(this::selectOp);

        pb = FulibTables.patternBuilder();
        clientsReadyPlaceVar = pb.buildPatternObject("clientsReady");
        menuPlaceVar = pb.buildPatternObject("menu");
        ordersPlaceVar = pb.buildPatternObject("orders");
        waitingClientsPlaceVar = pb.buildPatternObject("waitingClients");
        PatternObject order = pb.buildPatternObject("order_t1_m2");
        pb.buildAttributeConstraint(waitingClientsPlaceVar, Place.class, p -> p.getName().equals("waitingClients"));
        tableVar = pb.buildPatternObject("table");
        clientVar = pb.buildPatternObject("client");
        selectionVar = pb.buildPatternObject("selection");
        pb.buildPatternLink(menuPlaceVar, "place", "selections", selectionVar);
        pb.buildPatternLink(order, "order", "table", tableVar);
        pb.buildPatternLink(order, "order", "selection", selectionVar);
        pb.buildPatternLink(ordersPlaceVar, "place", "orders", order);
        pb.buildPatternLink(waitingClientsPlaceVar, "place", "orders", order);
        pb.buildPatternLink(waitingClientsPlaceVar, "place", "tables", tableVar);
        pb.buildPatternLink(waitingClientsPlaceVar, "place", "clients", clientVar);
        pb.buildPatternLink(tableVar, "table", "client", clientVar);
        // pb.buildPatternLink(waitingClientsPlaceVar, "place", "clients", );
        selectRule.setRhs(pb.getPattern());

        reacher.withRule(selectRule);
    }

    private void selectOp(Graph graph, ArrayList<Object> row) {
        Table t = (Table) row.get(1);
        Client c = t.getClient();
        Selection m = (Selection) row.get(3);
        Place orders = (Place) row.get(4);
        Place waitingClients = (Place) row.get(5);

        Order order = new Order().setName("order_" + t.getName() + m.getName());
        order.setTable(t);
        order.setSelection(m);
        order.withPlace(orders, waitingClients);
        graph.objMap().put(order.getName(), order);

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
