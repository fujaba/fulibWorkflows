package org.fulib.workflows;

import org.fulib.Fulib;
import org.fulib.FulibTools;
import org.fulib.builder.ClassModelManager;
import org.fulib.builder.Type;
import org.fulib.builder.reflect.Link;
import org.fulib.classmodel.Clazz;
import org.fulib.yaml.Yamler;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroupFile;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WorkflowGenerator
{
   private ClassModelManager mm;
   private ClassModelManager em;
   private ClassModelManager tm;
   private Clazz modelClazz;
   private LinkedHashMap<String, ClassModelManager> managerMap;
   private TreeMap<String, LinkedHashMap<String, String>> eventMap;
   private LinkedHashMap<String, LinkedHashMap<String, String>> userMap;
   private LinkedHashMap<String, LinkedHashMap<String, String>> serviceMap;
   private String workflowName;
   private STGroupFile group;
   private Clazz testClazz;

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
      buildEventMap(yaml);
      buildManagerMaps(mm);
      buildEventBroker();
      buildServices();
      buildTest();

      for (Map.Entry<String, LinkedHashMap<String, String>> entry : eventMap.entrySet()) {
         oneEventClass(entry.getValue());
      }

      System.out.println();
      return this;
   }

   private void buildServices()
   {
      ClassModelManager modelManager = null;

      for (Map.Entry<String, LinkedHashMap<String, String>> entry : serviceMap.entrySet()) {
         // each service gets its own package
         // build classModelManager for that package
         LinkedHashMap<String, String> map = entry.getValue();
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

         // initHandlerMap
         declaration = "private void initEventHandlerMap()";
         body.setLength(0);
         body.append("if (handlerMap == null) {\n");
         body.append("handlerMap = new LinkedHashMap<>();\n");
         body.append("   // add handlers for interesting events\n");
         body.append("}\n");
         modelManager.haveMethod(serviceClazz, declaration, body.toString());

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
      if (workflowName == null) {
         return;
      }

      tm = new ClassModelManager().setMainJavaDir(mm.getClassModel().getMainJavaDir().replace("/main/java", "/test/java"))
            .setPackageName(mm.getClassModel().getPackageName());
      managerMap.put("tm", tm);

      testClazz = tm.haveClass("Test" + workflowName);
      testClazz.withImports("import org.junit.Test;");
      testClazz.withImports(String.format("import %s;",
            em.getClassModel().getPackageName() + ".*"));
      tm.haveAttribute(testClazz, "eventBroker", "EventBroker");

      StringBuilder body = new StringBuilder();
      String declaration = "@Test\n" +
            "public void " + workflowName + "()";
      ST st = group.getInstanceOf("startEventBroker");
      body.append(st.render());

      for (LinkedHashMap<String, String> map : eventMap.values()) {
         // Send user events, start services, control event lists and object models
         String user = map.get("user");
         if (user != null && userMap.get(user) != null) {
            testGenerateSendUserEvent(body, map);
            continue;
         }

         String eventType = map.entrySet().iterator().next().getKey();
         if (eventType.equals("ServiceRegistered")) {
            testGenerateServiceStart(body, map);
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

   private void testGenerateServiceStart(StringBuilder body, LinkedHashMap<String, String> map)
   {
      String serviceName = map.get("name");
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
      boolean first = true;
      String varName = null;
      String eventType = null;
      String statement = null;
      for (Map.Entry<String, String> entry : map.entrySet()) {
         if (first) {
            eventType = entry.getKey();
            varName = "e" + entry.getValue().replaceAll("\\:", "");
            statement = String.format("\n" +
                        "// send user event %s: %s\n",
                  eventType, entry.getValue());
            body.append(statement);
            statement = String.format("%s %s = new %s();\n",
                  eventType, varName, eventType);
            body.append(statement);
            statement = String.format("%s.setId(\"%s\");\n",
                  varName, varName);
            body.append(statement);
            first = false;
            continue;
         }

         String setterName = org.fulib.StrUtil.cap(entry.getKey());
         statement = String.format("%s.set%s(\"%s\");\n",
               varName, setterName, entry.getValue());
         body.append(statement);
      }

      // send it
      statement = String.format("publish(%s);\n", varName);
      body.append(statement);
   }

   private void buildEventMap(String yaml)
   {
      eventMap = new TreeMap<>();
      userMap = new LinkedHashMap<>();
      serviceMap = new LinkedHashMap<>();

      ArrayList<LinkedHashMap<String, String>> maps = new Yamler().decodeList(yaml);

      for (LinkedHashMap<String, String> map : maps) {
         Map.Entry<String, String> entry = map.entrySet().iterator().next();
         if (entry.getKey().equals("WorkflowStarted")) {
            workflowName = entry.getValue();
            continue;
         }
         if (entry.getKey().equals("UserRegistered")) {
            userMap.put(map.get("name"), map);
         }
         if (entry.getKey().equals("ServiceRegistered")) {
            serviceMap.put(map.get("name"), map);
         }
         eventMap.put(entry.getValue(), map);
      }

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

   private void oneEventClass(LinkedHashMap<String, String> map)
   {
      Clazz event = em.haveClass("Event");
      boolean first = true;
      Clazz clazz = null;
      for (String key : map.keySet()) {
         if (first) {
            if (key.equals("ServiceRegistered") || key.equals("UserRegistered")) {
               // generateModelClass(map);
               break;
            }
            clazz = em.haveClass(key);
            clazz.setSuperClass(event);
            first = false;
            continue;
         }

         mm.haveAttribute(clazz, key, "String");
      }
   }

   private void generateModelClass(LinkedHashMap<String, String> map)
   {
      String name = map.get("name");
      modelClazz = mm.haveClass(name + "Model");
      modelClazz.withImports("import java.util.LinkedHashMap;");
      mm.haveAttribute(modelClazz,
            "objectMap",
            "LinkedHashMap<String, Object>",
            "new LinkedHashMap<>()");

      String events = map.get("events");
      String[] split = events.split(" ");
      for (String event : split) {
         generateModelElementsFor(event);
      }
      Logger.getGlobal().info(String.format("%s events are %s",
            modelClazz.getName(),
            events));
   }

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
