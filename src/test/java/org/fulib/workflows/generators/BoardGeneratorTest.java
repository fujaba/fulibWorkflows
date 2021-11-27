package org.fulib.workflows.generators;

import org.junit.Test;

import java.nio.file.Path;

public class BoardGeneratorTest {

    @Test
    public void testFileImport() {
        BoardGenerator dataImporter = new BoardGenerator();
        dataImporter.generateBoardFromFile(Path.of("src/gen/resources/workflow.es.yaml"));
    }

    @Test
    public void testStringImport() {
        String yaml = """
                - workflow: Testerino
                                
                - event: do something
                  testerino: swasda
                  gasdfwad: asfasd
                  wadasda: wadsdw
                  wadfasgfaqs: asdagdg
                  Wbgsdfaa: adawdas
                                
                - page:
                    - name: Testerino
                    - label: This is a test page
                    - input: Username
                    - input: Password
                    - button: Login
                """;
        
        BoardGenerator dataImporter = new BoardGenerator();
        dataImporter.generateBoardFromString(yaml);
    }
}
