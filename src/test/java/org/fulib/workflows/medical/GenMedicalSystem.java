package org.fulib.workflows.medical;

import org.fulib.FulibTools;
import org.fulib.builder.ClassModelManager;
import org.fulib.workflows.HtmlGenerator3;
import org.fulib.workflows.WorkflowGenerator;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class GenMedicalSystem
{
   private ClassModelManager mm;

   @Test
   public void generateMedicalSystem()
   {
      try {
         mm = new ClassModelManager();
         mm.setMainJavaDir("test/src/main/java");
         mm.setPackageName("uks.debuggen.medical");

         String fileName = "test/src/gen/resources/workflows/medical/MedicalOverview.es.yaml";

         // html
         HtmlGenerator3 generator = new HtmlGenerator3();
         generator.dumpObjectDiagram = (f, o) -> { FulibTools.objectDiagrams().dumpSVG(f, o); };
         String html = generator.generateHtml(fileName);
         Files.write(Path.of("tmp/MedicalEventStorming.html"), html.getBytes(StandardCharsets.UTF_8));

         // java
         WorkflowGenerator workflowGenerator = new WorkflowGenerator();
         workflowGenerator.dumpObjectDiagram = (o) -> { FulibTools.objectDiagrams().dumpSVG("tmp/MedicalBoard.svg", o); };
         workflowGenerator.loadWorkflow(mm, fileName);

         FulibTools.objectDiagrams().dumpSVG("tmp/MedicalEventStormingModel.svg",
               workflowGenerator.getEventModel().getEventStormingBoard());

         workflowGenerator.generate();

         System.out.println();
      }
      catch (IOException e) {
         e.printStackTrace();
      }
   }
}
