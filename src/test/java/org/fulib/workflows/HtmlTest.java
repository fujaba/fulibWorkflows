package org.fulib.workflows;

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
      String html = generator.generateHtml(yaml);
      Files.write(Path.of("tmp/index3.html"), html.getBytes(StandardCharsets.UTF_8));

   }

   @Test
   public void testGUIYaml() throws IOException
   {

      String yaml = Files.readString(Path.of("test/src/gen/resources/workflows/GUI.yaml"));
      Files.createDirectories(Path.of("tmp"));

      HtmlGenerator3 generator = new HtmlGenerator3();
      String html = generator.generateHtml(yaml);
      Files.write(Path.of("tmp/gui.html"), html.getBytes(StandardCharsets.UTF_8));

   }
}
