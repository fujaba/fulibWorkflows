package org.fulib.workflows;

import org.fulib.FulibTools;
import org.fulib.builder.ClassModelManager;
import org.fulib.workflows.html.HtmlGenerator3;
import org.junit.Test;

public class TestPartyWorkflow {

    private ClassModelManager mm;

    @Test
    public void testGeneratePartySystemMigration() {
        mm = new ClassModelManager();
        mm.setMainJavaDir("test/src/main/java");
        mm.setPackageName("uks.debuggen.party2");

        String fileName = "test/src/gen/resources/workflows/PartyWorkflow2.es.yaml";

        // html
        HtmlGenerator3 generator = new HtmlGenerator3();
        generator.dumpObjectDiagram = (f, o) -> {
            FulibTools.objectDiagrams().dumpSVG(f, o);
        };
        generator.generateViewFiles(fileName, "Party2");

        // java
        WorkflowGenerator workflowGenerator = new WorkflowGenerator();
        workflowGenerator.dumpObjectDiagram = (o) -> {
            FulibTools.objectDiagrams().dumpSVG("tmp/Party2/PartyBoard2.svg", o);
        };
        workflowGenerator.loadWorkflow(mm, fileName);

        FulibTools.objectDiagrams().dumpSVG("tmp/Party2/PartyEventStormingModel2.svg",
                workflowGenerator.getEventModel().getEventStormingBoard());

        workflowGenerator.generate();

        System.out.println();
    }

    @Test
    public void testGeneratePartySystem() {
        mm = new ClassModelManager();
        mm.setMainJavaDir("test/src/main/java");
        mm.setPackageName("uks.debuggen.party");

        String fileName = "test/src/gen/resources/workflows/PartyWorkflow.es.yaml";

        // html
        HtmlGenerator3 generator = new HtmlGenerator3();
        generator.dumpObjectDiagram = (f, o) -> {
            FulibTools.objectDiagrams().dumpSVG(f, o);
        };
        generator.generateViewFiles(fileName, "Party");

        // java
        WorkflowGenerator workflowGenerator = new WorkflowGenerator();
        workflowGenerator.dumpObjectDiagram = (o) -> {
            FulibTools.objectDiagrams().dumpSVG("tmp/Party/PartyBoard.svg", o);
        };
        workflowGenerator.loadWorkflow(mm, fileName);

        FulibTools.objectDiagrams().dumpSVG("tmp/Party/PartyEventStormingModel.svg",
                workflowGenerator.getEventModel().getEventStormingBoard());

        workflowGenerator.generate();

        System.out.println();
    }
}
