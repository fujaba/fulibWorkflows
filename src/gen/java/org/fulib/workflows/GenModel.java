
package org.fulib.workflows;

import org.fulib.builder.ClassModelDecorator;
import org.fulib.builder.ClassModelManager;
import org.fulib.builder.reflect.Link;

import java.util.LinkedHashMap;
import java.util.List;

public class GenModel implements ClassModelDecorator
{
   class Event {
      String id;
   }

   class Note {
      LinkedHashMap<String, String> map = new LinkedHashMap<>();
   }

   class EventStormingBoard {
      String name = "some event storming";
      @Link("eventStormingBoard")
      List<Workflow> workflows;
      @Link("eventStormingBoard")
      List<ServiceNote> services;
      @Link("eventStormingBoard")
      List<EventType> eventTypes;
      @Link("eventStormingBoard")
      List<UserNote> users;
   }

   class Workflow extends Note {
      String name;
      @Link("workflows")
      EventStormingBoard eventStormingBoard;
      @Link("workflow")
      List<WorkflowNote> notes;
      @Link("workflows")
      List<ServiceNote> services;
      @Link("workflow")
      List<UserInteraction> userInteractions;
      @Link("workflow")
      List<Policy> policies;
   }

   class UserNote extends Note {
      String name;
      @Link("user")
      List<UserInteraction> interactions;
      @Link("users")
      EventStormingBoard eventStormingBoard;
   }

   class ServiceNote extends Note {
      String name;
      String port;
      @Link("services")
      List<Workflow> workflows;
      @Link("services")
      EventStormingBoard eventStormingBoard;
      @Link("service")
      List<Policy> policies;
      @Link("handlers")
      List<EventType> handledEventTypes;
      @Link("service")
      List<PageNote> pages;
   }

   class WorkflowNote extends Note {
      String time;
      @Link("notes")
      Workflow workflow;
      @Link("steps")
      Interaction interaction;
   }

   class EventType
   {
      String eventTypeName;
      @Link("type")
      List<EventNote> events;
      @Link("handledEventTypes")
      List<ServiceNote> handlers;
      @Link("eventTypes")
      EventStormingBoard eventStormingBoard;
   }

   class EventNote extends WorkflowNote {
      String eventTypeName;
      @Link("trigger")
      List<Policy> policies;
      @Link("events")
      EventType type;
   }

   class PageNote extends WorkflowNote {
      @Link("pageNote")
      List<PageLine> lines;
      @Link("pages")
      ServiceNote service;
   }

   class PageLine {
      LinkedHashMap<String, String> map = new LinkedHashMap<>();
      @Link("lines")
      PageNote pageNote;
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
