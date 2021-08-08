package org.fulib.workflows;

import org.fulib.FulibTools;
import org.fulib.builder.ClassModelManager;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class TestPartyWorkflow
{

   private ClassModelManager mm;

   @Test
   public void testGeneratePartySystem()
   {
      try {
         mm = new ClassModelManager();
         mm.setMainJavaDir("test/src/main/java");
         mm.setPackageName("uks.debuggen.party");

         String yaml = Files.readString(Path.of("test/src/gen/resources/workflows/PartyWorkflow.es.yaml"));

         // html
         HtmlGenerator3 generator = new HtmlGenerator3();
         generator.dumpObjectDiagram = (f, o) -> { FulibTools.objectDiagrams().dumpSVG(f, o); };
         String html = generator.generateHtml(yaml);
         Files.write(Path.of("tmp/PartyEventStorming.html"), html.getBytes(StandardCharsets.UTF_8));

         // java
         WorkflowGenerator workflowGenerator = new WorkflowGenerator();
         workflowGenerator.dumpObjectDiagram = (o) -> { FulibTools.objectDiagrams().dumpSVG("tmp/PartyBoard.svg", o); };
         workflowGenerator.loadWorkflow(mm, yaml);

         FulibTools.objectDiagrams().dumpSVG("tmp/PartyEventStormingModel.svg",
               workflowGenerator.getEventModel().getEventStormingBoard());

         workflowGenerator.generate();

         System.out.println();
      }
      catch (IOException e) {
         e.printStackTrace();
      }
   }
}
