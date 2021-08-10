package org.fulib.workflows;

import org.fulib.FulibTools;
import org.fulib.builder.ClassModelManager;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class TestStudyRight
{

   private ClassModelManager mm;

   @Test
   public void testStartGenerator()
   {
      try {
         mm = new ClassModelManager();
         mm.setMainJavaDir("test/src/main/java");
         mm.setPackageName("uks.debuggen.studyright");

         String yaml = Files.readString(Path.of("test/src/gen/resources/workflows/StudyRight.es.yaml"));

         // html
         HtmlGenerator3 generator = new HtmlGenerator3();
         generator.dumpObjectDiagram = (f, o) -> { FulibTools.objectDiagrams().dumpSVG(f, o); };
         String html = generator.generateHtml(yaml);
         Files.write(Path.of("tmp/StudyRightEventStorming.html"), html.getBytes(StandardCharsets.UTF_8));

         // java
         WorkflowGenerator workflowGenerator = new WorkflowGenerator();
         workflowGenerator.loadWorkflow(mm, "", yaml);

         FulibTools.objectDiagrams().dumpSVG("tmp/StudyRightModel.svg",
               workflowGenerator.getEventModel().getEventStormingBoard());

         workflowGenerator.generate();

         System.out.println();
      }
      catch (IOException e) {
         e.printStackTrace();
      }
   }
}
