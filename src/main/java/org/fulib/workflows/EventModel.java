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

   public Workflow getOrCreateRootWorkflow() {
      if (rootWorkflow == null) {
         rootWorkflow = new Workflow().setName("working smoothly");
      }
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
            userNote.withWorkflows(getOrCreateRootWorkflow());
         }
         else if (entry.getKey().equalsIgnoreCase("service")) {
            ServiceNote note = new ServiceNote();
            note.setName(entry.getValue());
            note.setPort(map.get("port"));
            note.setMap(map);
            note.withWorkflows(getOrCreateRootWorkflow());
         }
         else if (entry.getKey().equalsIgnoreCase("Action")) {
            UserInteraction userInteraction = new UserInteraction();
            userInteraction.setActorName(entry.getValue());
            UserNote userNote = getOrCreateRootWorkflow().getOrCreateFromUsers(userInteraction.getActorName());
            userNote.withInteractions(userInteraction);
            lastActor = userInteraction;
         }
         else if (entry.getKey().equalsIgnoreCase("Policy")) {
            Policy policy = new Policy();
            policy.setActorName(entry.getValue());
            policy.setWorkflow(getOrCreateRootWorkflow());
            ServiceNote service = getOrCreateRootWorkflow().getOrCreateFromServices(entry.getValue());
            policy.setService(service);
            EventNote trigger = (EventNote) getOrCreateRootWorkflow().getFromNotes(map.get("trigger"));
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
            note.setWorkflow(getOrCreateRootWorkflow());
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
            eventNote.setWorkflow(getOrCreateRootWorkflow());
            eventNote.setMap(map);

            EventType eventType = getOrCreateRootWorkflow().getOrCreateEventType(eventNote.getEventTypeName());
            eventType.withEvents(eventNote);

            lastEvent = eventNote;
            addToStepsOfLastActor(eventNote);
         }
         else {
            Logger.getGlobal().severe("Unknown event type " + getEventType(map));
         }
      }
      return getOrCreateRootWorkflow();
   }

   private void addToStepsOfLastActor(WorkflowNote note)
   {
      if (note instanceof EventNote) {
         if (lastActor == null || ! (lastActor instanceof UserInteraction)) {
            UserNote somebody = getOrCreateRootWorkflow().getOrCreateFromUsers("somebody");
            Interaction someaction = new UserInteraction().setWorkflow(getOrCreateRootWorkflow()).setUser(somebody).setActorName("somebody");
            lastActor = someaction;
         }
      }
      else if (note instanceof DataNote) {
         if (lastActor == null || ! (lastActor instanceof Policy)) {
            ServiceNote someservice = getOrCreateRootWorkflow().getOrCreateFromServices("someservice");
            Interaction someaction = new Policy()
                  .setWorkflow(getOrCreateRootWorkflow())
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
