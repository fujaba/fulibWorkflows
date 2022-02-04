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
        boardGenerator.generateBoardFromFile(Path.of("src/gen/resources/examples/workflow.es.yaml"));
    }

    @Test
    public void testRegistration() {
        BoardGenerator boardGenerator = new BoardGenerator();
        boardGenerator.generateBoardFromFile(Path.of("src/gen/resources/examples/register.es.yaml"));
    }

    @Test
    public void testEventsWorkflow() {
        BoardGenerator boardGenerator = new BoardGenerator();
        boardGenerator.generateBoardFromFile(Path.of("src/gen/resources/examples/events.es.yaml"));
    }

    @Test
    public void testFalseDataWorkflow() {
        BoardGenerator boardGenerator = new BoardGenerator();

        try {
            boardGenerator.generateBoardFromFile(Path.of("src/gen/resources/examples/falseData.es.yaml"));
        } catch (Exception e) {
            // Is a valid check, because the root exception is an IndexOutOfBoundsException
            Assert.assertEquals("begin 1, end 0, length 0", e.getMessage());
        }
    }

    @Test
    public void testMultiplePagesFromFile() {
        BoardGenerator boardGenerator = new BoardGenerator();
        boardGenerator.generateBoardFromFile(Path.of("src/gen/resources/examples/pages.es.yaml"));
    }

    @Test
    public void testMultiplePagesFromString() {
        BoardGenerator boardGenerator = new BoardGenerator();

        try {
            String yamlContent = Files.readString(Path.of("src/gen/resources/examples/pages.es.yaml"));
            boardGenerator.generateBoardFromString(yamlContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testMultipleWorkflows() {
        BoardGenerator boardGenerator = new BoardGenerator();
        boardGenerator.generateBoardFromFile(Path.of("src/gen/resources/examples/workflows.es.yaml"));
    }

    @Test
    public void testObjectDiagrams() {
        BoardGenerator boardGenerator = new BoardGenerator();
        boardGenerator.generateBoardFromFile(Path.of("src/gen/resources/examples/pm.es.yaml"));
    }

    @Test
    public void testTypesWorkflow() {
        BoardGenerator boardGenerator = new BoardGenerator();
        boardGenerator.generateBoardFromFile(Path.of("src/gen/resources/examples/types.es.yaml"));
    }

    @Test
    public void testMultipleWorkflowsFromString() {
        BoardGenerator boardGenerator = new BoardGenerator();

        try {
            String yaml = Files.readString(Path.of("src/gen/resources/examples/workflows.es.yaml"));
            Map<String, String> map = boardGenerator.generateAndReturnHTMLsFromString(yaml);
            Assert.assertEquals(11, map.size()); // 1 Board, 4 Htmls, 4 Fxmls, 1 ObjectDiagram, 1 ClassDiagram
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testPMFromString() {
        BoardGenerator boardGenerator = new BoardGenerator();

        Path yamlFile = Path.of("src/gen/resources/examples/pm.es.yaml");
        Map<String, String> map = boardGenerator.generateAndReturnHTMLsFromFile(yamlFile);
        Assert.assertEquals(10, map.size()); // 1 Board, 8 ObjectDiagrams, 1 ClassDiagram
    }
}
