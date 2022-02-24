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

        String svg = diag.toSVG();

        Files.writeString(Path.of("tmp/jsondiag.svg"), svg);

    }
}
