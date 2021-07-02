package org.fulib.workflows;

import org.fulib.yaml.Yaml;
import org.fulib.yaml.Yamler;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import static org.assertj.core.api.Assertions.assertThat;

public class TestWorkflowGenerator
{
   @Test
   public void testIndexOfLastUpper()
   {
      int index = StrUtil.indexOfLastUpperChar("OrderPicked");
      assertThat(index).isEqualTo(5);
   }

   @Test
   public void testLoadEventModel() throws IOException
   {


   }
}
