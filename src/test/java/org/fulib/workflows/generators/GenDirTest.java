package org.fulib.workflows.generators;

import org.junit.Test;

import java.nio.file.Path;

public class GenDirTest {
    @Test
    public void testWebGenDir() {
        BoardGenerator boardGenerator = new BoardGenerator()
                .setWebGeneration(true)
                .setGenDir("temp/MicroservicesExample");

        Path yamlFile = Path.of("src/gen/resources/examples/webeditor/microservices.es.yaml");
        boardGenerator.generateBoardFromFile(yamlFile);
    }
}
