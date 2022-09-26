package org.fulib.workflows.generators;

import org.junit.Test;
import org.junit.runner.JUnitCore;

import java.nio.file.Path;

public class HamburgDataTest {

    public static void main(String[] args) {
        JUnitCore.main(HamburgDataTest.class.getName());
    }
    @Test
    public void testOneWorkflow() {
        BoardGenerator boardGenerator = new BoardGenerator();
        boardGenerator.generateBoardFromFile(Path.of("src/gen/resources/hamburgData.es.yaml"));
    }
}
