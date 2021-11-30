package org.fulib.workflows.pm;

import org.fulib.builder.ClassModelManager;
import org.fulib.workflows.WorkflowGenerator;
import org.fulib.workflows.html.HtmlGenerator3;
import org.junit.Test;
import org.junit.runner.JUnitCore;

public class GenParty
{
   public static void main(String[] args) {
      JUnitCore.main(GenParty.class.getName());
   }
   private ClassModelManager mm;

   @Test
   public void generate()
   {
      try {
         mm = new ClassModelManager();
         mm.setMainJavaDir("test/src/main/java");
         mm.setPackageName("uks.debuggen.pm.party");

         String fileName = "test/src/gen/resources/workflows/PM/Party.es.yaml";

         HtmlGenerator3 generator = new HtmlGenerator3();
         generator.generateViewFiles(fileName, "event-models/Party");

         WorkflowGenerator workflowGenerator = new WorkflowGenerator();
         workflowGenerator.setTestOutputDir("../event-models/Party");
         workflowGenerator.loadPlainModel(mm, fileName);
         workflowGenerator.generate();
      } catch (Exception e) {
         e.printStackTrace();
      }
   }
}
