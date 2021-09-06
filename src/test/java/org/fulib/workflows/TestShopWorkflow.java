package org.fulib.workflows;

import org.fulib.FulibTools;
import org.fulib.builder.ClassModelManager;
import org.fulib.workflows.html.HtmlGenerator3;
import org.junit.Test;

public class TestShopWorkflow {

    private ClassModelManager mm;

    @Test
    public void testGenerateShopSystem() {
        mm = new ClassModelManager();
        mm.setMainJavaDir("test/src/main/java");
        mm.setPackageName("uks.debuggen.shop");

        // html
        HtmlGenerator3 generator = new HtmlGenerator3();
        generator.dumpObjectDiagram = (f, o) -> {
            FulibTools.objectDiagrams().dumpSVG(f, o);
        };
        String filename = "test/src/gen/resources/workflows/ShopWorkflow2.yaml";
        generator.generateViewFiles(filename, "Shop");

        // java
        WorkflowGenerator workflowGenerator = new WorkflowGenerator();
        workflowGenerator.dumpObjectDiagram = (o) -> {
            FulibTools.objectDiagrams().dumpSVG("tmp/Shop/shopboard.svg", o);
        };
        workflowGenerator.loadWorkflow(mm, filename);

        FulibTools.objectDiagrams().dumpSVG("tmp/Shop/ShopEventStormingModel.svg",
                workflowGenerator.getEventModel().getEventStormingBoard());

        workflowGenerator.generate();

        System.out.println();
    }
}
