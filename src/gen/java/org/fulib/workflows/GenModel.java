
package org.fulib.workflows;

import org.fulib.builder.ClassModelDecorator;
import org.fulib.builder.ClassModelManager;

public class GenModel implements ClassModelDecorator
{
   class Event {
      String id;
   }

   @Override
   public void decorate(ClassModelManager mm)
   {
      mm.haveNestedClasses(GenModel.class);
   }
}
