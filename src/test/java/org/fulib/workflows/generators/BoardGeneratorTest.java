package org.fulib.workflows.generators;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class BoardGeneratorTest {

    @Test
    public void testAntlrOneWorkflow() {
        BoardGenerator boardGenerator = new BoardGenerator();
        boardGenerator.generateBoardFromFile(Path.of("src/gen/resources/workflow.es.yaml"));
    }

    @Test
    public void testAntlrMultiplePages() {
        BoardGenerator boardGenerator = new BoardGenerator();
        boardGenerator.generateBoardFromFile(Path.of("src/gen/resources/pages.es.yaml"));
    }

    @Test
    public void testAntlrMultipleWorkflows() {
        BoardGenerator boardGenerator = new BoardGenerator();
        boardGenerator.generateBoardFromFile(Path.of("src/gen/resources/workflows.es.yaml"));
    }

    @Test
    public void testAntlrMultipleWorkflowsFromString() {
        BoardGenerator boardGenerator = new BoardGenerator();

        try {
            String yaml = Files.readString(Path.of("src/gen/resources/workflows.es.yaml"));
            Map<String, String> map = boardGenerator.generateAndReturnHTMLs(yaml);
            Assert.assertEquals(5, map.size());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
