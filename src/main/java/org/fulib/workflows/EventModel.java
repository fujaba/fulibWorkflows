package org.fulib.workflows;

import org.fulib.yaml.Yamler2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.logging.Logger;

public class EventModel
{
   public String workflowName;
   private EventStormingBoard eventStormingBoard = null;
   private Workflow rootWorkflow;
   private Interaction lastActor;
   private EventNote lastEvent;
   private LinkedList<String> yamlFileList;
   private UserInteraction lastUser;
   private ServiceNote lastService;

   public EventStormingBoard getEventStormingBoard()
   {
      if (eventStormingBoard == null) {
         eventStormingBoard = new EventStormingBoard();
      }
      return eventStormingBoard;
   }

   public Workflow getRootWorkflow()
   {
      if (rootWorkflow == null) {
         rootWorkflow = new Workflow().setName("working smoothly");
         getEventStormingBoard().withWorkflows(rootWorkflow);
      }
      return rootWorkflow;
   }

   public EventStormingBoard buildEventStormModel(String fileName)
   {
      yamlFileList = new LinkedList<>();
      yamlFileList.add(fileName);


      while (!yamlFileList.isEmpty()) {
         fileName = yamlFileList.poll();

         String oneYaml = null;

         try {
            oneYaml = Files.readString(Path.of(fileName));
         }
         catch (IOException e) {
            continue;
         }

         ArrayList<LinkedHashMap<String, String>> maps = new Yamler2().decodeList(oneYaml);
         LinkedHashMap<String, PageNote> userLastPage = new LinkedHashMap<>();

         lastActor = null;
         lastUser = null;

         for (LinkedHashMap<String, String> map : maps) {
            Iterator<Map.Entry<String, String>> iterator = map.entrySet().iterator();
            Map.Entry<String, String> entry = iterator.next();
            if (entry.getKey().equalsIgnoreCase("workflow")) {
               workflowName = entry.getValue();
               workflowName = StrUtil.toIdentifier(workflowName);
               rootWorkflow = new Workflow().setName(workflowName);
               rootWorkflow.setMap(map);
               getEventStormingBoard().withWorkflows(rootWorkflow);
               continue;
            }
            if (entry.getKey().equals("user")) {
               UserNote userNote = new UserNote().setName(map.get("name"));
               userNote.setMap(map);
               userNote.setEventStormingBoard(getEventStormingBoard());
            }
            else if (entry.getKey().equalsIgnoreCase("service")) {
               ServiceNote note = new ServiceNote();
               note.setName(entry.getValue());
               note.setPort(map.get("port"));
               note.setMap(map);
               note.withWorkflows(getRootWorkflow());
               note.setEventStormingBoard(getEventStormingBoard());
            }
            else if (entry.getKey().equalsIgnoreCase("Action")) {
               UserInteraction userInteraction = new UserInteraction();
               userInteraction.setActorName(entry.getValue());
               UserNote userNote = getEventStormingBoard().getOrCreateFromUsers(userInteraction.getActorName());
               userNote.withInteractions(userInteraction);
               lastActor = userInteraction;
               lastUser = userInteraction;
            }
            else if (entry.getKey().equalsIgnoreCase("subprocess")
                  || entry.getKey().equalsIgnoreCase("boundedcontext")) {
               SubprocessNote subprocessNote = new SubprocessNote();
               subprocessNote.setSubprocessName(StrUtil.toIdentifier(entry.getValue()));
               subprocessNote.setTime(subprocessNote.getSubprocessName());
               subprocessNote.setMap(map);
               subprocessNote.setWorkflow(getRootWorkflow());
               subprocessNote.setKind(entry.getKey());
               subprocessNote.setEventStormingBoard(getEventStormingBoard());

               addSubFile(fileName, subprocessNote);
            }
            else if (entry.getKey().equalsIgnoreCase("brokertopic")) {
               BrokerTopicNote brokerTopicNote = new BrokerTopicNote();
               brokerTopicNote.setBrokerName(StrUtil.toIdentifier(entry.getValue()));
               brokerTopicNote.setTime(brokerTopicNote.getBrokerName());
               brokerTopicNote.setMap(map);
               brokerTopicNote.setWorkflow(getRootWorkflow());
               addToStepsOfLastActor(brokerTopicNote);
            }
            else if (entry.getKey().equalsIgnoreCase("Policy")) {
               Policy policy = new Policy();
               policy.setActorName(entry.getValue());
               policy.setWorkflow(getRootWorkflow());
               ServiceNote service = getEventStormingBoard().getOrCreateFromServices(entry.getValue());
               policy.setService(service);
               String triggerTime = map.get("trigger");
               if (triggerTime != null) {
                  EventNote trigger = (EventNote) getRootWorkflow().getFromNotes(triggerTime);
                  policy.setTrigger(trigger);
                  EventType type = trigger.getType();
                  service.withHandledEventTypes(type);
               }
               lastService = service;
               lastActor = policy;
            }
            else if (entry.getKey().equalsIgnoreCase("class")) {
               ClassNote classNote = new ClassNote();
               classNote.setMap(map);
               classNote.setWorkflow(getRootWorkflow());
               addToStepsOfLastActor(classNote);
            }
            else if (entry.getKey().equalsIgnoreCase("Data")) {
               buildDataNote(map, iterator, entry);
            }
            else if (entry.getKey().equalsIgnoreCase("event")) {
               EventNote eventNote = new EventNote();
               fillEventNote(map, eventNote);
            }
            else if (entry.getKey().equalsIgnoreCase("command")) {
               CommandNote commandNote = new CommandNote();
               fillEventNote(map, commandNote);
            }
            else if (entry.getKey().equalsIgnoreCase("page")) {
               buildPageNote(userLastPage, map, entry);
            }
            else if (entry.getKey().equalsIgnoreCase("board")) {
               getEventStormingBoard().setName(map.get("board"));
            }
            else if (entry.getKey().equalsIgnoreCase("query")) {
               String value = entry.getValue();
               String[] split = value.split("\\s+");
               String key = split[0];
               String time = split[1];

               QueryNote queryNote = new QueryNote();
               queryNote.setTime(time);
               queryNote.setWorkflow(getRootWorkflow());
               queryNote.setMap(map);
               queryNote.setKey(key);
               queryNote.setResult(map.get("result"));

               addToStepsOfLastActor(queryNote);
               // send a query
               // validate result
            }
            else {
               Logger.getGlobal().severe("Unknown event type " + getEventType(map));
            }
         }

      }

      return getEventStormingBoard();
   }

