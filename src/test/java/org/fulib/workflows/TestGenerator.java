package org.fulib.workflows;

import org.fulib.Fulib;
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

         String yaml = Files.readString(Path.of("test/src/gen/resources/workflows/ShopWorkflow.yml"));

         WorkflowGenerator workflowGenerator = new WorkflowGenerator().loadWorkflow(mm, yaml);

         workflowGenerator.generate();

         System.out.println();
      }
      catch (IOException e) {
         e.printStackTrace();
      }
   }
}
