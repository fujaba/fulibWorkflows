package org.fulib.workflows;

import org.fulib.FulibTools;
import org.fulib.workflows.html.HtmlGenerator3;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class HtmlTest
{
   @Test
   public void testGenWorkflow() throws IOException
   {

      String yaml = Files.readString(Path.of("test/src/gen/resources/workflows/ShopWorkflow2.yaml"));
      Files.createDirectories(Path.of("tmp"));

      HtmlGenerator3 generator = new HtmlGenerator3();
      generator.dumpObjectDiagram = (f, o) -> { FulibTools.objectDiagrams().dumpSVG(f, o); };
      generator.generateViewFiles(yaml, "Shop2");

   }

   @Test
   public void testGUIYaml() throws IOException
   {

      String yaml = Files.readString(Path.of("test/src/gen/resources/workflows/GUI.yaml"));
      Files.createDirectories(Path.of("tmp"));

      HtmlGenerator3 generator = new HtmlGenerator3();
      generator.dumpObjectDiagram = (f, o) -> { FulibTools.objectDiagrams().dumpSVG(f, o); };
      generator.generateViewFiles(yaml, "GUI");
   }
}
