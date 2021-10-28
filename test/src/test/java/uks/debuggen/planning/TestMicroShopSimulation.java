package uks.debuggen.planning;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.fulib.workflows.EventStormingBoard;
import org.fulib.workflows.SimulationBuilder;
import org.fulib.workflows.Workflow;
import org.fulib.workflows.html.HtmlGenerator3;
import org.junit.Test;

public class TestMicroShopSimulation {
    @Test
    public void genMicroShopSimulation() {
        SimulationBuilder simBuilder = new SimulationBuilder();

        EventStormingBoard board = simBuilder.addBoard("ShopSimulation");
        ArrayList<Workflow> workflowList = simBuilder.addWorkflows("products arrive 001", "products arrive 010", "001");
        simBuilder.addCommands("store products 08:00", "00:15");
        simBuilder.add("palette", "pal001", "001");
        simBuilder.addList("product", "red shoes", "red shoes", "red shoes", "blue jeans");
        simBuilder.addList("amount", "10", "10", "10", "8");

        simBuilder.addPolicies("Warehouse");

        simBuilder.addDatas("Palette 08:00:01", "00:15:00");

        new HtmlGenerator3().generateViewFiles4Board(board, board.getName());

        System.err.println();

    }
}
