package org.fulib.workflows.generators;

import org.junit.Test;

import java.nio.file.Path;

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
}
