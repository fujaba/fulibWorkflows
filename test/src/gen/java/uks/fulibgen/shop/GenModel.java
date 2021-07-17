package uks.fulibgen.shop;

import org.fulib.builder.ClassModelDecorator;
import org.fulib.builder.ClassModelManager;
import org.fulib.workflows.WorkflowGenerator;
import org.fulib.yaml.Yamler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class GenModel implements ClassModelDecorator
{
   @Override
   public void decorate(ClassModelManager mm)
   {
      try {
         String yaml = Files.readString(Path.of("src/gen/resources/workflows/ShopWorkflow.yaml"));

         new WorkflowGenerator().generateWorkflow(mm, yaml);
      }
      catch (IOException e) {
         e.printStackTrace();
      }


   }
}
