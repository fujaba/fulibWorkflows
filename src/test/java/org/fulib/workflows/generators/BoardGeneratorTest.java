package org.fulib.workflows.generators;

import org.junit.Test;

import java.nio.file.Path;

public class BoardGeneratorTest {

    @Test
    public void testDataImporter() {
        BoardGenerator dataImporter = new BoardGenerator();
        dataImporter.generateBoardFromFile(Path.of("src/gen/resources/workflow.es.yaml"));
    }
}
