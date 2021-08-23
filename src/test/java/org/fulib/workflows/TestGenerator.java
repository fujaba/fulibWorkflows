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
      mm = new ClassModelManager();
      mm.setMainJavaDir("test/src/main/java");
      mm.setPackageName("uks.debuggen.shop");

      WorkflowGenerator workflowGenerator = new WorkflowGenerator();
      workflowGenerator.dumpObjectDiagram = (o) -> { FulibTools.objectDiagrams().dumpSVG("tmp/shopboard.svg", o); };
      workflowGenerator.loadWorkflow(mm, "test/src/gen/resources/workflows/ShopWorkflow2.yaml");

      FulibTools.objectDiagrams().dumpSVG("tmp/EventStorming.svg",
            workflowGenerator.getEventModel().getEventStormingBoard());

      workflowGenerator.generate();

      System.out.println();
   }

   @Test
   public void testGeneratorWithPages()
   {
      mm = new ClassModelManager();
      mm.setMainJavaDir("test/src/main/java");
      mm.setPackageName("uks.debuggen.page");

      WorkflowGenerator workflowGenerator = new WorkflowGenerator();
      workflowGenerator.dumpObjectDiagram = (o) -> { FulibTools.objectDiagrams().dumpSVG("tmp/guiboard.svg", o); };
      workflowGenerator.loadWorkflow(mm, "test/src/gen/resources/workflows/GUI.yaml");

      FulibTools.objectDiagrams().dumpSVG("tmp/EventStorming.svg",
            workflowGenerator.getEventModel().getEventStormingBoard());

      workflowGenerator.generate();

      System.out.println();
   }
}
