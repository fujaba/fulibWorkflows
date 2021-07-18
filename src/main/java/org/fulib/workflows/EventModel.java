package org.fulib.workflows;

import org.fulib.yaml.Yamler2;

import java.util.*;

public class EventModel
{
   public TreeMap<String, LinkedHashMap<String, String>> eventMap;
   public LinkedHashMap<String, LinkedHashMap<String, String>> userMap;
   public LinkedHashMap<String, LinkedHashMap<String, LinkedHashSet<Map<String, String>>>> handlerDataMockupsMap;
   public String workflowName;
   private Workflow rootWorkflow;

   public Workflow getRootWorkflow()
   {
      return rootWorkflow;
   }

   public void buildEventMap(String yaml)
   {
      eventMap = new TreeMap<>();
      userMap = new LinkedHashMap<>();

      ArrayList<LinkedHashMap<String, String>> maps = new Yamler2().decodeList(yaml);

      Interaction lastActor = null;
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
            userMap.put(map.get("name"), map);
            UserNote userNote = new UserNote().setName(map.get("name"));
            userNote.setMap(map);
            userNote.withWorkflows(rootWorkflow);
            eventMap.put(entry.getValue(), map);
         }
         else if (entry.getKey().equals("ServiceRegistered")) {
            ServiceNote note = new ServiceNote();
            note.setName(map.get("name"));
            note.setPort(map.get("port"));
            note.setMap(map);
            note.withWorkflows(rootWorkflow);
            eventMap.put(entry.getValue(), map);
         }
         else if (entry.getKey().endsWith("Data")) {
            eventMap.put(entry.getValue(), map);
            Map.Entry<String, String> typeEntry = iterator.next();
            DataNote note = new DataNote();
            note.setTime(entry.getValue());
            note.setMap(map);
            note.setWorkflow(rootWorkflow);
            note.setDataType(typeEntry.getKey());
            String dataTime = note.getTime();
            String triggerTime = dataTime.substring(0, dataTime.lastIndexOf(':'));
            EventNote triggerNote = (EventNote) rootWorkflow.getFromNotes(triggerTime);
            EventType eventType = triggerNote.getType();
            String serviceName = entry.getKey();
            serviceName = serviceName.substring(0, serviceName.length() - "Data".length());
            Policy policy = null;
            if (lastActor == null || !lastActor.getActorName().equals(serviceName)) {
               ServiceNote serviceNote = rootWorkflow.getFromServices(serviceName);
               policy = new Policy();
               policy.setActorName(serviceName);
               policy.setWorkflow(rootWorkflow);
               policy.setService(serviceNote);
               policy.setTrigger((EventNote) triggerNote);

               serviceNote.withHandledEventTypes(eventType);

               lastActor = policy;
            }
            else {
               policy = (Policy) lastActor;
            }
            policy.withSteps(note);
         }
         else {
            eventMap.put(entry.getValue(), map);
            EventNote eventNote = new EventNote();
            eventNote.setTime(getEventId(map));
            eventNote.setEventTypeName(getEventType(map));
            eventNote.setWorkflow(rootWorkflow);
            eventNote.setMap(map);

            EventType eventType = rootWorkflow.getOrCreateEventType(eventNote.getEventTypeName());
            eventType.withEvents(eventNote);

            String user = map.get("user");
            if (lastActor == null || !lastActor.getActorName().equals(user)) {
               UserNote userNote = rootWorkflow.getFromUsers(user);
               if (userNote != null) {
                  UserInteraction userInteraction = new UserInteraction();
                  userInteraction.setActorName(user);
                  userInteraction.setUser(userNote);
                  userInteraction.setWorkflow(rootWorkflow);
                  lastActor = userInteraction;
               }
               else {
                  ServiceNote serviceNote = rootWorkflow.getFromServices(user);
                  Policy policy = new Policy();
                  policy.setActorName(user);
                  policy.setService(serviceNote);
                  policy.setWorkflow(rootWorkflow);
                  policy.setTrigger(lastEvent);

                  EventType type = lastEvent.getType();
                  type.withHandlers(serviceNote);

                  lastActor = policy;
               }

            }
            lastActor.withSteps(eventNote);

         }
      }

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
