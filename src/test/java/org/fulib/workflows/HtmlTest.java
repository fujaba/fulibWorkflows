package org.fulib.workflows;

import java.io.IOException;

import org.fulib.FulibTools;
import org.fulib.workflows.html.HtmlGenerator3;
import org.junit.Test;

public class HtmlTest
{


   @Test
   public void testGUIYaml() throws IOException
   {

      String fileName = "test/src/gen/resources/workflows/GUI.yaml";

      HtmlGenerator3 generator = new HtmlGenerator3();
      generator.dumpObjectDiagram = (f, o) -> { FulibTools.objectDiagrams().dumpSVG(f, o); };
      generator.generateViewFiles(fileName, "GUI");
   }
}
