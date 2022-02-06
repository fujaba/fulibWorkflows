package org.fulib.workflows.generators;

import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Path;
import java.util.Map;

public class StringGenerationTest {
    @Test
    public void testEventsWorkflow() {
        BoardGenerator boardGenerator = new BoardGenerator();
        Path yamlFile = Path.of("src/gen/resources/examples/events.es.yaml");
        Map<String, String> map = boardGenerator.generateAndReturnHTMLsFromFile(yamlFile);
        Assert.assertEquals(1, map.size()); // 1 Board
    }

    @Test
    public void testRegistration() {
        BoardGenerator boardGenerator = new BoardGenerator();
        Path yamlFile = Path.of("src/gen/resources/examples/register.es.yaml");
        Map<String, String> map = boardGenerator.generateAndReturnHTMLsFromFile(yamlFile);
        Assert.assertEquals(5, map.size()); // 1 Board, 1 Page (HTML), 1 Page (FXML), 1 ObjectDiagrams, 1 ClassDiagram
    }

    @Test
    public void testTypes() {
        BoardGenerator boardGenerator = new BoardGenerator();
        Path yamlFile = Path.of("src/gen/resources/examples/types.es.yaml");
        Map<String, String> map = boardGenerator.generateAndReturnHTMLsFromFile(yamlFile);
        Assert.assertEquals(5, map.size()); // 1 Board, 1 Page (HTML), 1 Page (FXML), 1 ObjectDiagrams, 1 ClassDiagram
    }

    @Test
    public void testOneWorkflow() {
        BoardGenerator boardGenerator = new BoardGenerator();
        Path yamlFile = Path.of("src/gen/resources/examples/workflow.es.yaml");
        Map<String, String> map = boardGenerator.generateAndReturnHTMLsFromFile(yamlFile);
        Assert.assertEquals(5, map.size()); // 1 Board, 1 Page (HTML), 1 Page (FXML), 1 ObjectDiagrams, 1 ClassDiagram
    }

    @Test
    public void testMultipleWorkflows() {
        BoardGenerator boardGenerator = new BoardGenerator();
        Path yamlFile = Path.of("src/gen/resources/examples/workflows.es.yaml");
        Map<String, String> map = boardGenerator.generateAndReturnHTMLsFromFile(yamlFile);
        Assert.assertEquals(11, map.size()); // 1 Board, 4 Pages (HTML), 4 Pages (FXML), 1 ObjectDiagrams, 1 ClassDiagram
    }

    @Test
    public void testMultiplePagesFromFile() {
        BoardGenerator boardGenerator = new BoardGenerator();
        Path yamlFile = Path.of("src/gen/resources/examples/webeditor/pages.es.yaml");
        Map<String, String> map = boardGenerator.generateAndReturnHTMLsFromFile(yamlFile);
        Assert.assertEquals(25, map.size()); // 1 Board, 12 Pages (HTML), 12 Pages (FXML)
    }
}
