package org.fulib.workflows;

import org.fulib.yaml.Yamler2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
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
   private ExternalSystemNote externalSystemNote = null;

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

         while (!maps.isEmpty()) {
            LinkedHashMap<String, String> map = maps.remove(0);
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
               lastService = note;
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
               maps = buildPolicy(fileName, maps, map, entry);
               continue;
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
            else if (entry.getKey().equalsIgnoreCase("externalsystem")) {
               String value = entry.getValue();
               ExternalSystemNote externalSystemNote = new ExternalSystemNote();
               String timeInterval = map.get("events");
               externalSystemNote.setMap(map);
               externalSystemNote.setTimeInterval(timeInterval);
               externalSystemNote.setTime(timeInterval);
               externalSystemNote.setWorkflow(getRootWorkflow());
               externalSystemNote.setSystemName(getTypeName(value));
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

      generateExports(fileName);

      return getEventStormingBoard();
   }

   private ArrayList<LinkedHashMap<String, String>> buildPolicy(String fileName, ArrayList<LinkedHashMap<String, String>> maps, LinkedHashMap<String, String> map, Map.Entry<String, String> entry)
   {
      String oneYaml;
      Policy policy = new Policy();
      policy.setActorName(entry.getValue());
      policy.setWorkflow(getRootWorkflow());
      ServiceNote service = getEventStormingBoard().getOrCreateFromServices(entry.getValue());
      policy.setService(service);
      String triggerTime = map.get("trigger");
      if (triggerTime != null) {
         WorkflowNote trigger = getRootWorkflow().getFromNotes(triggerTime);
         if (trigger instanceof EventNote) {
            EventNote eventNote = (EventNote) trigger;
            policy.setTrigger(eventNote);
            EventType type = eventNote.getType();
            service.withHandledEventTypes(type);
         }
         else {
            ExternalSystemNote externalSystemNote = (ExternalSystemNote) trigger;
            policy.setExternalSystem(externalSystemNote);

            // insert imported data events
            String importedFileName = fileName.substring(0, fileName.lastIndexOf('/'));
            String exportName = externalSystemNote.getSystemName();
            importedFileName = String.format("%s/export%s.es.yaml", importedFileName, exportName);
            try {
               oneYaml = Files.readString(Path.of(importedFileName));
            }
            catch (IOException e) {
               return null;
            }

            ArrayList<LinkedHashMap<String, String>> importedMaps = new Yamler2().decodeList(oneYaml);
            importedMaps.addAll(maps);
            maps = importedMaps;
         }
      }
      lastService = service;
      lastActor = policy;
      return maps;
   }

   private void generateExports(String fileName)
   {
      StringBuilder body = new StringBuilder();
      String currentTopic = null;
      for (Workflow workflow : getEventStormingBoard().getWorkflows()) {
         for (WorkflowNote note : workflow.getNotes()) {
            if (note instanceof BrokerTopicNote) {
               BrokerTopicNote brokerTopicNote = (BrokerTopicNote) note;
               currentTopic = brokerTopicNote.getBrokerName();
               continue;
            }

            if (note instanceof DataNote) {
               String oneNote = mapToYaml(note.getMap());
               body.append(oneNote);
            }
         }
      }

      if (currentTopic == null) {
         return;
      }

      int lastIndexOf = fileName.lastIndexOf('/');
      fileName = fileName.substring(0, lastIndexOf);
      fileName = String.format("%s/export%s.es.yaml", fileName, currentTopic);
      try {
         Files.writeString(Path.of(fileName), body.toString(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
      }
      catch (IOException e) {
         e.printStackTrace();
      }
   }

   private String mapToYaml(LinkedHashMap<String, String> map)
   {
      StringBuilder body = new StringBuilder();
      String indent = "- ";
      for (Map.Entry<String, String> entry : map.entrySet()) {
         body.append(String.format("%s%s: %s\n", indent, entry.getKey(), entry.getValue()));
         indent = "  ";
      }
      return body.toString() + "\n";
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
         objectId = getObjectId(objectId);
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
      // note.setWorkflow(getRootWorkflow());
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
            String eventTime = getEventTime(eventDescription, "00:00:01");
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
      String eventTime = getEventTime(value, "00:01:00");
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

   private String getEventTime(String value, String delta)
   {
      String[] split = value.split("\\s");
      String eventTime = split[split.length - 1];
      if (eventTime.indexOf(':') < 0) {
         // no time given, use auto time and set class name
         eventTime = getRootWorkflow().addToTime(delta);
         value = value + " " + eventTime;
         split = StrUtil.split(value);
      }
      else {
         getRootWorkflow().setCurrentTime(eventTime);
      }
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

   public String getTypeName(String value)
   {
      String[] split2 = value.split("\\s");
      String typeName = "";
      for (int i = 0; i < split2.length; ++i) {
         typeName += org.fulib.StrUtil.cap(split2[i]);
      }
      return typeName;
   }

   public String getVarName(String value)
   {
      String[] split2 = value.split("\\s");
      String varName = StrUtil.decap(split2[0]);
      for (int i = 1; i < split2.length; ++i) {
         varName += org.fulib.StrUtil.cap(split2[i]);
      }
      return varName;
   }

   public String getObjectId(String objectId)
   {
      if (objectId == null) {
         return null;
      }
      return objectId.replaceAll("\\W+", "_");
   }

   private void addToStepsOfLastActor(WorkflowNote note)
   {
      if (note instanceof EventNote || note instanceof PageNote || note instanceof CommandNote || note instanceof BrokerTopicNote) {
         if (lastUser == null) {
            UserNote somebody = getEventStormingBoard().getOrCreateFromUsers("somebody");
            lastUser = (UserInteraction) new UserInteraction().setWorkflow(getRootWorkflow()).setUser(somebody).setActorName("somebody");
         }
         if (lastActor == null) {
            lastActor = lastUser;
         }
         if (lastActor instanceof Policy && (note instanceof PageNote || note instanceof CommandNote)) {
            lastActor = new UserInteraction().setWorkflow(getRootWorkflow()).setUser(lastUser.getUser()).setActorName(lastUser.getActorName());
         }
         if (note instanceof PageNote || note instanceof CommandNote) {
            String actorName = lastActor.getActorName();
         }
      }
      else if ((note instanceof DataNote) || (note instanceof ClassNote) || (note instanceof QueryNote)) {
         if (lastEvent == null && externalSystemNote == null) {
            externalSystemNote = new ExternalSystemNote();
            externalSystemNote.setTime("11:00:00");
            externalSystemNote.setWorkflow(getRootWorkflow());
            externalSystemNote.setSystemName("someExternalSystem");
            LinkedHashMap<String, String> exMap = new LinkedHashMap<>();
            exMap.put("externalsystem", "someExternalsystem");
            externalSystemNote.setMap(exMap);
         }
         if (lastService == null) {
            lastService = getEventStormingBoard().getOrCreateFromServices("someservice");
         }
         if (lastActor == null || !(lastActor instanceof Policy)) {
            if (lastEvent != null) {
               Interaction action = new Policy()
                     .setWorkflow(getRootWorkflow())
                     .setService(lastService)
                     .setTrigger(lastEvent)
                     .setActorName(lastService.getName());
               lastService.withHandledEventTypes(lastEvent.getType());
               lastActor = action;
            }
            else {
               // add external service
               Interaction action = new Policy()
                     .setWorkflow(getRootWorkflow())
                     .setService(lastService)
                     .setExternalSystem(externalSystemNote)
                     .setActorName(lastService.getName());
               lastActor = action;
            }
         }
         note.setWorkflow(getRootWorkflow());
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
