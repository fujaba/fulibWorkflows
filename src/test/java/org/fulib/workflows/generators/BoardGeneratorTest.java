package org.fulib.workflows.generators;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class BoardGeneratorTest {

    @Test
    public void testOneWorkflow() {
        BoardGenerator boardGenerator = new BoardGenerator();
        boardGenerator.generateBoardFromFile(Path.of("src/gen/resources/workflow.es.yaml"));
    }

    @Test
    public void testMultiplePages() {
        BoardGenerator boardGenerator = new BoardGenerator();
        boardGenerator.generateBoardFromFile(Path.of("src/gen/resources/pages.es.yaml"));
    }

    @Test
    public void testMultipleWorkflows() {
        BoardGenerator boardGenerator = new BoardGenerator();
        boardGenerator.generateBoardFromFile(Path.of("src/gen/resources/workflows.es.yaml"));
    }

    @Test
    public void testObjectDiagrams() {
        BoardGenerator boardGenerator = new BoardGenerator();
        boardGenerator.generateBoardFromFile(Path.of("src/gen/resources/pm.es.yaml"));
    }

    @Test
    public void testAntlrMultipleWorkflowsFromString() {
        BoardGenerator boardGenerator = new BoardGenerator();

        try {
            String yaml = Files.readString(Path.of("src/gen/resources/workflows.es.yaml"));
            Map<String, String> map = boardGenerator.generateAndReturnHTMLs(yaml);
            Assert.assertEquals(9, map.size()); // 1 Board, 4 Htmls, 4 Fxmls
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
