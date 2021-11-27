package org.fulib.workflows.generators;

import org.fulib.workflows.events.Board;
import org.junit.Assert;
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
        String yaml = getYamlString();

        BoardGenerator dataImporter = new BoardGenerator();
        Board board = dataImporter.generateBoardFromString(yaml);

        Assert.assertEquals(9, board.getWorkflows().get(0).getNotes().size());
    }

    private String getYamlString() {
        return """
                - workflow: Testerino
                                
                - externalSystem: höhöhöhö
                                
                - service: TestinService
                                
                - command: Commanderino started
                                
                - event: do something
                  testerino: swasda
                  gasdfwad: asfasd
                  wadasda: wadsdw
                  wadfasgfaqs: asdagdg
                  Wbgsdfaa: adawdas
                                
                - policy: start do something process
                                
                - user: Bob
                                
                - class: Wambo
                  lecture: string
                  students: int
                                
                - data: wambologieClass
                  lecture: wambo
                  studentes: 31
                                
                - page:
                    - name: Testerino
                    - text: This is a test page
                    - input: E-Mail
                    - password: Password
                    - button: Login
                """;
    }
}
