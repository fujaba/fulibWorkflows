package org.fulib.workflows.generators;

import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Path;
import java.util.Map;

public class WebEditorExampleTest {

    @Test
    public void testDataModelling() {
        BoardGenerator boardGenerator = new BoardGenerator();

        Path yamlFile = Path.of("src/gen/resources/examples/webeditor/dataModelling.es.yaml");
        Map<String, String> map = boardGenerator.generateAndReturnHTMLsFromFile(yamlFile);
        Assert.assertEquals(10, map.size()); // 1 Board, 8 ObjectDiagrams, 1 ClassDiagram
    }

    @Test
    public void testMicroservices() {
        BoardGenerator boardGenerator = new BoardGenerator();

        Path yamlFile = Path.of("src/gen/resources/examples/webeditor/microservices.es.yaml");
        Map<String, String> map = boardGenerator.generateAndReturnHTMLsFromFile(yamlFile);

        // 1 Board, 22 Pages (HTML), 22 Pages (FXML), 21 ObjectDiagrams, 1 ClassDiagram
        Assert.assertEquals(67, map.size());
    }

    @Test
    public void testPages() {
        BoardGenerator boardGenerator = new BoardGenerator();

        Path yamlFile = Path.of("src/gen/resources/examples/webeditor/pages.es.yaml");
        Map<String, String> map = boardGenerator.generateAndReturnHTMLsFromFile(yamlFile);
        Assert.assertEquals(17, map.size()); // 1 Board, 8 Pages (HTML), 8 Pages (FXML)
    }
}
