package org.fulib.workflows;

import org.fulib.FulibTools;
import org.fulib.builder.ClassModelManager;
import org.fulib.workflows.HtmlGenerator3;
import org.fulib.workflows.WorkflowGenerator;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class GenInterconnect
{
   private ClassModelManager mm;

   @Test
   public void generateInterconnectSystem()
   {
      try {
         mm = new ClassModelManager();
         mm.setMainJavaDir("test/src/main/java");
         mm.setPackageName("uks.debuggen.interconnect");

         String fileName = "test/src/gen/resources/workflows/interconnect/InterconnectOverview.es.yaml";

         // html
         HtmlGenerator3 generator = new HtmlGenerator3();
         generator.dumpObjectDiagram = (f, o) -> { FulibTools.objectDiagrams().dumpSVG(f, o); };
         String html = generator.generateHtml(fileName);
         Files.write(Path.of("tmp/InterconnectEventStorming.html"), html.getBytes(StandardCharsets.UTF_8));

         // java
         WorkflowGenerator workflowGenerator = new WorkflowGenerator();
         workflowGenerator.dumpObjectDiagram = (o) -> { FulibTools.objectDiagrams().dumpSVG("tmp/InterconnectBoard.svg", o); };
         workflowGenerator.loadWorkflow(mm, fileName);

         FulibTools.objectDiagrams().dumpSVG("tmp/InterconnectEventStormingModel.svg",
               workflowGenerator.getEventModel().getEventStormingBoard());

         workflowGenerator.generate();

         System.out.println();
      }
      catch (IOException e) {
         e.printStackTrace();
      }
   }
}
