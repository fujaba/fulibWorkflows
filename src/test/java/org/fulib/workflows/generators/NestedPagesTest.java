package org.fulib.workflows.generators;

import java.nio.file.Path;

import org.junit.Test;
import org.junit.runner.JUnitCore;

public class NestedPagesTest {

    public static void main(String[] args) {
        JUnitCore.main(NestedPagesTest.class.getName());
    }
    @Test
    public void testOneWorkflow() {
        BoardGenerator boardGenerator = new BoardGenerator().setWebGeneration(true);
        boardGenerator.generateBoardFromFile(Path.of("src/gen/resources/nestedPages.es.yaml"));
    }
}
