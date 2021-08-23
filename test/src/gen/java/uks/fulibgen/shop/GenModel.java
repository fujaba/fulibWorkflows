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
      new WorkflowGenerator().generateWorkflow(mm, "src/gen/resources/workflows/ShopWorkflow2.yaml");
   }
}
