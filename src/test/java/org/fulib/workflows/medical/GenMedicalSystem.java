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
import java.util.Locale;

public class GenMedicalSystem
{
   private ClassModelManager mm;

   @Test
   public void generateMedicalSystem()
   {
      genSystem("FamilyDoctorDegen");
      genSystem("MarburgExpertSystem");
   }

   private void genSystem(String system)
   {
      String packageName = system.toLowerCase();
      try {
         mm = new ClassModelManager();
         mm.setMainJavaDir("test/src/main/java");
         mm.setPackageName("uks.debuggen.medical." + packageName);

         String fileName = String.format("test/src/gen/resources/workflows/medical/%s/%1$s.es.yaml", system);

         // html
         HtmlGenerator3 generator = new HtmlGenerator3();
         generator.dumpObjectDiagram = (f, o) -> { FulibTools.objectDiagrams().dumpSVG(f, o); };
         String html = generator.generateHtml(fileName);
         Files.write(Path.of(String.format("tmp/%sEventStorming.html", system)), html.getBytes(StandardCharsets.UTF_8));

         // java
         WorkflowGenerator workflowGenerator = new WorkflowGenerator();
         workflowGenerator.dumpObjectDiagram = (o) -> { FulibTools.objectDiagrams().dumpSVG(String.format("tmp/%s.svg", system), o); };
         workflowGenerator.loadWorkflow(mm, fileName);

         FulibTools.objectDiagrams().dumpSVG(String.format("tmp/%sEventStormingModel.svg", system),
               workflowGenerator.getEventModel().getEventStormingBoard());

         workflowGenerator.generate();

         System.out.println();
      }
      catch (IOException e) {
         e.printStackTrace();
      }
   }
}
