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
    public void testMultiplePagesFromFile() {
        BoardGenerator boardGenerator = new BoardGenerator();
        boardGenerator.generateBoardFromFile(Path.of("src/gen/resources/pages.es.yaml"));
    }

    @Test
    public void testMultiplePagesFromString() {
        BoardGenerator boardGenerator = new BoardGenerator();

        try {
            String yamlContent = Files.readString(Path.of("src/gen/resources/pages.es.yaml"));
            boardGenerator.generateBoardFromString(yamlContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
    public void testMultipleWorkflowsFromString() {
        BoardGenerator boardGenerator = new BoardGenerator();

        try {
            String yaml = Files.readString(Path.of("src/gen/resources/workflows.es.yaml"));
            Map<String, String> map = boardGenerator.generateAndReturnHTMLsFromString(yaml);
            Assert.assertEquals(11, map.size()); // 1 Board, 4 Htmls, 4 Fxmls, 1 ObjectDiagram, 1 ClassDiagram
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testPMFromString() {
        BoardGenerator boardGenerator = new BoardGenerator();

        Path yamlFile = Path.of("src/gen/resources/pm.es.yaml");
        Map<String, String> map = boardGenerator.generateAndReturnHTMLsFromFile(yamlFile);
        Assert.assertEquals(10, map.size()); // 1 Board, 8 ObjectDiagrams, 1 ClassDiagram
    }

    @Test
    public void testPagesTabsFromString() {
        BoardGenerator boardGenerator = new BoardGenerator();
        boardGenerator.generateBoardFromString(pagesWithTabs());
    }

    // Helper
    private String pagesWithTabs() {
        return "- workflow: Pages\n" +
                "\n" +
                "- page:\n" +
                "\t- pageName: Register\n" +
                "\t\t- text: Please register yourself\n" +
                "\t\t- input: E-Mail\n" +
                "\t\t- input: Username\n" +
                "\t- password: Password\n" +
                "\t- password: Repeat Password\n" +
                "\t\t- button: Register\n" +
                "\n" +
                "- page:\n" +
                "    - pageName: Login\n" +
                "    - text: Welcome back\n" +
                "    - input: Username/E-Mail\n" +
                "    - password: Password\n" +
                "    - button: Login\n" +
                "\n" +
                "- page:\n" +
                "    - pageName: Overview\n" +
                "    - text: Your current Purchases\n" +
                "    - button: Add Purchase\n";
    }
}
