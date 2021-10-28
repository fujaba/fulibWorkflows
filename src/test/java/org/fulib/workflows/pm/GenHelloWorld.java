package org.fulib.workflows.pm;

import org.fulib.builder.ClassModelManager;
import org.fulib.workflows.WorkflowGenerator;
import org.fulib.workflows.html.HtmlGenerator3;
import org.junit.Test;

public class GenHelloWorld
{
   private ClassModelManager mm;

   @Test
   public void generate()
   {
      try {
         mm = new ClassModelManager();
         mm.setMainJavaDir("test/src/main/java");
         mm.setPackageName("uks.debuggen.pm.hello");

         String fileName = "test/src/gen/resources/workflows/PM/HelloWorld.es.yaml";

         String system = "PM.Hello";

         HtmlGenerator3 generator = new HtmlGenerator3();
         generator.generateViewFiles(fileName, "Hello");

      } catch (Exception e) {
         e.printStackTrace();
      }
   }
}
