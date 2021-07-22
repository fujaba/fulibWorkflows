package org.fulib.workflows;

import org.fulib.yaml.Yamler2;

import java.util.*;
import java.util.logging.Logger;

public class EventModel
{
   public String workflowName;
   private Workflow rootWorkflow;
   private Interaction lastActor;
   private EventNote lastEvent;

   public Workflow getRootWorkflow()
   {
      return rootWorkflow;
   }

   public Workflow buildEventStormModel(String yaml)
   {
      ArrayList<LinkedHashMap<String, String>> maps = new Yamler2().decodeList(yaml);

      lastActor = null;

      for (LinkedHashMap<String, String> map : maps) {
         Iterator<Map.Entry<String, String>> iterator = map.entrySet().iterator();
         Map.Entry<String, String> entry = iterator.next();
         if (entry.getKey().equalsIgnoreCase("workflow")) {
            workflowName = entry.getValue();
            workflowName = StrUtil.toIdentifier(workflowName);
            rootWorkflow = new Workflow().setName(workflowName);
            rootWorkflow.setMap(map);
            continue;
         }
         if (entry.getKey().equals("user")) {
            UserNote userNote = new UserNote().setName(map.get("name"));
            userNote.setMap(map);
            userNote.withWorkflows(rootWorkflow);
         }
         else if (entry.getKey().equalsIgnoreCase("service")) {
            ServiceNote note = new ServiceNote();
            note.setName(entry.getValue());
            note.setPort(map.get("port"));
            note.setMap(map);
            note.withWorkflows(rootWorkflow);
         }
         else if (entry.getKey().equalsIgnoreCase("Action")) {
            UserInteraction userInteraction = new UserInteraction();
            userInteraction.setActorName(entry.getValue());
            UserNote userNote = rootWorkflow.getOrCreateFromUsers(userInteraction.getActorName());
            userNote.withInteractions(userInteraction);
            lastActor = userInteraction;
         }
         else if (entry.getKey().equalsIgnoreCase("Policy")) {
            Policy policy = new Policy();
            policy.setActorName(entry.getValue());
            policy.setWorkflow(rootWorkflow);
            ServiceNote service = rootWorkflow.getOrCreateFromServices(entry.getValue());
            policy.setService(service);
            EventNote trigger = (EventNote) rootWorkflow.getFromNotes(map.get("trigger"));
            policy.setTrigger(trigger);
            EventType type = trigger.getType();
            service.withHandledEventTypes(type);
            lastActor = policy;
         }
         else if (entry.getKey().equalsIgnoreCase("Data")) {
            Map.Entry<String, String> typeEntry = iterator.next();
            DataNote note = new DataNote();
            note.setTime(entry.getValue());
            note.setMap(map);
            note.setWorkflow(rootWorkflow);
            note.setDataType(typeEntry.getKey());
            addToStepsOfLastActor(note);
         }
         else if (entry.getKey().equalsIgnoreCase("event")){
            EventNote eventNote = new EventNote();
            String value = getEventId(map); // example value: product stored 12:00
            String[] split = value.split("\\s");
            eventNote.setTime(split[split.length-1]);
            String eventTypeName = "";
            for (int i = 0; i < split.length - 1; i++) {
               eventTypeName += org.fulib.StrUtil.cap(split[i]);
            }
            eventNote.setEventTypeName(eventTypeName);
            eventNote.setWorkflow(rootWorkflow);
            eventNote.setMap(map);

            EventType eventType = rootWorkflow.getOrCreateEventType(eventNote.getEventTypeName());
            eventType.withEvents(eventNote);

            lastEvent = eventNote;
            addToStepsOfLastActor(eventNote);
         }
         else {
            Logger.getGlobal().severe("Unknown event type " + getEventType(map));
         }
      }
      return rootWorkflow;
   }

   private void addToStepsOfLastActor(WorkflowNote note)
   {
      if (note instanceof EventNote) {
         if (lastActor == null || ! (lastActor instanceof UserInteraction)) {
            UserNote somebody = rootWorkflow.getOrCreateFromUsers("somebody");
            Interaction someaction = new UserInteraction().setWorkflow(rootWorkflow).setUser(somebody).setActorName("somebody");
            lastActor = someaction;
         }
      }
      else if (note instanceof DataNote) {
         if (lastActor == null || ! (lastActor instanceof Policy)) {
            ServiceNote someservice = rootWorkflow.getOrCreateFromServices("someservice");
            Interaction someaction = new Policy()
                  .setWorkflow(rootWorkflow)
                  .setService(someservice)
                  .setTrigger(lastEvent)
                  .setActorName("someservice");
            someservice.withHandledEventTypes(lastEvent.getType());
            lastActor = someaction;
         }
      }
      else {
         Logger.getGlobal().severe("Unknown note type " + note.getClass().getSimpleName());
      }
      lastActor.withSteps(note);

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
