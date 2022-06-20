package org.fulib.tools;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Test;
import org.junit.runner.JUnitCore;

public class GraphDiagramTest {
    public static void main(String[] args) {
        JUnitCore.runClasses(GraphDiagramTest.class);
    }

    @Test
    public void drawDiag() throws IOException {
        GraphDiagram diag = new GraphDiagram();

        diag.addGraph("StudyGuide", "StudyGuide Service");

        diag.addNode("carli", "carli : Student", "motivation = 83\n");
        diag.addEdge("carli", "stops", "stop1", "student");
        diag.addNode("stop1", "stop1 : Stop", "motivation = 66\n");
        diag.addEdge("stop1", "room", "r1", "stops");
        diag.addNode("r1", "r1 : Room", "topic = math\n"
        + "credits = 17\n");
        diag.addEdge("r1", "neighbors", "r2", "neighbors");
        diag.addEdge("r1", "neighbors", "r4", "neighbors");
        diag.addNode("r2", "r2 : Room", "topic = calculus\n"
        + "credits = 20\n");
        diag.addNode("r2", "r2 : Room", "topic = calculus\n"
        + "credits = 20\n");
        diag.addEdge("r2", "neighbors", "r1", "neighbors");
        diag.addEdge("r2", "neighbors", "r4", "neighbors");
        diag.addNode("r3", "r3 : Room", "topic = exam\n");
        diag.addEdge("r3", "neighbors", "r4", "neighbors");
        diag.addNode("r4", "r4 : Room", "topic = modeling\n"
        + "credits = 29");
        diag.addEdge("r4", "neighbors", "r1", "neighbors");
        diag.addEdge("r4", "neighbors", "r2", "neighbors");
        diag.addEdge("r4", "neighbors", "r3", "neighbors");

        diag.addGraph("Warehouse", "Warehouse Service");
        diag.addNode("p1", "p1 : Palette", "barcode = b001\n"
        + "amount = 10\n");
        diag.addEdge("p1", "product", "wPro1", "palettes");
        diag.addEdge("p1", "location", "shelf_42", "palettes");
        diag.addNode("wPro1", "wPro1 : WHProduct", "name = red shoes\namount = 10\n");
        diag.addNode("shelf_42", "shelf_42 : Shelf");

        diag.addGraph("MicroShop", "MicroShop Service");
        diag.addNode("mPro1", "mPro1 : MSProduct", "itemName = red shoes\n"
        + "amount = 10\n"
        + "state = in stock\n");

        diag.addGraph("Warehouse", "Warehouse Service");
        diag.addNode("p2", "p2 : Palette", "barcode = b002\n"
        + "amount = 8\n");
        diag.addEdge("p2", "product", "wPro1", "palettes");
        diag.addEdge("p2", "location", "shelf_23", "palettes");
        diag.addNode("wPro1", "wPro1 : WHProduct", "name = red shoes\namount = 18\n");
        diag.addNode("shelf_23", "shelf_23 : Shelf");

        diag.addGraph("MicroShop", "MicroShop Service");
        diag.addNode("mPro1", "mPro1 : MSProduct", "itemName = red shoes\n"
        + "amount = 18\n"
        + "state = in stock\n");

        diag.addGraph("Warehouse", "Warehouse Service");
        diag.addNode("p3", "p3 : Palette", "barcode = b003\n"
        + "amount = 6\n");
        diag.addEdge("p3", "product", "wPro3", "palettes");
        diag.addEdge("p3", "location", "shelf_23", "palettes");
        diag.addNode("wPro3", "wPro3 : WHProduct", "name = blue jeans\namount = 6\n");

        diag.addGraph("MicroShop", "MicroShop Service");
        diag.addNode("mPro3", "mPro3 : MSProduct", "itemName = blue jeans\n"
        + "amount = 6\n"
        + "state = in stock\n");

        diag.addGraph("MicroShop", "MicroShop Service");
        diag.addNode("mPro1", "mPro1 : MSProduct", "itemName = red shoes\n"
        + "amount = 18\n"
        + "state = in stock\n"
        + "<b>price = 42</b>\n");

        diag.addGraph("MicroShop", "MicroShop Service");
        diag.addNode("mPro3", "mPro3 : MSProduct", "itemName = blue jeans\n"
        + "amount = 6\n"
        + "state = in stock\n"
        + "<b>price = 63</b>\n");

        String svg = diag.toSVG();

        Files.createDirectories(Path.of("tmp"));
        Files.writeString(Path.of("tmp/jsondiag.svg"), svg);

    }
}
