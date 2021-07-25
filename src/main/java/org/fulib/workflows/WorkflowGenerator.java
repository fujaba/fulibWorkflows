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
import java.util.function.BiConsumer;
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

   public BiConsumer<String, Object> dumpObjectDiagram;
   private EventStormingBoard eventStormingBoard;
   private StringBuilder testBody;

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
      eventStormingBoard = eventModel.buildEventStormModel(yaml);
      // dumpObjectDiagram.accept("tmp/afterBuildEventStormModel.svg", rootWorkflow);
      buildClassModelManagerMap(mm);
      buildEventBroker();
      buildServices();
      buildTest();

      buildEventClasses();

      return this;
   }


   private void buildServices()
   {
      ClassModelManager modelManager = null;

      for (ServiceNote serviceNote : eventStormingBoard.getServices()) {
         // each service gets its own package
         // build classModelManager for that package
         String port = serviceNote.getPort();

         Map<String, String> map = serviceNote.getMap();
         if (map != null) {
            map.get("port");
         }
         String serviceName = serviceNote.getName();
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
         modelManager.haveAttribute(serviceClazz, "port", Type.INT, port);
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
         st.add("port", port);
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
      ServiceNote service = eventStormingBoard.getFromServices(serviceName);
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

      ServiceNote serviceNote = eventStormingBoard.getFromServices(serviceName);
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
         if (note instanceof DataNote) {
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
         else {
            // fire event
            EventNote eventNote = (EventNote) note;
            String varName = eventNote.getTime().replaceAll("\\:", "");
            body.append(String.format("\n   %s e%s = new %1$s();\n",
                  eventNote.getEventTypeName(), varName));

            LinkedHashMap<String, String> map = eventNote.getMap();
            LinkedHashMap<String, String> clone = (LinkedHashMap<String, String>) map.clone();
            clone.remove(eventNote.getEventTypeName());
            for (Map.Entry<String, String> entry : clone.entrySet()) {
               String setterName = org.fulib.StrUtil.cap(entry.getKey());
               String statement = String.format("   e%s.set%s(\"%s\");\n",
                     varName, setterName, entry.getValue());
               body.append(statement);
            }
            body.append(String.format("   publish(e%s);\n",
                  varName));
         }
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
      tm = new ClassModelManager().setMainJavaDir(mm.getClassModel().getMainJavaDir().replace("/main/java", "/test/java"))
            .setPackageName(mm.getClassModel().getPackageName());
      managerMap.put("tm", tm);

      String boardName = StrUtil.toIdentifier(eventStormingBoard.getName());
      testClazz = tm.haveClass("Test" + boardName);
      testClazz.withImports("import org.junit.Test;");
      testClazz.withImports(String.format("import %s;",
            em.getClassModel().getPackageName() + ".*"));
      tm.haveAttribute(testClazz, "eventBroker", "EventBroker");

      String declaration = "@Test\n" +
            "public void " + StrUtil.toIdentifier(eventModel.getEventStormingBoard().getName()) + "()";

      startServices();

      for (Workflow workflow : eventStormingBoard.getWorkflows()) {
         testBody.append("\n// workflow " + workflow.getName());
         for (WorkflowNote note : workflow.getNotes()) {
            // Send user events
            if (note instanceof EventNote) {
               EventNote eventNote = (EventNote) note;
               Interaction interaction = eventNote.getInteraction();
               if (interaction instanceof UserInteraction) {
                  testGenerateSendUserEvent(testBody, eventNote);
               }
            }
         }
      }

      testBody.append("\nSystem.out.println();\n");

      tm.haveMethod(testClazz, declaration, testBody.toString());

      // add publish method to the test class
      declaration = "public void publish(Event event)";
      testBody.setLength(0);
      ST st;
      st = group.getInstanceOf("publishBody");
      testBody.append(st.render());
      tm.haveMethod(testClazz, declaration, testBody.toString());
      testClazz.withImports("import org.fulib.yaml.Yaml;",
            "import com.mashape.unirest.http.HttpResponse;",
            "import com.mashape.unirest.http.Unirest;",
            "import com.mashape.unirest.http.exceptions.UnirestException;",
            "import static com.codeborne.selenide.Selenide.open;",
            "import static com.codeborne.selenide.Selenide.$;");
   }

   private void startServices()
   {
      testBody = new StringBuilder();

      ST st = group.getInstanceOf("startEventBroker");
      testBody.append(st.render());

      for (ServiceNote service : eventStormingBoard.getServices()) {
         testGenerateServiceStart(testBody, service);
      }

      // validate that the event broker knows the services.

      testBody.append("\nopen(\"http://localhost:42000\");\n");
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

   private void testGenerateSendUserEvent(StringBuilder body, EventNote note)
   {
      // yes this event shall be send by a user, i.e. by our test
      // build it
      String varName = addCreateAndInitEventCode(note, body);
      String statement;

      // send it
      statement = String.format("publish(%s);\n", varName);
      body.append(statement);
   }

   private String addCreateAndInitEventCode(EventNote note, StringBuilder body)
   {
      boolean first = true;
      String varName = null;
      String id = null;
      String statement = null;
      String eventTypeName = null;
      for (Map.Entry<String, String> entry : note.getMap().entrySet()) {
         if (first) {
            String value = entry.getValue(); // example value: product stored 12:00
            String[] split = value.split("\\s");
            String time = note.getTime();
            eventTypeName = note.getEventTypeName();
            id = time;
            varName = time.replaceAll("\\:", "");
            if (!Character.isAlphabetic(varName.charAt(0))) {
               varName = "e" + varName;
            }
            statement = String.format("\n" +
                        "// create %s: %s\n",
                  eventTypeName, entry.getValue());
            body.append(statement);
            statement = String.format("%s %s = new %s();\n",
                  eventTypeName, varName, eventTypeName);
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


   private void buildClassModelManagerMap(ClassModelManager mm)
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
      for (Workflow workflow : eventStormingBoard.getWorkflows()) {
         for (WorkflowNote note : workflow.getNotes()) {
            if (note instanceof EventNote) {
               EventNote eventNote = (EventNote) note;
               oneEventClass(eventNote);
            }
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
