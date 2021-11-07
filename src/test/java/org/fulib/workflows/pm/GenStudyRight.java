package org.fulib.workflows.pm;

import org.fulib.builder.ClassModelManager;
import org.fulib.workflows.WorkflowGenerator;
import org.fulib.workflows.html.HtmlGenerator3;
import org.junit.Test;

public class GenStudyRight
{
   private ClassModelManager mm;

   @Test
   public void generate()
   {
      try {
         mm = new ClassModelManager();
         mm.setMainJavaDir("test/src/main/java");
         mm.setPackageName("uks.debuggen.pm.studyright");

         String fileName = "test/src/gen/resources/workflows/PM/StudyRight.es.yaml";

         HtmlGenerator3 generator = new HtmlGenerator3();
         generator.generateViewFiles(fileName, "StudyRightAdam");

         WorkflowGenerator workflowGenerator = new WorkflowGenerator();
         workflowGenerator.loadPlainModel(mm, fileName);
         workflowGenerator.generate();
      } catch (Exception e) {
         e.printStackTrace();
      }
   }
}
