package org.fulib.workflows;

import org.fulib.FulibTools;
import org.fulib.builder.ClassModelManager;
import org.junit.Test;

public class TestGenerator {

    private ClassModelManager mm;

    @Test
    public void testStartGenerator() {
        mm = new ClassModelManager();
        mm.setMainJavaDir("test/src/main/java");
        mm.setPackageName("uks.debuggen.shop");

        WorkflowGenerator workflowGenerator = new WorkflowGenerator();
        workflowGenerator.dumpObjectDiagram = (o) -> {
            FulibTools.objectDiagrams().dumpSVG("tmp/Shop/shopboard.svg", o);
        };
        workflowGenerator.loadWorkflow(mm, "test/src/gen/resources/workflows/ShopWorkflow2.yaml");

        FulibTools.objectDiagrams().dumpSVG("tmp/Shop/EventStorming.svg",
                workflowGenerator.getEventModel().getEventStormingBoard());

        workflowGenerator.generate();

        System.out.println();
    }

    @Test
    public void testGeneratorWithPages() {
        mm = new ClassModelManager();
        mm.setMainJavaDir("test/src/main/java");
        mm.setPackageName("uks.debuggen.page");

        WorkflowGenerator workflowGenerator = new WorkflowGenerator();
        workflowGenerator.dumpObjectDiagram = (o) -> {
            FulibTools.objectDiagrams().dumpSVG("tmp/Shop/guiboard.svg", o);
        };
        workflowGenerator.loadWorkflow(mm, "test/src/gen/resources/workflows/GUI.yaml");

        FulibTools.objectDiagrams().dumpSVG("tmp/Shop/ShopEventStorming.svg",
                workflowGenerator.getEventModel().getEventStormingBoard());

        workflowGenerator.generate();

        System.out.println();
    }
}
