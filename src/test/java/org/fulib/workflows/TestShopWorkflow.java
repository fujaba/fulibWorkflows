package org.fulib.workflows;

import org.fulib.FulibTools;
import org.fulib.builder.ClassModelManager;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class TestShopWorkflow
{

   private ClassModelManager mm;

   @Test
   public void testGenerateShopSystem()
   {
      try {
         mm = new ClassModelManager();
         mm.setMainJavaDir("test/src/main/java");
         mm.setPackageName("uks.debuggen.shop");

         // html
         HtmlGenerator3 generator = new HtmlGenerator3();
         generator.dumpObjectDiagram = (f, o) -> { FulibTools.objectDiagrams().dumpSVG(f, o); };
         String filename = "test/src/gen/resources/workflows/ShopWorkflow2.yaml";
         String html = generator.generateHtml(filename);
         Files.write(Path.of("tmp/ShopEventStorming.html"), html.getBytes(StandardCharsets.UTF_8));

         // java
         WorkflowGenerator workflowGenerator = new WorkflowGenerator();
         workflowGenerator.dumpObjectDiagram = (o) -> { FulibTools.objectDiagrams().dumpSVG("tmp/shopboard.svg", o); };
         workflowGenerator.loadWorkflow(mm, filename);

         FulibTools.objectDiagrams().dumpSVG("tmp/ShopEventStormingModel.svg",
               workflowGenerator.getEventModel().getEventStormingBoard());

         workflowGenerator.generate();

         System.out.println();
      }
      catch (IOException e) {
         e.printStackTrace();
      }
   }
}
