package org.fulib.workflows.pm;

import org.fulib.builder.ClassModelManager;
import org.fulib.workflows.WorkflowGenerator;
import org.fulib.workflows.html.HtmlGenerator3;
import org.junit.Test;
import org.junit.runner.JUnitCore;

public class GenClickCounter
{
   public static void main(String[] args) {
      JUnitCore.main(GenClickCounter.class.getName());
   }
   private ClassModelManager mm;

   @Test
   public void generate()
   {
      try {
         mm = new ClassModelManager();
         mm.setMainJavaDir("test/src/main/java");
         mm.setPackageName("uks.debuggen.pm.clickCounter");

         String fileName = "test/src/gen/resources/workflows/PM/ClickCounter.es.yaml";

         HtmlGenerator3 generator = new HtmlGenerator3();
         generator.generateViewFiles(fileName, "event-models/ClickCounter");

         WorkflowGenerator workflowGenerator = new WorkflowGenerator();
         workflowGenerator.setTestOutputDir("../event-models/ClickCounter");
         workflowGenerator.loadWorkflow(mm, fileName);
         workflowGenerator.generate();
      } catch (Exception e) {
         e.printStackTrace();
      }
   }
}
