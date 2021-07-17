
package org.fulib.workflows;

import org.fulib.builder.ClassModelDecorator;
import org.fulib.builder.ClassModelManager;
import org.fulib.builder.reflect.Link;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GenModel implements ClassModelDecorator
{
   class Event {
      String id;
   }

   class Note {
      LinkedHashMap<String, String> map = new LinkedHashMap<>();
   }

   class Workflow extends Note {
      String name;
      @Link("workflow")
      List<WorkflowNote> notes;
      @Link("workflows")
      List<UserNote> users;
      @Link("workflows")
      List<ServiceNote> services;
      @Link("workflow")
      List<UserInteraction> userInteractions;
      @Link("workflow")
      List<Policy> policies;

   }

   class UserNote extends Note {
      String name;
      @Link("users")
      List<Workflow> workflows;
      @Link("user")
      List<UserInteraction> interactions;
   }

   class ServiceNote extends Note {
      String name;
      String port;
      @Link("services")
      List<Workflow> workflows;
      @Link("service")
      List<UserInteraction> userInteractions;
      @Link("service")
      List<Policy> policies;
   }

   class WorkflowNote extends Note {
      String time;
      @Link("notes")
      Workflow workflow;
      @Link("steps")
      Interaction interaction;
   }

   class EventNote extends WorkflowNote {
      String eventType;
      @Link("trigger")
      List<Policy> policies;
   }

   class DataNote extends WorkflowNote {
      String dataType;
   }

   class CommandNote extends WorkflowNote {
      String eventType;
   }

   class Interaction {
      String actorName;
      @Link("interaction")
      List<WorkflowNote> steps;
   }

   class UserInteraction extends Interaction {
      @Link("interactions")
      UserNote user;
      @Link("userInteractions")
      ServiceNote service;
      @Link("userInteractions")
      Workflow workflow;
   }

   class Policy extends Interaction {
      @Link("policies")
      ServiceNote service;
      @Link("policies")
      EventNote trigger;
      @Link("policies")
      Workflow workflow;
   }



   @Override
   public void decorate(ClassModelManager mm)
   {
      mm.haveNestedClasses(GenModel.class);
   }
}
