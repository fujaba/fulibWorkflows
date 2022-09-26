package org.fulib.workflows.generators;

import org.junit.Test;
import org.junit.runner.JUnitCore;

import java.nio.file.Path;

public class ComplexPagesTest {

    public static void main(String[] args) {
        JUnitCore.main(ComplexPagesTest.class.getName());
    }
    @Test
    public void testOneWorkflow() {
        BoardGenerator boardGenerator = new BoardGenerator();
        boardGenerator.generateBoardFromFile(Path.of("src/gen/resources/complexPages.es.yaml"));
    }
}
