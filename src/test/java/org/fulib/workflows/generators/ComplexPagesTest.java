package org.fulib.workflows.generators;

import java.nio.file.Path;

import org.junit.Test;
import org.junit.runner.JUnitCore;

public class ComplexPagesTest {

    public static void main(String[] args) {
        JUnitCore.main(ComplexPagesTest.class.getName());
    }
    @Test
    public void testOneWorkflow() {
        BoardGenerator boardGenerator = new BoardGenerator().setStandAlone();
        boardGenerator.generateBoardFromFile(Path.of("src/gen/resources/complexPages.es.yaml"));
    }
}