   private void addSubFile(String fileName, SubprocessNote subprocessNote)
   {
      ArrayList<String> fileNameTries = new ArrayList<>();
      String subFileName = fileName.substring(0, fileName.lastIndexOf('/'));
      subFileName = String.format("%s/%s.es.yaml", subFileName, subprocessNote.getSubprocessName());
      if (Files.exists(Path.of(subFileName))) {
         yamlFileList.add(subFileName);
         return;
      }

      subFileName = fileName.substring(0, fileName.lastIndexOf('/'));
      subFileName = String.format("%s/%s/%2$s.es.yaml", subFileName, subprocessNote.getSubprocessName());
      if (Files.exists(Path.of(subFileName))) {
         yamlFileList.add(subFileName);
         return;
      }

      Logger.getGlobal().info("No file for subprocess or bounded context " + subprocessNote.getSubprocessName());


   }

   private void buildDataNote(LinkedHashMap<String, String> map, Iterator<Map.Entry<String, String>> iterator, Map.Entry<String, String> entry)
   {
      String value = entry.getValue();
      String[] split = StrUtil.split(value);
      String className;
      String objectId;
      String time = split[split.length - 1];

      if (time.indexOf(':') < 0) {
         // no time given, use auto time and set class name
         time = getRootWorkflow().addToTime("00:00:01");
         value = value + " " + time;
         split = StrUtil.split(value);
      }

      if (split.length == 1) {
         // just 12:00, get class and id from next line
         value = split[0];
         Map.Entry<String, String> typeEntry = iterator.next();
         className = StrUtil.cap(typeEntry.getKey());
         objectId = typeEntry.getValue();
      }
      else if (split.length == 2) {
         // class name and 12:00
         className = split[0];
         value = split[1];
         Map.Entry<String, String> typeEntry = iterator.next();
         objectId = typeEntry.getValue();
         objectId = getVarName(objectId);
         map.put("data", String.format("%s %s %s", className, objectId, value));
      }
      else {
         className = StrUtil.cap(split[0]);
         objectId = split[1];
         value = split[2];
      }

      DataNote note = new DataNote();
      note.setTime(value);
      note.setBlockId(objectId);
      note.setMap(map);
      note.setWorkflow(getRootWorkflow());
      note.setDataType(className);
      addToStepsOfLastActor(note);
      ServiceNote serviceNote = ((Policy) lastActor).getService();
      serviceNote.getObjectMap().put(objectId, className);

      DataType dataType = eventStormingBoard.getOrCreateDataType(note.getDataType());
      dataType.withDataNotes(note);
      String migratedTo = map.get("@migratedTo");
      if (migratedTo != null) {
         dataType.setMigratedTo(migratedTo);
      }

      serviceNote.withHandledDataTypes(dataType);
   }

