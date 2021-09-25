package org.fulib.workflows;

import org.fulib.FulibTools;
import org.fulib.builder.ClassModelManager;
import org.fulib.workflows.html.HtmlGenerator3;
import org.junit.Test;

public class GenShopWorkflow {

    private ClassModelManager mm;

    @Test
    public void generateShopSystem() {

        // html
        HtmlGenerator3 generator = new HtmlGenerator3();
        // generator.generateViewFiles("test/src/gen/resources/workflows/shop/ShopFirstEventStorming.es.yaml", "FirstShopBoard");
        // generator.generateViewFiles("test/src/gen/resources/workflows/shop/ShopRefinedProductStoredEventStorming.es.yaml", "StoreProductShopBoard");
        generator.generateViewFiles("test/src/gen/resources/workflows/shop/ShopWithGuiPrototypes.es.yaml", "ShopWithGuiBoard");


        mm = new ClassModelManager();
        mm.setMainJavaDir("test/src/main/java");
        mm.setPackageName("uks.debuggen.microshop");
        WorkflowGenerator workflowGenerator = new WorkflowGenerator();
        workflowGenerator.dumpObjectDiagram = (root) -> FulibTools.objectDiagrams().dumpSVG("tmp/MicroShopBoard.svg", root);
        workflowGenerator.loadWorkflow(mm, "test/src/gen/resources/workflows/shop/ShopWithGuiPrototypes.es.yaml");
        workflowGenerator.generate();

        System.out.println();
    }
}
