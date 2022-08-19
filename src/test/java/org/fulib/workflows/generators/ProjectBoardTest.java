package org.fulib.workflows.generators;

import org.junit.Test;
import org.junit.runner.JUnitCore;

import java.nio.file.Path;

public class ProjectBoardTest
{

    public static void main(String[] args) {
        JUnitCore.main(ProjectBoardTest.class.getName());
    }

    @Test
    public void testOneWorkflow() {
        BoardGenerator boardGenerator = new BoardGenerator().setWebGeneration(true);
        boardGenerator.generateBoardFromFile(Path.of("src/gen/resources/projectBoard.es.yaml"));
    }
}