   private void buildPageNote(LinkedHashMap<String, PageNote> userLastPage, LinkedHashMap<String, String> map, Map.Entry<String, String> entry)
   {
      PageNote pageNote = new PageNote();
      pageNote.setWorkflow(getRootWorkflow());
      pageNote.setMap(map);

      // read multiline value
      String multilineValue = entry.getValue();
      ArrayList<LinkedHashMap<String, String>> lineMaps = new Yamler2().decodeList(multilineValue);
      for (LinkedHashMap<String, String> lineMap : lineMaps) {
         PageLine pageLine = new PageLine()
               .setPageNote(pageNote)
               .setMap(lineMap);
         String nameValue = lineMap.get("name");
         if (nameValue != null) {
            // its like name: ServiceName timestamp
            String[] split = nameValue.split("\\s+");
            String pageName = split[split.length - 1];
            String serviceName = split[0];
            pageNote.setTime(pageName);
            ServiceNote serviceNote = getEventStormingBoard().getOrCreateFromServices(serviceName);
            pageNote.setService(serviceNote);

            addToStepsOfLastActor(pageNote);
            PageNote previousPage = userLastPage.get(lastActor.getActorName());
            if (previousPage != null) {
               previousPage.setNextPage(pageNote);
            }
            userLastPage.put(lastActor.getActorName(), pageNote);
         }
         String command = lineMap.get("command");
         if (command != null) {
            // store button name
            String buttonId = lineMap.get("button");
            pageNote.setButtonId(buttonId);

            // add an event note
            EventNote commandNote = new CommandNote();
            LinkedHashMap<String, String> eventMap = new LinkedHashMap<>();
            eventMap.put("command", command);
            for (PageLine line : pageNote.getLines()) {
               // add inputs as event attributes
               String input = line.getMap().get("input");
               if (input != null) {
                  String fill = line.getMap().get("fill");
                  if (fill == null) {
                     fill = "somevalue";
                  }
                  eventMap.put(input, fill);
               }
            }
            String eventDescription = getEventId(eventMap);
            String eventTime = getEventTime(eventDescription);
            String eventTypeName = getEventTypeName(eventDescription) + "Command";
            commandNote.setTime(eventTime);
            commandNote.setEventTypeName(eventTypeName);
            commandNote.setWorkflow(getRootWorkflow());
            commandNote.setMap(eventMap);
            commandNote.setRaisingPage(pageNote);

            EventType eventType = getEventStormingBoard().getOrCreateEventType(commandNote.getEventTypeName());
            eventType.withEvents(commandNote);

            lastEvent = commandNote;
            addToStepsOfLastActor(commandNote);
         }
      }
   }

   private void fillEventNote(LinkedHashMap<String, String> map, EventNote eventNote)
   {
      String value = getEventId(map); // example value: product stored 12:00
      String eventTime = getEventTime(value);
      eventNote.setTime(eventTime);
      String suffix = eventNote instanceof CommandNote ? "Command" : "Event";
      String eventTypeName = getEventTypeName(value) + suffix;
      eventNote.setEventTypeName(eventTypeName);
      eventNote.setWorkflow(getRootWorkflow());
      eventNote.setMap(map);

      EventType eventType = getEventStormingBoard().getOrCreateEventType(eventNote.getEventTypeName());
      eventType.withEvents(eventNote);

      lastEvent = eventNote;
      addToStepsOfLastActor(eventNote);
   }

   private String getEventTime(String value)
   {
      String[] split = value.split("\\s");
      String eventTime = split[split.length - 1];
      return eventTime;
   }

   public String getEventTypeName(String value)
   {
      String[] split2 = value.split("\\s");
      String eventTypeName = "";
      for (int i = 0; i < split2.length - 1; i++) {
         eventTypeName += org.fulib.StrUtil.cap(split2[i]);
      }
      return eventTypeName;
   }

   public String getVarName(String value)
   {
      String[] split2 = value.split("\\s");
      String eventTypeName = split2[0];
      for (int i = 1; i < split2.length; ++i) {
         eventTypeName += org.fulib.StrUtil.cap(split2[i]);
      }
      return eventTypeName;
   }

   private void addToStepsOfLastActor(WorkflowNote note)
   {
      if (note instanceof EventNote || note instanceof PageNote || note instanceof BrokerTopicNote) {
         if (lastUser == null) {
            UserNote somebody = getEventStormingBoard().getOrCreateFromUsers("somebody");
            lastUser = (UserInteraction) new UserInteraction().setWorkflow(getRootWorkflow()).setUser(somebody).setActorName("somebody");
         }
         if (lastActor == null) {
            lastActor = lastUser;
         }
         if (lastActor instanceof Policy && note instanceof PageNote) {
            lastActor = new UserInteraction().setWorkflow(getRootWorkflow()).setUser(lastUser.getUser()).setActorName(lastUser.getActorName());
         }
         if (note instanceof PageNote) {
            String actorName = lastActor.getActorName();
         }
      }
      else if ((note instanceof DataNote) || (note instanceof ClassNote) || (note instanceof QueryNote)) {
         if (lastService == null) {
            lastService = getEventStormingBoard().getOrCreateFromServices("someservice");
         }
         if (lastActor == null || !(lastActor instanceof Policy)) {
            Interaction action = new Policy()
                  .setWorkflow(getRootWorkflow())
                  .setService(lastService)
                  .setTrigger(lastEvent)
                  .setActorName(lastService.getName());
            lastService.withHandledEventTypes(lastEvent.getType());
            lastActor = action;
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
