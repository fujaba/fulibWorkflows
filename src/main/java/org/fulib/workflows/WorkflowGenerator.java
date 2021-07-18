package org.fulib.workflows;

import org.fulib.Fulib;
import org.fulib.FulibTools;
import org.fulib.builder.ClassModelManager;
import org.fulib.builder.Type;
import org.fulib.classmodel.Clazz;
import org.fulib.tables.ObjectTable;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroupFile;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WorkflowGenerator
{
   private ClassModelManager mm;
   private ClassModelManager em;
   private ClassModelManager tm;
   private Clazz modelClazz;
   private LinkedHashMap<String, ClassModelManager> managerMap;
   private STGroupFile group;
   private Clazz testClazz;
   private EventModel eventModel;
   private Workflow rootWorkflow;

   public EventModel getEventModel()
   {
      return eventModel;
   }

   public WorkflowGenerator generateWorkflow(ClassModelManager mm, String yaml)
   {
      loadWorkflow(mm, yaml);
      generate();
      return this;
   }

   public WorkflowGenerator loadWorkflow(ClassModelManager mm, String yaml)
   {
      this.mm = mm;

      group = new STGroupFile(this.getClass().getResource("templates/Workflows.stg"));

      // event map
      eventModel = new EventModel();
      eventModel.buildEventMap(yaml);
      rootWorkflow = eventModel.getRootWorkflow();
      buildManagerMaps(mm);
      buildEventBroker();
      buildHandlerMaps();
      buildServices();
      buildTest();

      buildEventClasses();

      return this;
   }

   private void buildHandlerMaps()
   {
      for (WorkflowNote note : rootWorkflow.getNotes()) {

         // find Data entries
         Map<String, String> map = note.getMap();
         String key = eventModel.getEventType(map);

         if (!(note instanceof DataNote)) {
            continue;
         }
         String serviceId = note.getInteraction().getActorName();
         String dataId = note.getTime();
         Policy policy = (Policy) note.getInteraction();
         String eventId = policy.getTrigger().getTime();
         // find corresponding event
         LinkedHashMap<String, String> event = eventModel.eventMap.get(eventId);
         String eventType = eventModel.getEventType(event);


      }
   }


   private void buildServices()
   {
      ClassModelManager modelManager = null;

      for (ServiceNote serviceNote : rootWorkflow.getServices()) {
         // each service gets its own package
         // build classModelManager for that package
         Map<String, String> map = serviceNote.getMap();
         String serviceName = map.get("name");
         modelManager = new ClassModelManager().setMainJavaDir(mm.getClassModel().getMainJavaDir())
               .setPackageName(mm.getClassModel().getPackageName() + "." + serviceName);
         managerMap.put(serviceName, modelManager);

         // add ModelClass
         Clazz modelClazz = modelManager.haveClass(serviceName + "Model");
         modelClazz.withImports("import java.util.LinkedHashMap;");
         modelManager.haveAttribute(modelClazz,
               "modelMap",
               "LinkedHashMap<String, Object>",
               "new LinkedHashMap<>()");

         // add ServiceClass
         Clazz serviceClazz = modelManager.haveClass(serviceName + "Service");
         serviceClazz.withImports("import java.util.LinkedHashMap;",
               "import java.util.Map;",
               "import java.util.function.Consumer;",
               "import " + em.getClassModel().getPackageName() + ".*;",
               "import org.fulib.yaml.Yaml;",
               "import spark.Service;",
               "import spark.Request;",
               "import spark.Response;",
               "import com.mashape.unirest.http.HttpResponse;",
               "import com.mashape.unirest.http.Unirest;",
               "import com.mashape.unirest.http.exceptions.UnirestException;",
               "import java.util.concurrent.ExecutorService;",
               "import java.util.concurrent.Executors;",
               "import java.util.logging.Logger;",
               "import java.util.logging.Level;");
         modelManager.haveAttribute(serviceClazz, "history",
               "LinkedHashMap<String, Event>",
               "new LinkedHashMap<>()");
         modelManager.haveAttribute(serviceClazz, "port", Type.INT, map.get("port"));
         modelManager.haveAttribute(serviceClazz, "spark", "Service");
         modelManager.haveAttribute(serviceClazz, "model", serviceName + "Model");
         modelManager.haveAttribute(serviceClazz, "handlerMap",
               "LinkedHashMap<Class, Consumer<Event>>", null);

         // add start method
         String declaration = "public void start()";
         StringBuilder body = new StringBuilder();
         body.append(String.format("model = new %sModel();\n", serviceName));
         body.append("ExecutorService executor = Executors.newSingleThreadExecutor();\n");
         body.append("spark = Service.ignite();\n");
         body.append("spark.port(port);\n");
         body.append("spark.get(\"/\", (req, res) -> executor.submit(() -> this.getHello(req, res)).get());\n");
         body.append("spark.post(\"/apply\", (req, res) -> executor.submit(() -> this.postApply(req, res)).get());\n");
         body.append("executor.submit(this::subscribeAndLoadOldEvents);\n");
         body.append(
               String.format("Logger.getGlobal().info(\"%s service is up and running on port \" + port);\n",
                     serviceName));
         modelManager.haveMethod(serviceClazz, declaration, body.toString());

         // add getHello
         declaration = "private String getHello(Request req, Response res)";
         body.setLength(0);
         ST st = group.getInstanceOf("serviceGetHelloBody");
         st.add("name", serviceName);
         body.append(st.render());
         modelManager.haveMethod(serviceClazz, declaration, body.toString());

         // add subscribeAndLoadOldEvents
         declaration = "private void subscribeAndLoadOldEvents()";
         body.setLength(0);
         st = group.getInstanceOf("serviceSubscribeAndLoadOldEvents");
         st.add("port", map.get("port"));
         body.append(st.render());
         modelManager.haveMethod(serviceClazz, declaration, body.toString());

         // apply method
         declaration = "public void apply(Event event)";
         body.setLength(0);
         st = group.getInstanceOf("serviceApply");
         body.append(st.render());
         modelManager.haveMethod(serviceClazz, declaration, body.toString());

         buildInitEventHandlerMapMethod(modelManager, serviceName, serviceClazz, body);

         // ignoreEvents method
         declaration = "private void ignoreEvent(Event event)";
         body.setLength(0);
         body.append("// empty\n");
         modelManager.haveMethod(serviceClazz, declaration, body.toString());

         // publish method
         declaration = "public void publish(Event event)";
         body.setLength(0);
         st = group.getInstanceOf("servicePublish");
         body.append(st.render());
         modelManager.haveMethod(serviceClazz, declaration, body.toString());

         // postApply method
         declaration = "private String postApply(Request req, Response res)";
         body.setLength(0);
         st = group.getInstanceOf("servicePostApply");
         body.append(st.render());
         modelManager.haveMethod(serviceClazz, declaration, body.toString());
      }
   }

   private void buildInitEventHandlerMapMethod(ClassModelManager modelManager, String serviceName, Clazz serviceClazz, StringBuilder body)
   {
      String declaration;
      // initHandlerMap
      declaration = "private void initEventHandlerMap()";
      body.setLength(0);
      body.append("if (handlerMap == null) {\n");
      body.append("   handlerMap = new LinkedHashMap<>();\n");
      addHandlersToInitEventHandlerMap(modelManager, serviceClazz, serviceName, body);
      body.append("}\n");
      modelManager.haveMethod(serviceClazz, declaration, body.toString());
   }

   private void addHandlersToInitEventHandlerMap(ClassModelManager modelManager, Clazz serviceClazz, String serviceName, StringBuilder body)
   {
      ServiceNote service = rootWorkflow.getFromServices(serviceName);
      for (EventType eventType : service.getHandledEventTypes()) {
         String eventTypeName = eventType.getEventTypeName();
         body.append(String.format("   handlerMap.put(%s.class, this::handle%s);\n",
               eventTypeName, eventTypeName));
         addEventHandlerMethod(modelManager, serviceClazz, serviceName, eventType);
      }
   }

   private void addEventHandlerMethod(ClassModelManager modelManager, Clazz serviceClazz, String serviceName, EventType eventType)
   {
      StringBuilder body = new StringBuilder();
      String eventTypeName = eventType.getEventTypeName();
      String declaration = String.format("private void handle%s(Event e)", eventTypeName);
      body.append(String.format("%s event = (%s) e;\n", eventTypeName, eventTypeName));

      ServiceNote serviceNote = rootWorkflow.getFromServices(serviceName);
      ObjectTable<Object> table = new ObjectTable<>("service", serviceNote);
      LinkedHashSet<Object> policies = table.expandLink("eventType", ServiceNote.PROPERTY_HANDLED_EVENT_TYPES)
            .filter(et -> et == eventType)
            .expandLink("event", EventType.PROPERTY_EVENTS)
            .expandLink("policy", EventNote.PROPERTY_POLICIES)
            .filter(p -> ((Policy) p).getService() == serviceNote)
            .toSet();

      for (Object obj : policies) {
         Policy policy = (Policy) obj;
         EventNote triggerEvent = policy.getTrigger();
         String eventId = triggerEvent.getTime();
         body.append(String.format("if (event.getId().equals(\"%s\")) {\n", eventId));
         addMockupData(modelManager, serviceName, policy, body);
         body.append("}\n");
      }
      modelManager.haveMethod(serviceClazz, declaration, body.toString());
   }

   private void addMockupData(ClassModelManager modelManager, String serviceName, Policy policy, StringBuilder body)
   {
      for (WorkflowNote note : policy.getSteps()) {
         //    Example
         //      - StorageData: 12:00:01
         //        Box: box23
         //        product: shoes
         //        place: shelf23
         LinkedHashMap<String, String> map = note.getMap();
         LinkedHashMap<String, String> mockup = (LinkedHashMap<String, String>) ((LinkedHashMap<String, String>) map).clone();
         Map.Entry<String, String> firstEntry = mockup.entrySet().iterator().next();
         mockup.remove(firstEntry.getKey());
         addModelClass(modelManager, mockup);
         addGetOrCreateMethodToServiceModel(modelManager, serviceName, mockup);
         addCreateAndInitModelObjectCode(mockup, body);
      }
   }

   private void addGetOrCreateMethodToServiceModel(ClassModelManager modelManager, String serviceName, LinkedHashMap<String, String> mockup)
   {
      String type = org.fulib.StrUtil.cap(eventModel.getEventType(mockup));
      Clazz modelClazz = modelManager.haveClass(serviceName + "Model");
      String declaration = String.format("public %s getOrCreate%s(String id)", type, type);
      String body = String.format("return (%s) modelMap.computeIfAbsent(id, k -> new %s().setId(k));\n"
            , type, type);
      modelManager.haveMethod(modelClazz, declaration, body);
   }

   private void addModelClass(ClassModelManager modelManager, LinkedHashMap<String, String> map)
   {
      Iterator<Map.Entry<String, String>> iter = map.entrySet().iterator();
      Map.Entry<String, String> entry = iter.next();
      String type = entry.getKey();
      type = org.fulib.StrUtil.cap(type);
      Clazz clazz = modelManager.haveClass(type);
      modelManager.haveAttribute(clazz, "id", Type.STRING);

      while (iter.hasNext()) {
         entry = iter.next();
         modelManager.haveAttribute(clazz, entry.getKey(), Type.STRING);
      }
   }


   private String addCreateAndInitModelObjectCode(LinkedHashMap<String, String> map, StringBuilder body)
   {
      boolean first = true;
      String varName = null;
      String id = null;
      String eventType = null;
      String statement = null;
      for (Map.Entry<String, String> entry : map.entrySet()) {
         if (first) {
            eventType = org.fulib.StrUtil.cap(entry.getKey());
            id = entry.getValue();
            varName = org.fulib.StrUtil.downFirstChar(id);
            statement = String.format("\n" +
                        "   %s %s = model.getOrCreate%s(\"%s\");\n",
                  eventType, varName, eventType, id);
            body.append(statement);
            first = false;
            continue;
         }

         String setterName = org.fulib.StrUtil.cap(entry.getKey());
         statement = String.format("   %s.set%s(\"%s\");\n",
               varName, setterName, entry.getValue());
         body.append(statement);
      }
      return varName;
   }

   private void buildEventBroker()
   {
      try {
         InputStream resource = this.getClass().getResourceAsStream("templates/EventBroker.java");
         BufferedInputStream buf = new BufferedInputStream(resource);
         byte[] bytes = buf.readAllBytes();
         String content = new String(bytes, StandardCharsets.UTF_8);
         content = content.replace("package uks.dpst21.events;",
               "package " + em.getClassModel().getPackageName() + ";");
         String eventBrokerName = getPackageDirName(em) + "/EventBroker.java";
         Logger.getGlobal().info("EventBroker file generated " + eventBrokerName);
         Files.write(Path.of(eventBrokerName), content.getBytes(StandardCharsets.UTF_8));
      }
      catch (IOException e) {
         Logger.getGlobal().log(Level.SEVERE, "could not read resource templates/Eventbroker.java", e);
      }

   }

   private void buildTest()
   {
      if (eventModel.workflowName == null) {
         return;
      }

      tm = new ClassModelManager().setMainJavaDir(mm.getClassModel().getMainJavaDir().replace("/main/java", "/test/java"))
            .setPackageName(mm.getClassModel().getPackageName());
      managerMap.put("tm", tm);

      testClazz = tm.haveClass("Test" + rootWorkflow.getName());
      testClazz.withImports("import org.junit.Test;");
      testClazz.withImports(String.format("import %s;",
            em.getClassModel().getPackageName() + ".*"));
      tm.haveAttribute(testClazz, "eventBroker", "EventBroker");

      StringBuilder body = new StringBuilder();
      String declaration = "@Test\n" +
            "public void " + eventModel.workflowName + "()";
      ST st = group.getInstanceOf("startEventBroker");
      body.append(st.render());

      for (ServiceNote service : rootWorkflow.getServices()) {
         testGenerateServiceStart(body, service);
      }

      for (WorkflowNote note : rootWorkflow.getNotes()) {
         // Send user events
         if (note instanceof EventNote) {
            EventNote eventNote = (EventNote) note;
            Interaction interaction = eventNote.getInteraction();
            if (interaction instanceof UserInteraction) {
               testGenerateSendUserEvent(body, eventNote.getMap());
            }
         }
      }

      body.append("System.out.println();\n");

      tm.haveMethod(testClazz, declaration, body.toString());

      // add publish method to the test class
      declaration = "public void publish(Event event)";
      body.setLength(0);
      st = group.getInstanceOf("publishBody");
      body.append(st.render());
      tm.haveMethod(testClazz, declaration, body.toString());
      testClazz.withImports("import org.fulib.yaml.Yaml;",
            "import com.mashape.unirest.http.HttpResponse;",
            "import com.mashape.unirest.http.Unirest;",
            "import com.mashape.unirest.http.exceptions.UnirestException;");
   }

   private void testGenerateServiceStart(StringBuilder body, ServiceNote serviceNote)
   {
      String serviceName = serviceNote.getName();
      String imp = String.format("import %s.%s.%sService;",
            mm.getClassModel().getPackageName(), serviceName, serviceName);
      testClazz.withImports(imp);

      body.append("\n");
      String serviceVarName = org.fulib.StrUtil.downFirstChar(serviceName);
      body.append("// start service\n");
      body.append(String.format("%sService %s = new %sService();\n",
            serviceName, serviceVarName, serviceName));
      body.append(String.format("%s.start();\n", serviceVarName));
   }

   private void testGenerateSendUserEvent(StringBuilder body, LinkedHashMap<String, String> map)
   {
      // yes this event shall be send by a user, i.e. by our test
      // build it
      String varName = addCreateAndInitEventCode(map, body);
      String statement;

      // send it
      statement = String.format("publish(%s);\n", varName);
      body.append(statement);
   }

   private String addCreateAndInitEventCode(LinkedHashMap<String, String> map, StringBuilder body)
   {
      boolean first = true;
      String varName = null;
      String id = null;
      String eventType = null;
      String statement = null;
      for (Map.Entry<String, String> entry : map.entrySet()) {
         if (first) {
            eventType = entry.getKey();
            id = entry.getValue();
            varName = entry.getValue().replaceAll("\\:", "");
            if (!Character.isAlphabetic(varName.charAt(0))) {
               varName = "e" + varName;
            }
            statement = String.format("\n" +
                        "// create %s: %s\n",
                  eventType, entry.getValue());
            body.append(statement);
            statement = String.format("%s %s = new %s();\n",
                  eventType, varName, eventType);
            body.append(statement);
            statement = String.format("%s.setId(\"%s\");\n",
                  varName, id);
            body.append(statement);
            first = false;
            continue;
         }

         String setterName = org.fulib.StrUtil.cap(entry.getKey());
         statement = String.format("%s.set%s(\"%s\");\n",
               varName, setterName, entry.getValue());
         body.append(statement);
      }
      return varName;
   }


   private void buildManagerMaps(ClassModelManager mm)
   {
      managerMap = new LinkedHashMap<>();
      managerMap.put("mm", mm);

      em = new ClassModelManager().setMainJavaDir(mm.getClassModel().getMainJavaDir())
            .setPackageName(mm.getClassModel().getPackageName() + ".events");
      managerMap.put("em", em);

      Clazz event = em.haveClass("Event");
      em.haveAttribute(event, "id", Type.STRING);
      Clazz serviceSubscribed = em.haveClass("ServiceSubscribed");
      serviceSubscribed.setSuperClass(event);
      em.haveAttribute(serviceSubscribed, "serviceUrl", Type.STRING);
   }


   private void buildEventClasses()
   {
      for (WorkflowNote note : rootWorkflow.getNotes()) {
         if (note instanceof EventNote) {
            EventNote eventNote = (EventNote) note;
            oneEventClass(eventNote);
         }
      }
   }


   private void oneEventClass(EventNote note)
   {
      Clazz event = em.haveClass("Event");
      boolean first = true;
      Clazz clazz = em.haveClass(note.getEventTypeName());
      clazz.setSuperClass(event);
      LinkedHashSet<String> keys = new LinkedHashSet<>(note.getMap().keySet());
      keys.remove(note.getEventTypeName());
      for (String key : keys) {
         mm.haveAttribute(clazz, key, "String");
      }
   }

//   private void generateModelClass(LinkedHashMap<String, String> map)
//   {
//      String name = map.get("name");
//      modelClazz = mm.haveClass(name + "Model");
//      modelClazz.withImports("import java.util.LinkedHashMap;");
//      mm.haveAttribute(modelClazz,
//            "objectMap",
//            "LinkedHashMap<String, Object>",
//            "new LinkedHashMap<>()");
//
//      String events = map.get("events");
//      String[] split = events.split(" ");
//      for (String event : split) {
//         generateModelElementsFor(event);
//      }
//      Logger.getGlobal().info(String.format("%s events are %s",
//            modelClazz.getName(),
//            events));
//   }

   private void generateModelElementsFor(String event)
   {
      // event handler
      int index = StrUtil.indexOfLastUpperChar(event);
      String dataClassName = event.substring(0, index);

      // data class
      Clazz dataClazz = mm.haveClass(dataClassName);
      mm.haveAttribute(dataClazz, "id", "String");

      // getOrCreate method
      String declaration = String.format("public %s getOrCreate%s(String id)",
            dataClassName, dataClassName);
      String body = String.format("" +
                  "Object obj = objectMap.computeIfAbsent(id, k -> new %s().setId(k));\n" +
                  "return (%s) obj;",
            dataClassName,
            dataClassName);
      mm.haveMethod(modelClazz, declaration, body);
   }

   public WorkflowGenerator generate()
   {
      for (ClassModelManager manager : managerMap.values()) {
         Fulib.generator().generate(manager.getClassModel());
         String classDiagramName = getPackageDirName(manager) + "/classDiagram.svg";
         FulibTools.classDiagrams().dumpSVG(manager.getClassModel(), classDiagramName);
      }

      return this;
   }

   private String getPackageDirName(ClassModelManager manager)
   {
      String packageDirName = manager.getClassModel().getPackageName().replaceAll("\\.", "/");
      packageDirName = manager.getClassModel().getMainJavaDir() + "/" + packageDirName;
      return packageDirName;
   }
}
