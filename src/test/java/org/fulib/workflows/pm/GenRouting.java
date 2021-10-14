package org.fulib.workflows.pm;

import org.fulib.builder.ClassModelManager;
import org.fulib.workflows.WorkflowGenerator;
import org.fulib.workflows.html.HtmlGenerator3;
import org.junit.Test;

public class GenRouting
{
   private ClassModelManager mm;

   @Test
   public void generate()
   {
      try {
         mm = new ClassModelManager();
         mm.setMainJavaDir("test/src/main/java");
         mm.setPackageName("uks.debuggen.pm.routing");

         String fileName = "test/src/gen/resources/workflows/PM/Routing.es.yaml";

         String system = "PM.Routing";

         HtmlGenerator3 generator = new HtmlGenerator3();
         generator.generateViewFiles(fileName, "Routing");

         WorkflowGenerator workflowGenerator = new WorkflowGenerator();
         workflowGenerator.loadPlainModel(mm, fileName);
         workflowGenerator.generate();
      } catch (Exception e) {
         e.printStackTrace();
      }
   }
}
