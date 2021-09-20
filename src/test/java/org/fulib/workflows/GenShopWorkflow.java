package org.fulib.workflows;

import org.fulib.FulibTools;
import org.fulib.builder.ClassModelManager;
import org.fulib.workflows.html.HtmlGenerator3;
import org.junit.Test;

public class GenShopWorkflow {

    private ClassModelManager mm;

    @Test
    public void generateShopSystem() {
        mm = new ClassModelManager();
        mm.setMainJavaDir("test/src/main/java");
        mm.setPackageName("uks.debuggen.microshop");

        // html
        HtmlGenerator3 generator = new HtmlGenerator3();
        generator.generateViewFiles("test/src/gen/resources/workflows/shop/ShopFirstEventStorming.es.yaml", "FirstShopBoard");
        generator.generateViewFiles("test/src/gen/resources/workflows/shop/ShopRefinedProductStoredEventStorming.es.yaml", "StoreProductShopBoard");

        String filename = "test/src/gen/resources/shop/ShopWorkflow2.es.yaml";
        generator.generateViewFiles(filename, "MicroShop");

        // java
        WorkflowGenerator workflowGenerator = new WorkflowGenerator();
        workflowGenerator.dumpObjectDiagram = (o) -> {
            FulibTools.objectDiagrams().dumpSVG("tmp/MicroShop/MicroShopboard.svg", o);
        };
        workflowGenerator.loadWorkflow(mm, filename);

        FulibTools.objectDiagrams().dumpSVG("tmp/MicroShop/ShopEventStormingModel.svg",
                workflowGenerator.getEventModel().getEventStormingBoard());

        workflowGenerator.generate();

        System.out.println();
    }
}
