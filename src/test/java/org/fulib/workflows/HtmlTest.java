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
      HtmlGenerator generator = new HtmlGenerator();

      String yaml = Files.readString(Path.of("test/src/gen/resources/workflows/ShopWorkflow.yml"));

      String html = generator.generateHtml(yaml);

      Files.createDirectories(Path.of("tmp"));
      Files.write(Path.of("tmp/index.html"), html.getBytes(StandardCharsets.UTF_8));
   }
}
