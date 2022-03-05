package org.fulib.workflows.generators;

import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;

public class FalseInputTest {
    @Test
    public void testFalsePage() {
        BoardGenerator boardGenerator = new BoardGenerator();

        try {
            String yamlContent = Files.readString(Path.of("src/gen/resources/examples/falseData/falsePageNote.es.yaml"));
            boardGenerator.generateBoardFromString(yamlContent);
        } catch (Exception e) {
            // TODO Assert message of exception
        }
    }
}
