package org.fulib.workflows.generators;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class GenerationTest {
    // From File
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
    public void testRegistration() {
        BoardGenerator boardGenerator = new BoardGenerator();
        boardGenerator.generateBoardFromFile(Path.of("src/gen/resources/examples/register.es.yaml"));
    }

    @Test
    public void testTypes() {
        BoardGenerator boardGenerator = new BoardGenerator();
        boardGenerator.generateBoardFromFile(Path.of("src/gen/resources/examples/types.es.yaml"));
    }

    @Test
    public void testOneWorkflow() {
        BoardGenerator boardGenerator = new BoardGenerator();
        boardGenerator.generateBoardFromFile(Path.of("src/gen/resources/examples/workflow.es.yaml"));
    }

    @Test
    public void testMultipleWorkflows() {
        BoardGenerator boardGenerator = new BoardGenerator();
        boardGenerator.generateBoardFromFile(Path.of("src/gen/resources/examples/workflows.es.yaml"));
    }

    @Test
    public void testMultiplePagesFromFile() {
        BoardGenerator boardGenerator = new BoardGenerator();
        boardGenerator.setWebGeneration(true);
        boardGenerator.generateBoardFromFile(Path.of("src/gen/resources/examples/webeditor/pages.es.yaml"));
    }

    @Test
    public void testDataWithListFromFile() {
        BoardGenerator boardGenerator = new BoardGenerator();
        boardGenerator.setWebGeneration(true);
        boardGenerator.generateBoardFromFile(Path.of("src/gen/resources/examples/dataWithList.es.yaml"));
    }

    @Test
    public void testDataModellingFromFile() {
        BoardGenerator boardGenerator = new BoardGenerator();
        boardGenerator.setWebGeneration(true);
        boardGenerator.generateBoardFromFile(Path.of("src/gen/resources/examples/webeditor/dataModelling.es.yaml"));
    }

    // From String
    @Test
    public void testEventsWorkflowFromString() {
        BoardGenerator boardGenerator = new BoardGenerator();
        try {
            String yamlContent = Files.readString(Path.of("src/gen/resources/examples/events.es.yaml"));
            boardGenerator.generateBoardFromString(yamlContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testFalseDataWorkflowFromString() {
        BoardGenerator boardGenerator = new BoardGenerator();

        try {
            String yamlContent = Files.readString(Path.of("src/gen/resources/examples/falseData.es.yaml"));
            boardGenerator.generateBoardFromString(yamlContent);
        } catch (Exception e) {
            // Is a valid check, because the root exception is an IndexOutOfBoundsException
            Assert.assertEquals("begin 1, end 0, length 0", e.getMessage());
        }
    }

    @Test
    public void testRegistrationFromString() {
        BoardGenerator boardGenerator = new BoardGenerator();
        try {
            String yamlContent = Files.readString(Path.of("src/gen/resources/examples/register.es.yaml"));
            boardGenerator.generateBoardFromString(yamlContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testTypesFromString() {
        BoardGenerator boardGenerator = new BoardGenerator();
        try {
            String yamlContent = Files.readString(Path.of("src/gen/resources/examples/types.es.yaml"));
            boardGenerator.generateBoardFromString(yamlContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testOneWorkflowFromString() {
        BoardGenerator boardGenerator = new BoardGenerator();
        try {
            String yamlContent = Files.readString(Path.of("src/gen/resources/examples/workflow.es.yaml"));
            boardGenerator.generateBoardFromString(yamlContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testMultipleWorkflowsFromString() {
        BoardGenerator boardGenerator = new BoardGenerator();
        try {
            String yamlContent = Files.readString(Path.of("src/gen/resources/examples/workflows.es.yaml"));
            boardGenerator.generateBoardFromString(yamlContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testMultiplePagesFromFileFromString() {
        BoardGenerator boardGenerator = new BoardGenerator();
        try {
            String yamlContent = Files.readString(Path.of("src/gen/resources/examples/webeditor/pages.es.yaml"));
            boardGenerator.generateBoardFromString(yamlContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testDataWithListFromString() {
        BoardGenerator boardGenerator = new BoardGenerator();
        try {
            String yamlContent = Files.readString(Path.of("src/gen/resources/examples/dataWithList.es.yaml"));
            boardGenerator.generateBoardFromString(yamlContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
