package org.fulib.workflows;

import org.fulib.yaml.Yamler2;

import java.util.*;

public class EventModel
{
   public String workflowName;
   private Workflow rootWorkflow;

   public Workflow getRootWorkflow()
   {
      return rootWorkflow;
   }

   public Workflow buildEventStormModel(String yaml)
   {
      ArrayList<LinkedHashMap<String, String>> maps = new Yamler2().decodeList(yaml);

      Interaction lastActor = new UserInteraction().setActorName("somebody");
      EventNote lastEvent = null;

      for (LinkedHashMap<String, String> map : maps) {
         Iterator<Map.Entry<String, String>> iterator = map.entrySet().iterator();
         Map.Entry<String, String> entry = iterator.next();
         if (entry.getKey().equals("WorkflowStarted")) {
            workflowName = entry.getValue();
            rootWorkflow = new Workflow().setName(workflowName);
            continue;
         }
         if (entry.getKey().equals("UserRegistered")) {
            UserNote userNote = new UserNote().setName(map.get("name"));
            userNote.setMap(map);
            userNote.withWorkflows(rootWorkflow);
         }
         else if (entry.getKey().equals("ServiceRegistered")) {
            ServiceNote note = new ServiceNote();
            note.setName(entry.getValue());
            note.setPort(map.get("port"));
            note.setMap(map);
            note.withWorkflows(rootWorkflow);
         }
         else if (entry.getKey().equals("Action")) {
            UserInteraction userInteraction = new UserInteraction();
            userInteraction.setActorName(entry.getValue());
            UserNote userNote = rootWorkflow.getOrCreateFromUsers(userInteraction.getActorName());
            userNote.withInteractions(userInteraction);
            lastActor = userInteraction;
         }
         else if (entry.getKey().equals("Policy")) {
            Policy policy = new Policy();
            policy.setActorName(entry.getValue());
            policy.setWorkflow(rootWorkflow);
            ServiceNote service = rootWorkflow.getFromServices(entry.getValue());
            policy.setService(service);
            EventNote trigger = (EventNote) rootWorkflow.getFromNotes(map.get("trigger"));
            policy.setTrigger(trigger);
            EventType type = trigger.getType();
            service.withHandledEventTypes(type);
            lastActor = policy;
         }
         else if (entry.getKey().endsWith("Data")) {
            Map.Entry<String, String> typeEntry = iterator.next();
            DataNote note = new DataNote();
            note.setTime(entry.getValue());
            note.setMap(map);
            note.setWorkflow(rootWorkflow);
            note.setDataType(typeEntry.getKey());
            Policy policy = (Policy) lastActor;
            policy.withSteps(note);
         }
         else {
            EventNote eventNote = new EventNote();
            eventNote.setTime(getEventId(map));
            eventNote.setEventTypeName(getEventType(map));
            eventNote.setWorkflow(rootWorkflow);
            eventNote.setMap(map);

            EventType eventType = rootWorkflow.getOrCreateEventType(eventNote.getEventTypeName());
            eventType.withEvents(eventNote);

            lastActor.withSteps(eventNote);

         }
      }
      return rootWorkflow;
   }

   public String getEventId(Map<String, String> map)
   {
      return map.values().iterator().next();
   }

   public String getEventType(Map<String, String> map)
   {
      return map.keySet().iterator().next();
   }
}
