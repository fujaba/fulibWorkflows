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

      String yaml = Files.readString(Path.of("test/src/gen/resources/workflows/ShopWorkflow.yml"));
      Files.createDirectories(Path.of("tmp"));

      HtmlGenerator2 generator2 = new HtmlGenerator2();
      String html2 = generator2.generateHtml(yaml);
      Files.write(Path.of("tmp/index2.html"), html2.getBytes(StandardCharsets.UTF_8));

      HtmlGenerator generator = new HtmlGenerator();
      String html = generator.generateHtml(yaml);
      Files.write(Path.of("tmp/index.html"), html.getBytes(StandardCharsets.UTF_8));
   }
}
