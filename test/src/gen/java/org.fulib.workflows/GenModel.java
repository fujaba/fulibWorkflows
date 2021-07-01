package org.fulib.workflows;

import org.fulib.builder.ClassModelDecorator;
import org.fulib.builder.ClassModelManager;
import org.fulib.yaml.Yamler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class GenModel implements ClassModelDecorator
{
   class Event {
      String id;
   }

   @Override
   public void decorate(ClassModelManager mm)
   {
      mm.haveNestedClasses(GenModel.class);

      String yaml = null;
      try {
         yaml = Files.readString(Path.of("src/gen/resources/org/fulib/workflows/ShopWorkflow.yml"));
         ArrayList<LinkedHashMap<String, String>> maps = new Yamler().decodeList(yaml);
         System.out.println();
         new WorkflowGenerator().loadWorkflow(mm, yaml);
      }
      catch (IOException e) {
         e.printStackTrace();
      }


   }
}
