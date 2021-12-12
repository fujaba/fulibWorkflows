package org.fulib.workflows.generators;

import org.fulib.workflows.events.Board;
import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Path;
import java.util.Map;

public class BoardGeneratorTest {

    @Test
    public void testAntlrOneWorkflow() {
        BoardGenerator boardGenerator = new BoardGenerator();
        boardGenerator.generateBoardFromFileViaANTLR(Path.of("src/gen/resources/workflow.es.yaml"));
    }

    @Test
    public void testAntlrMultiplePages() {
        BoardGenerator boardGenerator = new BoardGenerator();
        boardGenerator.generateBoardFromFileViaANTLR(Path.of("src/gen/resources/pages.es.yaml"));
    }

    @Test
    public void testAntlrMultipleWorkflows() {
        BoardGenerator boardGenerator = new BoardGenerator();
        boardGenerator.generateBoardFromFileViaANTLR(Path.of("src/gen/resources/workflows.es.yaml"));
    }

    @Test
    public void testFileImport() {
        BoardGenerator dataImporter = new BoardGenerator();
        dataImporter.generateBoardFromFile(Path.of("src/gen/resources/workflow.es.yaml"));
    }

    @Test
    public void testPageExport() {
        BoardGenerator dataImporter = new BoardGenerator();
        dataImporter.generateBoardFromFile(Path.of("src/gen/resources/pages.es.yaml"));
    }

    @Test
    public void testStringImport() {
        String yaml = getYamlString();

        BoardGenerator dataImporter = new BoardGenerator();
        Board board = dataImporter.generateBoardFromString(yaml);

        Assert.assertEquals(10, board.getWorkflows().get(0).getNotes().size());
    }

    @Test
    public void testHTMLGeneration() {
        String yaml = getYamlString();

        BoardGenerator dataImporter = new BoardGenerator();
        Map<String, String> htmlMap = dataImporter.generateAndReturnHTMLsFromString(yaml);

        Assert.assertEquals(2, htmlMap.size());
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
                
                - problem: This part is bulls***
                """;
    }
}
