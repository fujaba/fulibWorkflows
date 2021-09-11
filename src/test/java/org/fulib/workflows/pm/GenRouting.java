package org.fulib.workflows.pm;

import org.fulib.FulibTools;
import org.fulib.builder.ClassModelManager;
import org.fulib.workflows.HtmlGenerator3;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class GenRouting
{
   private ClassModelManager mm;

   @Test
   public void generate()
   {
      try {
         mm = new ClassModelManager();
         mm.setMainJavaDir("test/src/main/java");
         mm.setPackageName("uks.debuggen.pm");

         String fileName = "test/src/gen/resources/workflows/PM/Routing.es.yaml";

         HtmlGenerator3 generator = new HtmlGenerator3();
         generator.dumpObjectDiagram = (f, o) ->
         {FulibTools.objectDiagrams().dumpSVG(f, o);};
         String html = generator.generateHtml(fileName);
         Files.write(Path.of(String.format("tmp/%sEventStorming.html", "PM.Routing")), html.getBytes(StandardCharsets.UTF_8));
      } catch (Exception e) {
         e.printStackTrace();
      }
   }
}
