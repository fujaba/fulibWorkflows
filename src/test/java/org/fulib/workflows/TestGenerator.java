package org.fulib.workflows;

import org.fulib.FulibTools;
import org.fulib.builder.ClassModelManager;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class TestGenerator
{

   private ClassModelManager mm;

   @Test
   public void testStartGenerator()
   {
      try {
         mm = new ClassModelManager();
         mm.setMainJavaDir("test/src/main/java");
         mm.setPackageName("uks.debuggen.shop");

         String yaml = Files.readString(Path.of("test/src/gen/resources/workflows/ShopWorkflow2.yaml"));

         WorkflowGenerator workflowGenerator = new WorkflowGenerator();
         workflowGenerator.dumpObjectDiagram = (o) -> { FulibTools.objectDiagrams().dumpSVG("tmp/shopboard.svg", o); };
         workflowGenerator.loadWorkflow(mm, "", yaml);

         FulibTools.objectDiagrams().dumpSVG("tmp/EventStorming.svg",
               workflowGenerator.getEventModel().getEventStormingBoard());

         workflowGenerator.generate();

         System.out.println();
      }
      catch (IOException e) {
         e.printStackTrace();
      }
   }

   @Test
   public void testGeneratorWithPages()
   {
      try {
         mm = new ClassModelManager();
         mm.setMainJavaDir("test/src/main/java");
         mm.setPackageName("uks.debuggen.page");

         String yaml = Files.readString(Path.of("test/src/gen/resources/workflows/GUI.yaml"));

         WorkflowGenerator workflowGenerator = new WorkflowGenerator();
         workflowGenerator.dumpObjectDiagram = (o) -> { FulibTools.objectDiagrams().dumpSVG("tmp/guiboard.svg", o); };
         workflowGenerator.loadWorkflow(mm, "", yaml);

         FulibTools.objectDiagrams().dumpSVG("tmp/EventStorming.svg",
               workflowGenerator.getEventModel().getEventStormingBoard());

         workflowGenerator.generate();

         System.out.println();
      }
      catch (IOException e) {
         e.printStackTrace();
      }
   }
}
