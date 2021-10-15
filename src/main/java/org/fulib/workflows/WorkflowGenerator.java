package org.fulib.workflows;

import org.fulib.Fulib;
import org.fulib.FulibTools;
import org.fulib.builder.ClassModelManager;
import org.fulib.builder.Type;
import org.fulib.classmodel.AssocRole;
import org.fulib.classmodel.Attribute;
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
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WorkflowGenerator {
   private ClassModelManager mm;
   private ClassModelManager em;
   private ClassModelManager tm;
   private Clazz modelClazz;
   private LinkedHashMap<String, ClassModelManager> managerMap;
   private STGroupFile group;
   private Clazz testClazz;
   private EventModel eventModel;

   public Consumer<Object> dumpObjectDiagram;
   private EventStormingBoard eventStormingBoard;
   private StringBuilder testBody;
   private ArrayList<String> testVarNames;
   private Clazz serviceClazz;
   private Clazz logicClass;
   private Clazz builderClass;
   private ServiceNote lastServiceNote;
   private Clazz logic;
   private StringBuilder testClosing;

   public EventModel getEventModel() {
      return eventModel;
   }

   public WorkflowGenerator generateWorkflow(ClassModelManager mm, String fileName) {
      loadWorkflow(mm, fileName);
      generate();
      return this;
   }

   public WorkflowGenerator loadWorkflow(ClassModelManager mm, String fileName) {
      this.mm = mm;

      group = new STGroupFile(this.getClass().getResource("templates/Workflows.stg"));

      // event map
      eventModel = new EventModel();
      eventStormingBoard = eventModel.buildEventStormModel(fileName);
      if (dumpObjectDiagram != null) {
         dumpObjectDiagram.accept(eventStormingBoard);
      }
      buildClassModelManagerMap(mm);
      buildEventBroker();
      buildEventClasses();
      buildServices();
      buildTest();

      return this;
   }

   public WorkflowGenerator loadPlainModel(ClassModelManager mm, String fileName) {
      this.mm = mm;

      group = new STGroupFile(this.getClass().getResource("templates/Workflows.stg"));

      // event map
      eventModel = new EventModel();
      eventStormingBoard = eventModel.buildEventStormModel(fileName);
      managerMap = new LinkedHashMap<>();
      managerMap.put("mm", mm);
      buildPlainModel();
      buildPlainTest();

      return this;
   }

   private void buildPlainModel() {
      ClassModelManager modelManager = null;
      for (ServiceNote currentServiceNote : eventStormingBoard.getServices()) {
         lastServiceNote = currentServiceNote;

         String serviceName = currentServiceNote.getName();
         modelManager = mm;
         managerMap.put(serviceName, modelManager);

         // add model class
         Clazz modelClazz = modelManager.haveClass(serviceName + "Model");
         modelClazz.withImports("import java.util.LinkedHashMap;");
         modelManager.haveAttribute(modelClazz, "modelMap", "LinkedHashMap<String, Object>", "new LinkedHashMap<>()");

         buildPlainDataClasses(currentServiceNote, modelManager);
      }
   }

   private void buildPlainDataClasses(ServiceNote serviceNote, ClassModelManager modelManager) {
      // collect object ids
      for (Workflow workflow : serviceNote.getEventStormingBoard().getWorkflows()) {
         for (WorkflowNote note : workflow.getNotes()) {
            if (note instanceof DataNote) {
               serviceNote.getObjectMap().put(((DataNote) note).getBlockId(), ((DataNote) note).getDataType());
            }
         }
      }

      // create classes
      for (Workflow workflow : serviceNote.getEventStormingBoard().getWorkflows()) {
         for (WorkflowNote note : workflow.getNotes()) {
            if (note instanceof DataNote) {
               DataNote dataNote = (DataNote) note;
               if (dataNote.getType().getMigratedTo() != null) {
                  continue;
               }
               LinkedHashMap<String, String> map = note.getMap();
               LinkedHashMap<String, String> mockup = getMockup(map);
               addModelClassForDataNote(modelManager, serviceNote, mockup);
               addGetOrCreateMethodToServiceModel(modelManager, serviceNote.getName(), mockup);
               // addCreateAndInitModelObjectCode(modelManager, serviceNote, dataNote, mockup,
               // body, methodCall);
            } else if (note instanceof ClassNote) {
               ClassNote classNote = (ClassNote) note;
               addModelClassForClassNote(modelManager, serviceNote, classNote);
            }
         }
      }
   }

   private void buildPlainTest() {
      tm = new ClassModelManager()
            .setMainJavaDir(mm.getClassModel().getMainJavaDir().replace("/main/java", "/test/java"))
            .setPackageName(mm.getClassModel().getPackageName());
      managerMap.put("tm", tm);

      String boardName = StrUtil.toIdentifier(eventStormingBoard.getName());
      testClazz = tm.haveClass("Test" + boardName);
      testClazz.withImports("import org.junit.Test;", "import java.util.LinkedHashMap;", "import org.fulib.FulibTools;",
            "import static org.assertj.core.api.Assertions.assertThat;");

      String declaration = "@Test\n" + "public void test"
            + StrUtil.toIdentifier(eventModel.getEventStormingBoard().getName()) + "()";
      testBody = new StringBuilder();
      testVarNames = new ArrayList<>();

      // create business logic
      ClassModelManager modelManager = managerMap.get(lastServiceNote.getName());
      logic = modelManager.haveClass(lastServiceNote.getName() + "BusinessLogic");

      testBody.append(String.format("%s logic = new %1$s();\n", logic.getName()));

      addAllObjectCreation();
      addAllLinkCreation();

      String allVarNames = String.join(", ", testVarNames.toArray(new String[] {}));
      testBody.append(String.format("\nFulibTools.objectDiagrams().dumpSVG(\"tmp/%sStart.svg\", %s);\n\n",
            boardName, allVarNames));

      addAllCommands();

      testBody.append(String.format("\nFulibTools.objectDiagrams().dumpSVG(\"tmp/%sEnd.svg\", %s);\n\n",
            boardName, allVarNames));

      testBody.append("\nSystem.out.println();\n");

      tm.haveMethod(testClazz, declaration, testBody.toString());
   }

   private void addAllObjectCreation() {
      for (Workflow workflow : eventStormingBoard.getWorkflows()) {
         for (WorkflowNote note : workflow.getNotes()) {
            if (note instanceof ExternalSystemNote) {
               ExternalSystemNote externalSystemNote = (ExternalSystemNote) note;
               for (Policy policy : externalSystemNote.getPolicies()) {
                  for (WorkflowNote step : policy.getSteps()) {
                     if (step instanceof DataNote) {
                        addObjectCreationToPlainTest(step);
                     }
                  }
               }
            }
         }
      }
   }

   private void addAllLinkCreation() {
      testBody.append("\n// create links\n");
      for (Workflow workflow : eventStormingBoard.getWorkflows()) {
         for (WorkflowNote note : workflow.getNotes()) {
            if (note instanceof ExternalSystemNote) {
               ExternalSystemNote externalSystemNote = (ExternalSystemNote) note;
               for (Policy policy : externalSystemNote.getPolicies()) {
                  for (WorkflowNote step : policy.getSteps()) {
                     if (step instanceof DataNote) {
                        addLinkCreationToPlainTest(step);
                     }
                  }
               }
            }
         }
      }
   }

   private void addAllCommands() {
      for (Workflow workflow : eventStormingBoard.getWorkflows()) {
         for (WorkflowNote note : workflow.getNotes()) {
            if (note instanceof CommandNote) {
               addCommandToPlainTest(note);
            }
         }
      }
   }

   private void addCommandToPlainTest(WorkflowNote note) {
      CommandNote commandNote = (CommandNote) note;
      String command = commandNote.getMap().get("command");
      command = eventModel.getVarName(command);
      String params = "";
      Iterator<Map.Entry<String, String>> iterator = commandNote.getMap().entrySet().iterator();
      iterator.next();
      while (iterator.hasNext()) {
         Map.Entry<String, String> entry = iterator.next();
         params += entry.getValue();
      }
      testBody.append(String.format("logic.%s(%s);\n", command, params));

      // is there a policy?
      for (Policy policy : commandNote.getPolicies()) {
         for (WorkflowNote step : policy.getSteps()) {
            if (step instanceof DataNote) {
               LinkedHashMap<String, String> mockup = getMockup(step.getMap());
               iterator = mockup.entrySet().iterator();
               Map.Entry<String, String> entry = iterator.next();
               String blockId = entry.getValue();
               if (!testVarNames.contains(blockId)) {
                  continue;
               }
               String type = entry.getKey();
               while (iterator.hasNext()) {
                  entry = iterator.next();
                  testBody.append(String.format("assertThat(%s.get%s()).isEqualTo(\"%s\");\n",
                        eventModel.getVarName(blockId), StrUtil.cap(entry.getKey()), entry.getValue()));
               }
            }
         }
      }
   }

   private void addObjectCreationToPlainTest(WorkflowNote note) {
      DataNote dataNote = (DataNote) note;
      ClassModelManager modelManager = managerMap.get(lastServiceNote.getName());
      Clazz dataClass = modelManager.haveClass(dataNote.getDataType());
      LinkedHashMap<String, String> mockup = getMockup(dataNote.getMap());
      String blockId = dataNote.getBlockId();
      String varName = StrUtil.decap(blockId);
      testBody.append(String.format("\n%s %s = new %1$s().setId(\"%s\");\n", dataNote.getDataType(), varName, blockId));
      testVarNames.add(varName);
      Iterator<Map.Entry<String, String>> iterator = mockup.entrySet().iterator();
      iterator.next();
      while (iterator.hasNext()) {
         Map.Entry<String, String> entry = iterator.next();
         String attr = entry.getKey();
         if (attr.endsWith(".back")) {
            continue;
         }
         String value = entry.getValue();
         Attribute attribute = dataClass.getAttribute(attr);
         AssocRole role = dataClass.getRole(attr);
         if (attribute != null) {
            if (attribute.getType().equals(Type.STRING)) {
               value = "\"" + value + "\"";
            }
            testBody.append(String.format("%s.set%s(%s);\n", varName, StrUtil.cap(attr), value));
         }
      }
   }

   private void addLinkCreationToPlainTest(WorkflowNote note) {
      DataNote dataNote = (DataNote) note;
      ClassModelManager modelManager = managerMap.get(lastServiceNote.getName());
      Clazz dataClass = modelManager.haveClass(dataNote.getDataType());
      LinkedHashMap<String, String> mockup = getMockup(dataNote.getMap());
      String blockId = dataNote.getBlockId();
      String varName = eventModel.getVarName(blockId);
      Iterator<Map.Entry<String, String>> iterator = mockup.entrySet().iterator();
      iterator.next();
      while (iterator.hasNext()) {
         Map.Entry<String, String> entry = iterator.next();
         String attr = entry.getKey();
         if (attr.endsWith(".back")) {
            continue;
         }
         String value = entry.getValue();
         Attribute attribute = dataClass.getAttribute(attr);
         AssocRole role = dataClass.getRole(attr);
         if (attribute == null && role.getCardinality() <= 1) {
            testBody.append(String.format("%s.set%s(%s);\n", varName, StrUtil.cap(attr), eventModel.getVarName(value)));
         } else if (attribute == null && role.getCardinality() > 1) {
            value = StrUtil.stripBrackets(value);
            testBody.append(String.format("%s.with%s(%s);\n", varName, StrUtil.cap(attr), value));
         }
      }
   }

   private void buildServices() {
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

         // add model class
         Clazz modelClazz = modelManager.haveClass(serviceName + "Model");
         modelClazz.withImports("import java.util.LinkedHashMap;");
         modelManager.haveAttribute(modelClazz, "modelMap", "LinkedHashMap<String, Object>", "new LinkedHashMap<>()");

         // add business logic class
         addBusinessLogicClass(modelManager, serviceName);

         addBuilderClass(modelManager, serviceName);

         // add ServiceClass
         addServiceClass(modelManager, port, serviceName);

         String declaration;
         StringBuilder body = new StringBuilder();
         ST st;

         addStartMethod(modelManager, serviceName, serviceClazz, body);
         addGetHelloMethod(modelManager, serviceName, serviceClazz, body);
         addSubscribeAndLoadOldEventsMethod(modelManager, port, serviceClazz, body);
         addApplyMethod(modelManager, body);

         buildGetPageMethod(modelManager, serviceClazz, serviceName, body);

         buildModelClasses(modelManager, serviceNote);
         buildInitEventHandlerMapMethod(modelManager, serviceNote, body);
         buildLoadAndInitLoaderMap(modelManager, serviceNote);

         // ignoreEvents method
         declaration = "private void ignoreEvent(Event event)";
         body.setLength(0);
         body.append("// empty\n");
         modelManager.haveMethod(logicClass, declaration, body.toString());

         // getHandlerMethod
         declaration = "public Consumer<Event> getHandler(Event event)";
         body.setLength(0);
         body.append("return getHandlerMap().computeIfAbsent(event.getClass(), k -> this::ignoreEvent);\n");
         modelManager.haveMethod(logicClass, declaration, body.toString());

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

         // stripbrackets
         declaration = "public String stripBrackets(String back)";
         body.setLength(0);
         st = group.getInstanceOf("stripBrackets");
         body.append(st.render());
         modelManager.haveMethod(builderClass, declaration, body.toString());

      }
   }

   private void buildModelClasses(ClassModelManager modelManager, ServiceNote serviceNote) {
      for (Policy policy : serviceNote.getPolicies()) {
         for (WorkflowNote note : policy.getSteps()) {
            if (note instanceof ClassNote) {
               ClassNote classNote = (ClassNote) note;
               addModelClassForClassNote(modelManager, serviceNote, classNote);
            } else if (note instanceof DataNote) {
               LinkedHashMap<String, String> mockup = getMockup(note.getMap());
               addModelClassForDataNote(modelManager, serviceNote, mockup);
            }
         }
      }
   }

   private void addBuilderClass(ClassModelManager modelManager, String serviceName) {
      builderClass = modelManager.haveClass(serviceName + "Builder");
      modelManager.haveAttribute(builderClass, "model", serviceName + "Model");
      builderClass.withImports("import java.util.LinkedHashMap;", "import java.util.function.Consumer;",
            "import " + em.getClassModel().getPackageName() + ".*;");

      modelManager.associate(logicClass, "builder", Type.ONE, builderClass, "businessLogic", Type.ONE);

      modelManager.haveAttribute(builderClass, "eventStore", "LinkedHashMap<String, DataEvent>",
            "new LinkedHashMap<>()");

      ST st;
      String declaration = "private boolean outdated(DataEvent event)";
      st = group.getInstanceOf("builderOutdated");
      String builderBody = st.render();
      modelManager.haveMethod(builderClass, declaration, builderBody);
   }

   private void addBusinessLogicClass(ClassModelManager modelManager, String serviceName) {
      logicClass = modelManager.haveClass(serviceName + "BusinessLogic");
      modelManager.haveAttribute(logicClass, "model", serviceName + "Model");
      logicClass.withImports("import java.util.LinkedHashMap;", "import java.util.function.Consumer;",
            "import " + em.getClassModel().getPackageName() + ".*;");
   }

   private void addServiceClass(ClassModelManager modelManager, String port, String serviceName) {
      serviceClazz = modelManager.haveClass(serviceName + "Service");
      serviceClazz.withImports("import java.util.LinkedHashMap;", "import java.time.Instant;",
            "import java.time.format.DateTimeFormatter;", "import java.util.Map;",
            "import java.util.function.Consumer;", "import " + em.getClassModel().getPackageName() + ".*;",
            "import org.fulib.yaml.Yaml;", "import org.fulib.yaml.YamlIdMap;", "import spark.Service;",
            "import spark.Request;", "import spark.Response;", "import com.mashape.unirest.http.HttpResponse;",
            "import com.mashape.unirest.http.Unirest;", "import com.mashape.unirest.http.exceptions.UnirestException;",
            "import java.util.concurrent.ExecutorService;", "import java.util.concurrent.Executors;",
            "import java.util.logging.Logger;", "import java.util.logging.Level;");
      modelManager.haveAttribute(serviceClazz, "history", "LinkedHashMap<String, Event>", "new LinkedHashMap<>()");
      modelManager.haveAttribute(serviceClazz, "port", Type.INT, port);
      modelManager.haveAttribute(serviceClazz, "spark", "Service");
      modelManager.haveAttribute(serviceClazz, "model", serviceName + "Model");
      modelManager.associate(serviceClazz, "businessLogic", Type.ONE, logicClass, "service", Type.ONE);
      modelManager.associate(serviceClazz, "builder", Type.ONE, builderClass, "service", Type.ONE);
      modelManager.haveAttribute(logicClass, "handlerMap", "LinkedHashMap<Class, Consumer<Event>>", null);

      String declaration = "public Query query(Query query)";
      ST st = group.getInstanceOf("serviceQuery");
      String queryBody = st.render();
      modelManager.haveMethod(serviceClazz, declaration, queryBody);

      declaration = "public String isoNow()";
      String isNowBody = "return DateTimeFormatter.ISO_INSTANT.format(Instant.now());\n";
      modelManager.haveMethod(serviceClazz, declaration, isNowBody);

   }

   private void buildGetPageMethod(ClassModelManager modelManager, Clazz serviceClazz, String serviceName,
         StringBuilder body) {
      String declaration;
      ST st;// apply method

      declaration = "public String getPage(Request request, Response response)";
      body.setLength(0);
      body.append("// to protect manuel changes to this method insert a 'no' in front of fulib in the next line\n");
      body.append("// fulib\n");
      body.append("return getDemoPage(request, response);\n");
      modelManager.haveMethod(serviceClazz, declaration, body.toString());

      // getDemoPage method
      body.setLength(0);
      declaration = "public String getDemoPage(Request request, Response response)";
      StringBuilder eventHandling = new StringBuilder();
      StringBuilder content = new StringBuilder();
      ServiceNote serviceNote = eventStormingBoard.getFromServices(serviceName);
      for (PageNote pageNote : serviceNote.getPages()) {
         content.append(String.format("// %s\n", pageNote.getTime()));
         String pageId = StrUtil.pageId(pageNote.getTime());
         content.append(String.format("if (id.equals(\"%s\")) {\n", pageId));
         buildPage(pageNote, eventHandling, content);
         content.append("   return html.toString();\n");
         content.append("}\n\n");
      }

      st = group.getInstanceOf("serviceGetPage");
      st.add("eventHandling", eventHandling.toString());
      st.add("content", content.toString());
      body.append(st.render());
      modelManager.haveMethod(serviceClazz, declaration, body.toString());
   }

   private void buildPage(PageNote pageNote, StringBuilder eventHandling, StringBuilder content) {
      String nextTime = "next_page";
      if (pageNote.getNextPage() != null) {
         nextTime = StrUtil.pageId(pageNote.getNextPage().getTime());
      }
      content.append(
            String.format("   html.append(\"<form action=\\\"/page/%s\\\" method=\\\"get\\\">\\n\");\n", nextTime));

      for (PageLine line : pageNote.getLines()) {
         String firstTag = line.getMap().keySet().iterator().next();
         if (firstTag.equals("label")) {
            content.append(String.format("   html.append(\"   <p>%s</p>\\n\");\n", line.getMap().get("label")));
            continue;
         }
         if (firstTag.equals("button")) {
            // is there an event?
            String key = line.getMap().get("button");
            String command = line.getMap().get("command");
            if (command != null) {
               buildEventHandling(pageNote, command, eventHandling);

               // hidden input
               content.append(String.format(
                     "   html.append(\"   <p><input id=\\\"event\\\" name=\\\"event\\\" type=\\\"hidden\\\" value=\\\"%s\\\"></p>\\n\");\n",
                     command));
            }
            content.append(String.format(
                  "   html.append(\"   <p><input id=\\\"%s\\\" name=\\\"button\\\" type=\\\"submit\\\" value=\\\"%1$s\\\"></p>\\n\");\n",
                  key));
            continue;
         }

         if (firstTag.equals("input")) {
            String key = line.getMap().get("input");
            content.append(String.format(
                  "   html.append(\"   <p><input id=\\\"%s\\\" name=\\\"%1$s\\\" placeholder=\\\"%1$s?\\\"></p>\\n\");\n",
                  key));
            continue;
         }

         if (firstTag.equals("password")) {
            String key = line.getMap().get("password");
            content.append(String.format(
                  "   html.append(\"   <p><input id=\\\"%s\\\" name=\\\"%1$s\\\" type=\\\"password\\\" placeholder=\\\"%1$s?\\\"></p>\\n\");\n",
                  key));
            continue;
         }

         content.append(String.format("   // %s\n", line.getMap().entrySet().iterator().next().getValue()));
      }
      content.append("   html.append(\"</form>\\n\");\n");
   }

   private void buildEventHandling(PageNote pageNote, String command, StringBuilder eventHandling) {
      EventNote eventNote = pageNote.getRaisedEvent();
      eventHandling.append(String.format("if (\"%s\".equals(event)) {\n", command));
      String varName = addCreateAndInitEventCode("   ", eventNote, eventHandling);
      eventHandling.append(String.format("   apply(%s);\n", varName));
      eventHandling.append("}\n\n");
   }

   private void addApplyMethod(ClassModelManager modelManager, StringBuilder body) {
      String declaration;
      ST st;// apply method
      declaration = "public void apply(Event event)";
      body.setLength(0);
      st = group.getInstanceOf("serviceApply");
      body.append(st.render());
      modelManager.haveMethod(serviceClazz, declaration, body.toString());
   }

   private void addSubscribeAndLoadOldEventsMethod(ClassModelManager modelManager, String port, Clazz serviceClazz,
         StringBuilder body) {
      String declaration;
      ST st;
      // add subscribeAndLoadOldEvents
      declaration = "private void subscribeAndLoadOldEvents()";
      body.setLength(0);
      st = group.getInstanceOf("serviceSubscribeAndLoadOldEvents");
      st.add("port", port);
      body.append(st.render());
      modelManager.haveMethod(serviceClazz, declaration, body.toString());
   }

   private void addGetHelloMethod(ClassModelManager modelManager, String serviceName, Clazz serviceClazz,
         StringBuilder body) {
      String declaration;
      // add getHello
      declaration = "private String getHello(Request req, Response res)";
      body.setLength(0);
      ST st = group.getInstanceOf("serviceGetHelloBody");
      st.add("name", serviceName);
      body.append(st.render());
      modelManager.haveMethod(serviceClazz, declaration, body.toString());
   }

   private void addStartMethod(ClassModelManager modelManager, String serviceName, Clazz serviceClazz,
         StringBuilder body) {
      String declaration;
      // add start method
      declaration = "public void start()";
      body.append(String.format("model = new %sModel();\n", serviceName));
      body.append(String.format("setBuilder(new %sBuilder().setModel(model));\n", serviceName));
      body.append(String.format("setBusinessLogic(new %sBusinessLogic());\n", serviceName));
      body.append("businessLogic.setBuilder(getBuilder());\n");
      body.append("businessLogic.setModel(model);\n");
      body.append("ExecutorService executor = Executors.newSingleThreadExecutor();\n");
      body.append("spark = Service.ignite();\n");
      body.append("spark.port(port);\n");
      body.append("spark.get(\"/page/:id\", (req, res) -> executor.submit(() -> this.getPage(req, res)).get());\n");
      body.append("spark.get(\"/\", (req, res) -> executor.submit(() -> this.getHello(req, res)).get());\n");
      body.append("spark.post(\"/apply\", (req, res) -> executor.submit(() -> this.postApply(req, res)).get());\n");
      body.append("executor.submit(this::subscribeAndLoadOldEvents);\n");
      body.append(
            String.format("Logger.getGlobal().info(\"%s service is up and running on port \" + port);\n", serviceName));
      modelManager.haveMethod(serviceClazz, declaration, body.toString());

      declaration = "public void stop()";
      String stopBody = "spark.stop();\n";
      modelManager.haveMethod(serviceClazz, declaration, stopBody);
   }

   private void buildLoadAndInitLoaderMap(ClassModelManager modelManager, ServiceNote serviceNote) {
      String declaration = "public Object load(String blockId)";
      StringBuilder body = new StringBuilder();
      ST st = group.getInstanceOf("builderLoad");
      body.append(st.render());

      modelManager.haveMethod(builderClass, declaration, body.toString());

      modelManager.haveAttribute(builderClass, "loaderMap", "LinkedHashMap<Class, Function<Event, Object>>");
      modelManager.haveAttribute(builderClass, "groupStore", "LinkedHashMap<String, LinkedHashMap<String, DataEvent>>",
            "new LinkedHashMap<>()");
      modelManager.haveImport(builderClass, "import java.util.function.Function;");

      declaration = "private void initLoaderMap()";
      body.setLength(0);
      body.append("if (loaderMap == null) {\n");
      body.append("   loaderMap = new LinkedHashMap<>();\n");
      for (DataType dataType : serviceNote.getHandledDataTypes()) {
         if (dataType.getMigratedTo() != null) {
            continue;
         }
         String eventTypeName = dataType.getDataTypeName() + "Built";
         body.append(String.format("   loaderMap.put(%s.class, this::load%s);\n", eventTypeName, eventTypeName));
      }
      body.append("}\n");

      modelManager.haveMethod(builderClass, declaration, body.toString());

      declaration = "public String getObjectId(String value)";
      body.setLength(0);
      st = group.getInstanceOf("builderGetObjectId");
      body.append(st.render());
      modelManager.haveMethod(builderClass, declaration, body.toString());

      declaration = "private void addToGroup(String groupId, String elementId)";
      body.setLength(0);
      st = group.getInstanceOf("builderAddToGroup");
      body.append(st.render());
      modelManager.haveMethod(builderClass, declaration, body.toString());
   }

   private void buildInitEventHandlerMapMethod(ClassModelManager modelManager, ServiceNote serviceNote,
         StringBuilder body) {
      String declaration;
      // initHandlerMap
      declaration = "public void initEventHandlerMap()";
      body.setLength(0);
      body.append("if (handlerMap == null) {\n");
      body.append("   handlerMap = new LinkedHashMap<>();\n");
      addHandlersToInitEventHandlerMap(modelManager, serviceNote, body);
      body.append("}\n");
      modelManager.haveMethod(logicClass, declaration, body.toString());
   }

   private void addHandlersToInitEventHandlerMap(ClassModelManager modelManager, ServiceNote serviceNote,
         StringBuilder body) {
      for (DataType dataType : serviceNote.getHandledDataTypes()) {
         String eventTypeName = dataType.getDataTypeName() + "Built";
         body.append(String.format("   handlerMap.put(%s.class, builder::store%s);\n", eventTypeName, eventTypeName));
         addDataEventHandlerMethod(modelManager, serviceNote, dataType);
      }

      for (EventType eventType : serviceNote.getHandledEventTypes()) {
         String eventTypeName = eventType.getEventTypeName();
         body.append(String.format("   handlerMap.put(%s.class, this::handle%s);\n", eventTypeName, eventTypeName));
         addEventHandlerMethod(modelManager, serviceNote, eventType);
      }
   }

   private void addDataEventHandlerMethod(ClassModelManager modelManager, ServiceNote serviceNote, DataType dataType) {
      StringBuilder body = new StringBuilder();
      String dataTypeName = dataType.getDataTypeName();
      String eventTypeName = dataTypeName + "Built";
      String declaration = String.format("public void store%s(Event e)", eventTypeName);
      body.append(String.format("%s event = (%1$s) e;\n", eventTypeName));

      if (dataType.getMigratedTo() != null) {
         body.append(
               "// please insert a no before fulib in the next line and insert event upgrading code\n" + "// fulib\n");
      } else {
         body.append("if (outdated(event)) {\n");
         body.append("   return;\n");
         body.append("}\n");
         body.append("// please insert a no before fulib in the next line and insert addToGroup commands as necessary\n"
               + "// fulib\n");
      }

      modelManager.haveMethod(builderClass, declaration, body.toString());

      if (dataType.getMigratedTo() != null) {
         return;
      }

      declaration = String.format("public %s load%s(Event e)", dataTypeName, eventTypeName);
      body.setLength(0);
      body.append(String.format("%s event = (%1$s) e;\n", eventTypeName));
      body.append(String.format("%s object = model.getOrCreate%1$s(event.getBlockId());\n", dataTypeName));

      // ensure datClazz
      for (DataNote note : dataType.getDataNotes()) {
         LinkedHashMap<String, String> map = note.getMap();
         LinkedHashMap<String, String> mockup = getMockup(map);
         addModelClassForDataNote(modelManager, serviceNote, mockup);
         addGetOrCreateMethodToServiceModel(modelManager, serviceNote.getName(), mockup);
      }

      Clazz dataClazz = modelManager.haveClass(dataTypeName);
      Clazz eventClazz = em.haveClass(eventTypeName);

      for (Attribute attribute : eventClazz.getAttributes()) {
         String attrName = attribute.getName();
         Attribute dataAttr = getAttribute(dataClazz, attrName);
         if (dataAttr != null) {
            // e.g.: object.setMotivation(event.getMotivation());
            if (dataAttr.getType().equals(Type.STRING)) {
               body.append(String.format("object.set%s(event.get%1$s());\n", StrUtil.cap(attrName)));
            } else if (dataAttr.getType().equals(Type.INT)) {
               body.append(String.format("object.set%s(Integer.parseInt(event.get%1$s()));\n", StrUtil.cap(attrName)));
            } else if (dataAttr.getType().equals(Type.DOUBLE)) {
               body.append(
                     String.format("object.set%s(Double.parseDouble(event.get%1$s()));\n", StrUtil.cap(attrName)));
            }
         } else {
            // e.g.: event.setPreviousStop(model.getOrCreateStop(event.getPreviousStop()));
            AssocRole role = getRole(dataClazz, attrName);
            AssocRole other = role.getOther();
            Clazz otherClazz = other.getClazz();
            if (role.getCardinality() <= 1) {
               body.append(String.format("object.set%s(model.getOrCreate%s(event.get%1$s()));\n", StrUtil.cap(attrName),
                     otherClazz.getName()));
               // body.append(String.format("object.set%s(model.getOrCreate%s(getObjectId(event.get%1$s())));\n",
               // StrUtil.cap(attrName), otherClazz.getName()));
            } else {
               body.append(String.format("for (String name : stripBrackets(event.get%s()).split(\",\\\\s+\")) {\n",
                     StrUtil.cap(attrName)));
               body.append("   if (name.equals(\"\")) continue;\n");
               body.append(String.format("   object.with%s(model.getOrCreate%s(name));\n", StrUtil.cap(attrName),
                     otherClazz.getName()));
               // body.append(String.format("
               // object.with%s(model.getOrCreate%s(getObjectId(name)));\n",
               // StrUtil.cap(attrName), otherClazz.getName()));
               body.append("}\n");
            }
         }
      }
      body.append("return object;\n");

      modelManager.haveMethod(builderClass, declaration, body.toString());

   }

   private void addEventHandlerMethod(ClassModelManager modelManager, ServiceNote serviceNote, EventType eventType) {
      StringBuilder body = new StringBuilder();
      String eventTypeName = eventType.getEventTypeName();
      String declaration = String.format("private void handle%s(Event e)", eventTypeName);
      body.append("// to protect manuel changes to this method insert a 'no' in front of fulib in the next line\n");
      body.append("// fulib\n");
      body.append(String.format("%s event = (%s) e;\n", eventTypeName, eventTypeName));
      body.append(String.format("handleDemo%s(event);\n", eventTypeName));
      modelManager.haveMethod(logicClass, declaration, body.toString());

      body.setLength(0);
      declaration = String.format("private void handleDemo%s(%1$s event)", eventTypeName);

      ObjectTable<Object> table = new ObjectTable<>("service", serviceNote);
      LinkedHashSet<Object> policies = table.expandLink("eventType", ServiceNote.PROPERTY_HANDLED_EVENT_TYPES)
            .filter(et -> et == eventType).expandLink("event", EventType.PROPERTY_EVENTS)
            .expandLink("policy", EventNote.PROPERTY_POLICIES).filter(p -> ((Policy) p).getService() == serviceNote)
            .toSet();

      for (Object obj : policies) {
         Policy policy = (Policy) obj;
         EventNote triggerEvent = policy.getTrigger();
         String eventId = triggerEvent.getTime();
         body.append(String.format("if (event.getId().equals(\"%s\")) {\n", eventId));
         addMockupData(modelManager, serviceNote, policy, body, "service.apply");
         body.append("}\n");
      }
      modelManager.haveMethod(logicClass, declaration, body.toString());
   }

   private void addMockupData(ClassModelManager modelManager, ServiceNote serviceNote, Policy policy,
         StringBuilder body, String methodCall) {
      for (WorkflowNote note : policy.getSteps()) {
         if (note instanceof DataNote) {
            // Example
            // - data: box box23
            // product: shoes
            // place: shelf23
            DataNote dataNote = (DataNote) note;
            if (dataNote.getType().getMigratedTo() != null) {
               continue;
            }
            LinkedHashMap<String, String> map = note.getMap();
            LinkedHashMap<String, String> mockup = getMockup(map);
            addModelClassForDataNote(modelManager, serviceNote, mockup);
            addGetOrCreateMethodToServiceModel(modelManager, serviceNote.getName(), mockup);
            addCreateAndInitModelObjectCode(modelManager, serviceNote, dataNote, mockup, body, methodCall);
         } else if (note instanceof ClassNote) {
            ClassNote classNote = (ClassNote) note;
            addModelClassForClassNote(modelManager, serviceNote, classNote);
         } else if (note instanceof QueryNote) {
            QueryNote classNote = (QueryNote) note;
            // add to query demo method
            System.out.println();
         } else if (note instanceof EventNote) {
            // fire event
            EventNote eventNote = (EventNote) note;
            String varName = eventNote.getTime().replaceAll("\\W+", "");
            body.append(String.format("\n   %s e%s = new %1$s();\n", eventNote.getEventTypeName(), varName));
            body.append(String.format("\n   e%s.setId(\"%s\");\n", varName, eventNote.getTime()));

            LinkedHashMap<String, String> map = eventNote.getMap();
            LinkedHashMap<String, String> clone = (LinkedHashMap<String, String>) map.clone();
            clone.remove(eventNote.getEventTypeName());
            if (eventNote instanceof CommandNote) {
               clone.remove("command");
            } else {
               clone.remove("event");
            }
            for (Map.Entry<String, String> entry : clone.entrySet()) {
               String setterName = org.fulib.StrUtil.cap(entry.getKey());
               String statement = String.format("   e%s.set%s(\"%s\");\n", varName, setterName, entry.getValue());
               body.append(statement);
            }
            body.append(String.format("   %s(e%s);\n", methodCall, varName));
         } else {
            Logger.getGlobal().severe("do not know how to deal with " + note);
         }
      }
   }

   private LinkedHashMap<String, String> getMockup(LinkedHashMap<String, String> map) {
      Map.Entry<String, String> firstEntry = map.entrySet().iterator().next();
      LinkedHashMap<String, String> mockup;
      String value = firstEntry.getValue();
      String[] split = StrUtil.split(value);
      if (split.length == 1) {
         // old style
         mockup = (LinkedHashMap<String, String>) ((LinkedHashMap<String, String>) map).clone();
         mockup.remove(firstEntry.getKey());
      } else {
         mockup = new LinkedHashMap<>();
         mockup.put(split[0], split[1]);
         mockup.putAll(map);
         mockup.remove("data");
      }
      return mockup;
   }

   private void addGetOrCreateMethodToServiceModel(ClassModelManager modelManager, String serviceName,
         LinkedHashMap<String, String> mockup) {
      String type = org.fulib.StrUtil.cap(eventModel.getEventType(mockup));
      Clazz modelClazz = modelManager.haveClass(serviceName + "Model");
      String declaration = String.format("public %s getOrCreate%s(String id)", type, type);
      String body = String.format("if (id == null) return null;\n", type, type);
      body += String.format("return (%s) modelMap.computeIfAbsent(id, k -> new %s().setId(k));\n", type, type);
      modelManager.haveMethod(modelClazz, declaration, body);
   }

   private void addModelClassForDataNote(ClassModelManager modelManager, ServiceNote serviceNote,
         LinkedHashMap<String, String> map) {
      if (map.get("@migratedTo") != null) {
         // no model class for migrated events
         return;
      }
      Iterator<Map.Entry<String, String>> iter = map.entrySet().iterator();
      Map.Entry<String, String> entry = iter.next();
      String type = entry.getKey();
      String objectId = entry.getValue();
      type = org.fulib.StrUtil.cap(type);
      Clazz clazz = modelManager.haveClass(type);
      if (clazz.getSuperClass() == null) {
         modelManager.haveAttribute(clazz, "id", Type.STRING);
      }

      while (iter.hasNext()) {
         entry = iter.next();

         String attrName = entry.getKey();
         String value = entry.getValue();
         if (attrName.endsWith(".back")) {
            continue;
         }
         String back = map.get(attrName + ".back");
         if (back != null) {
            // its an assoc
            int srcSize = value.startsWith("[") ? 42 : 1;
            value = StrUtil.stripBrackets(value).split(",\\s+")[0];
            value = eventModel.getObjectId(value);
            // find other class
            String otherClassName = serviceNote.getObjectMap().get(value);
            Clazz otherClazz = modelManager.haveClass(otherClassName);
            int backSize = back.startsWith("[") ? 42 : 1;
            back = StrUtil.stripBrackets(back);
            // declare assoc
            modelManager.associate(clazz, attrName, srcSize, otherClazz, back, backSize);
            // remove string attributes

            clazz.withoutAttributes(clazz.getAttribute(attrName));
            otherClazz.withoutAttributes(otherClazz.getAttribute(back));
            continue;
         }
         if (getRole(clazz, attrName) != null) {
            // its an assoc and already known
            continue;
         }
         if (getAttribute(clazz, attrName) != null) {
            continue;
         }
         Attribute attribute = getAttribute(clazz, attrName);
         if (attribute == null) {
            modelManager.haveAttribute(clazz, attrName, Type.STRING);
         }
      }
   }

   private AssocRole getRole(Clazz clazz, String attrName) {
      AssocRole role = clazz.getRole(attrName);
      if (role == null) {
         Clazz superClass = clazz.getSuperClass();
         if (superClass != null) {
            role = getRole(superClass, attrName);
         }
      }
      return role;
   }

   private Attribute getAttribute(Clazz clazz, String attrName) {
      Attribute attribute = clazz.getAttribute(attrName);
      if (attribute == null) {
         Clazz superClass = clazz.getSuperClass();
         if (superClass != null) {
            attribute = getAttribute(superClass, attrName);
         }
      }
      return attribute;
   }

   private void addModelClassForClassNote(ClassModelManager modelManager, ServiceNote serviceNote,
         ClassNote classNote) {
      LinkedHashMap<String, String> map = classNote.getMap();
      Iterator<Map.Entry<String, String>> iter = map.entrySet().iterator();
      Map.Entry<String, String> entry = iter.next();
      String type = entry.getValue();
      type = org.fulib.StrUtil.cap(type);
      Clazz clazz = modelManager.haveClass(type);
      modelManager.haveAttribute(clazz, "id", Type.STRING);

      while (iter.hasNext()) {
         entry = iter.next();

         String attrName = entry.getKey();
         String value = entry.getValue();
         if (attrName.endsWith(".back")) {
            continue;
         }
         if (attrName.equals("extends")) {
            Clazz superClass = modelManager.haveClass(value);
            clazz.setSuperClass(superClass);
            for (Attribute superAttr : superClass.getAttributes()) {
               Attribute localAttr = clazz.getAttribute(superAttr.getName());
               if (localAttr != null) {
                  clazz.withoutAttributes(localAttr);
               }
            }
            for (AssocRole superRole : superClass.getRoles()) {
               Attribute localAttr = clazz.getAttribute(superRole.getName());
               if (localAttr != null) {
                  clazz.withoutAttributes(localAttr);
               }
            }
            continue;
         }
         String back = map.get(attrName + ".back");
         if (back != null) {
            // its an assoc
            int srcSize = value.startsWith("[") ? 42 : 1;
            value = StrUtil.stripBrackets(value).split("\\s+")[0];
            // find other class
            String otherClassName = StrUtil.cap(value);
            Clazz otherClazz = modelManager.haveClass(otherClassName);
            int backSize = back.startsWith("[") ? 42 : 1;
            back = StrUtil.stripBrackets(back);
            // declare assoc
            modelManager.associate(clazz, attrName, srcSize, otherClazz, back, backSize);
            // remove string attributes

            clazz.withoutAttributes(clazz.getAttribute(attrName));
            otherClazz.withoutAttributes(otherClazz.getAttribute(back));
            continue;
         }
         if (getRole(clazz, attrName) != null) {
            // its an assoc and already known
            continue;
         }
         modelManager.haveAttribute(clazz, attrName, value);
      }
   }

   private String addCreateAndInitModelObjectCode(ClassModelManager modelManager, ServiceNote serviceNote,
         DataNote dataNote, LinkedHashMap<String, String> map, StringBuilder body, String methodCall) {
      boolean first = true;
      String varName = null;
      String id = null;
      String className = null;
      Clazz clazz = null;
      String statement = null;
      for (Map.Entry<String, String> entry : map.entrySet()) {
         String value = entry.getValue();
         if (first) {
            className = org.fulib.StrUtil.cap(entry.getKey());
            clazz = modelManager.haveClass(className);
            className = className + "Built";
            id = value;
            id = id.replaceAll("\\W+", "");
            varName = org.fulib.StrUtil.downFirstChar(id) + "Event";
            statement = String.format("   %s %s = new %1$s();\n", className, varName);
            body.append(statement);
            statement = String.format("   %s.setId(\"%s\");\n", varName, dataNote.getTime());
            body.append(statement);
            statement = String.format("   %s.setBlockId(\"%s\");\n", varName, dataNote.getBlockId());
            body.append(statement);

            first = false;
            continue;
         }

         String attrName = entry.getKey();
         if (attrName.endsWith(".back")) {
            continue;
         }

         if (attrName.startsWith("@")) {
            continue;
         }

         String setterName = org.fulib.StrUtil.cap(attrName);
         statement = String.format("   %s.set%s(\"%s\");\n", varName, setterName, value);
         body.append(statement);
      }

      statement = String.format("   %s(%s);\n\n", methodCall, varName);
      body.append(statement);

      return varName;
   }

   private void buildEventBroker() {
      try {
         InputStream resource = this.getClass().getResourceAsStream("templates/EventBroker.template");
         BufferedInputStream buf = new BufferedInputStream(resource);
         byte[] bytes = buf.readAllBytes();
         String content = new String(bytes, StandardCharsets.UTF_8);
         content = content.replace("package uks.dpst21.events;",
               "package " + em.getClassModel().getPackageName() + ";");
         String eventBrokerName = getPackageDirName(em) + "/EventBroker.java";
         // Logger.getGlobal().info("EventBroker file generated " + eventBrokerName);
         Files.createDirectories(Path.of(getPackageDirName(em)));
         Files.write(Path.of(eventBrokerName), content.getBytes(StandardCharsets.UTF_8));
      } catch (IOException e) {
         Logger.getGlobal().log(Level.SEVERE, "could not read resource templates/Eventbroker.template", e);
      }

   }

   private void buildTest() {
      tm = new ClassModelManager()
            .setMainJavaDir(mm.getClassModel().getMainJavaDir().replace("/main/java", "/test/java"))
            .setPackageName(mm.getClassModel().getPackageName());
      managerMap.put("tm", tm);

      String boardName = StrUtil.toIdentifier(eventStormingBoard.getName());
      testClazz = tm.haveClass("Test" + boardName);
      testClazz.withImports("import org.junit.Test;", "import java.util.LinkedHashMap;", "import java.util.Map;",
            "import spark.Request;", "import spark.Response;", "import spark.Service;",
            "import java.util.concurrent.ExecutorService;", "import java.util.concurrent.Executors;",
            "import java.util.concurrent.LinkedBlockingQueue;",
            "import static org.assertj.core.api.Assertions.assertThat;", "import org.fulib.yaml.Yaml;",
            "import org.fulib.yaml.YamlIdMap;", "import java.util.concurrent.TimeUnit;",
            "import java.util.logging.Level;", "import java.util.logging.Logger;");
      testClazz.withImports(String.format("import %s;", em.getClassModel().getPackageName() + ".*"));

      tm.haveAttribute(testClazz, "eventBroker", "EventBroker");
      tm.haveAttribute(testClazz, "spark", "Service");
      tm.haveAttribute(testClazz, "eventQueue", "LinkedBlockingQueue<Event>");
      tm.haveAttribute(testClazz, "history", "LinkedHashMap<String, Event>");
      tm.haveAttribute(testClazz, "port", Type.INT);

      String declaration = "";
      String methodBody = "";
      ST st;

      declaration = "public void start()";
      st = group.getInstanceOf("testStart");
      methodBody = st.render();
      tm.haveMethod(testClazz, declaration, methodBody);

      declaration = "private String postApply(Request req, Response res)";
      st = group.getInstanceOf("testPostApply");
      methodBody = st.render();
      tm.haveMethod(testClazz, declaration, methodBody);

      declaration = "private void subscribeAndLoadOldEvents()";
      st = group.getInstanceOf("testSubscribe");
      methodBody = st.render();
      tm.haveMethod(testClazz, declaration, methodBody);

      declaration = "public Event waitForEvent(String id)";
      st = group.getInstanceOf("testWaitForEvent");
      methodBody = st.render();
      tm.haveMethod(testClazz, declaration, methodBody);

      declaration = "@Test\n" + "public void " + StrUtil.toIdentifier(eventModel.getEventStormingBoard().getName())
            + "()";

      startServices();

      for (Workflow workflow : eventStormingBoard.getWorkflows()) {
         testBody.append("\n// workflow " + workflow.getName() + "\n");
         for (WorkflowNote note : workflow.getNotes()) {
            // Send user events
            if (note instanceof PageNote) {
               PageNote pageNote = (PageNote) note;
               String port = pageNote.getService().getPort();
               testBody.append(String.format("\n// page %s\n", note.getTime()));
               String pageId = note.getTime().replaceAll("\\:", "_");
               testBody.append(String.format("open(\"http://localhost:%s/page/%s\");\n", port, pageId));
               for (PageLine line : pageNote.getLines()) {
                  String fill = line.getMap().get("fill");
                  if (fill != null) {
                     String id = line.getMap().get("input");
                     if (id == null) {
                        id = line.getMap().get("password");
                     }
                     testBody.append(String.format("$(\"#%s\").setValue(\"%s\");\n", id, fill));
                  }
               }
               String buttonId = pageNote.getButtonId();
               if (buttonId != null) {
                  testBody.append(String.format("$(\"#%s\").click();\n", buttonId));
               }
            } else if (note instanceof EventNote) {
               EventNote eventNote = (EventNote) note;
               Interaction interaction = eventNote.getInteraction();
               if (interaction instanceof UserInteraction) {
                  testGenerateSendUserEvent(testBody, eventNote);
               }
            } else if (note instanceof ExternalSystemNote) {
               ExternalSystemNote externalSystemNote = (ExternalSystemNote) note;
               for (Policy policy : ((ExternalSystemNote) note).getPolicies()) {
                  ClassModelManager modelManager = managerMap.get(policy.getService().getName());
                  addMockupData(modelManager, policy.getService(), policy, testBody, "publish");
               }
            }
         }
      }

      testBody.append(testClosing.toString());
      testBody.append( String.format("\nSystem.out.println(\"%s completed gracefully\");\n", boardName));

      tm.haveMethod(testClazz, declaration, testBody.toString());

      // add publish method to the test class
      declaration = "public void publish(Event event)";
      testBody.setLength(0);

      st = group.getInstanceOf("publishBody");
      testBody.append(st.render());
      tm.haveMethod(testClazz, declaration, testBody.toString());
      testClazz.withImports("import org.fulib.yaml.Yaml;", "import com.mashape.unirest.http.HttpResponse;",
            "import com.mashape.unirest.http.Unirest;", "import com.mashape.unirest.http.exceptions.UnirestException;",
            "import static com.codeborne.selenide.Selenide.open;", "import static com.codeborne.selenide.Selenide.$;",
            "import static com.codeborne.selenide.Condition.text;",
            "import static com.codeborne.selenide.Condition.matchText;",
            "import com.codeborne.selenide.SelenideElement;");
   }

   private void startServices() {
      testBody = new StringBuilder();
      testClosing = new StringBuilder();
      testClosing.append("" + "try {\n" + "   Thread.sleep(3000);\n" + "} catch (Exception e) {\n" + "}\n"
            + "eventBroker.stop();\n" + "spark.stop();\n");

      ST st = group.getInstanceOf("startEventBroker");
      testBody.append(st.render());

      for (ServiceNote service : eventStormingBoard.getServices()) {
         testGenerateServiceStart(testBody, service);
      }

      testBody.append("SelenideElement pre;\n");
      testBody.append("LinkedHashMap<String, Object> modelMap;\n");

   }

   private void testGenerateServiceStart(StringBuilder body, ServiceNote serviceNote) {
      String serviceName = StrUtil.cap(serviceNote.getName());
      String imp = String.format("import %s.%s.%sService;", mm.getClassModel().getPackageName(), serviceName,
            serviceName);
      testClazz.withImports(imp);

      body.append("\n");
      String serviceVarName = org.fulib.StrUtil.downFirstChar(serviceName);
      body.append("// start service\n");
      body.append(String.format("%sService %s = new %sService();\n", serviceName, serviceVarName, serviceName));
      body.append(String.format("%s.start();\n", serviceVarName));
      body.append(String.format("waitForEvent(\"%s\");\n", serviceNote.getPort()));

      testClosing.append(String.format("%s.stop();\n", serviceVarName));
   }

   private void testGenerateSendUserEvent(StringBuilder body, EventNote note) {
      if (note.getRaisingPage() == null) {
         // yes this event shall be send by a user, i.e. by our test
         // build it
         String varName = addCreateAndInitEventCode("", note, body);
         body.append(String.format("publish(%s);\n", varName));
      }

      body.append(String.format("waitForEvent(\"%s\");\n", note.getTime()));

      String checkHistory = "";

      LinkedHashMap<ServiceNote, String> lastChecks = new LinkedHashMap<>();
      LinkedList<Policy> policyList = new LinkedList<>(note.getPolicies());
      // check subscribers
      while (!policyList.isEmpty()) {
         Policy policy = policyList.poll();
         ServiceNote service = policy.getService();
         ClassModelManager serviceModelManager = managerMap.get(service.getName());
         StringBuilder check = new StringBuilder();
         check.append(String.format("\n// check %s\n", service.getName()));
         check.append(String.format("open(\"http://localhost:%s\");\n", service.getPort()));
         check.append(checkHistory);
         // load data events
         String loadDataEventsCode = String.format(""
               + "for (DataEvent dataEvent : %s.getBuilder().getEventStore().values()) {\n"
               + "   %1$s.getBuilder().load(dataEvent.getBlockId());\n" + "}\n"
               + "modelMap = %1$s.getBuilder().getModel().getModelMap();\n" + "if (modelMap.values().size() > 0) {\n"
               + "   org.fulib.FulibTools.objectDiagrams().dumpSVG(\"tmp/%1$s%s.svg\", modelMap.values());\n" + "}\n\n",
               StrUtil.decap(service.getName()), note.getTime().replaceAll("\\W+", "_"));
         check.append(loadDataEventsCode);
         check.append("");
         check.append(String.format("open(\"http://localhost:%s\");\n", service.getPort()));
         for (WorkflowNote step : policy.getSteps()) {
            if (step instanceof DataNote) {
               DataNote dataNote = (DataNote) step;
               String migratedTo = dataNote.getMap().get("@migratedTo");
               if (migratedTo != null) {
                  continue;
               }
               check.append(String.format("// check data note %s\n", dataNote.getTime()));
               check.append(String.format("%sBuilt e%s = (%1$sBuilt) waitForEvent(\"%s\");\n", dataNote.getDataType(),
                     eventModel.getObjectId(dataNote.getTime()), dataNote.getTime()));
               LinkedHashMap<String, String> mockup = getMockup(dataNote.getMap());
               Iterator<Map.Entry<String, String>> iterator = mockup.entrySet().iterator();
               Map.Entry<String, String> entry = iterator.next();
               String value = entry.getValue();
               String yamlId = StrUtil.decap(value.replaceAll("\\W+", "_"));
               Clazz dataTypeClass = serviceModelManager.haveClass(dataNote.getDataType());
               while (iterator.hasNext()) {
                  entry = iterator.next();
                  String key = entry.getKey();
                  if (key.endsWith(".back")) {
                     continue;
                  }
                  AssocRole role = dataTypeClass.getRole(key);
                  Attribute attribute = getAttribute(dataTypeClass, key);
                  value = entry.getValue();
                  if (attribute != null && role == null) {
                     check.append(String.format("assertThat(e%s.get%s()).isEqualTo(\"%s\");\n",
                           eventModel.getObjectId(dataNote.getTime()), StrUtil.cap(key), value));
                  } else if (role != null) {
                     check.append(String.format("assertThat(e%s.get%s()).isEqualTo(\"%s\");\n",
                           eventModel.getObjectId(dataNote.getTime()), StrUtil.cap(key), value));
                  }
               }
            } else if (step instanceof EventNote) {
               EventNote eventNote = (EventNote) step;
               policyList.addAll(eventNote.getPolicies());
            }
         }
         lastChecks.put(service, check.toString());
      }

      for (String check : lastChecks.values()) {
         body.append(check);
      }
   }

   private String addCreateAndInitEventCode(String indent, EventNote note, StringBuilder body) {
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
            varName = time.replaceAll("\\W+", "");
            if (!Character.isAlphabetic(varName.charAt(0))) {
               varName = "e" + varName;
            }
            statement = String.format("\n" + "%s// create %s: %s\n", indent, eventTypeName, entry.getValue());
            body.append(statement);
            statement = String.format("%s%s %s = new %s();\n", indent, eventTypeName, varName, eventTypeName);
            body.append(statement);
            statement = String.format("%s%s.setId(\"%s\");\n", indent, varName, id);
            body.append(statement);
            first = false;
            continue;
         }

         String setterName = org.fulib.StrUtil.cap(entry.getKey());
         if (indent.equals("")) {
            statement = String.format("%s%s.set%s(\"%s\");\n", indent, varName, setterName, entry.getValue());
         } else {
            statement = String.format("%s%s.set%s(request.queryParams(\"%s\"));\n", indent, varName, setterName,
                  entry.getKey());
         }
         body.append(statement);
      }
      return varName;
   }

   private void buildClassModelManagerMap(ClassModelManager mm) {
      managerMap = new LinkedHashMap<>();
      managerMap.put("mm", mm);

      em = new ClassModelManager().setMainJavaDir(mm.getClassModel().getMainJavaDir())
            .setPackageName(mm.getClassModel().getPackageName() + ".events");
      managerMap.put("em", em);

      Clazz event = em.haveClass("Event");
      em.haveAttribute(event, "id", Type.STRING);

      Clazz dataEvent = em.haveClass("DataEvent");
      dataEvent.setSuperClass(event);
      em.haveAttribute(dataEvent, "blockId", Type.STRING);

      Clazz subcribeEvent = em.haveClass("SubscribeEvent");
      subcribeEvent.setSuperClass(event);
      em.haveAttribute(subcribeEvent, "url", Type.STRING);

      Clazz dataGroup = em.haveClass("DataGroup");
      dataGroup.setSuperClass(dataEvent);

      em.associate(dataGroup, "elements", Type.MANY, dataEvent, "sagas", Type.MANY);

      Clazz query = em.haveClass("Query");
      query.setSuperClass(event);
      em.haveAttribute(query, "key", Type.STRING);
      em.associate(query, "results", Type.MANY, dataEvent);

      Clazz serviceSubscribed = em.haveClass("ServiceSubscribed");
      serviceSubscribed.setSuperClass(event);
      em.haveAttribute(serviceSubscribed, "serviceUrl", Type.STRING);
   }

   private void buildEventClasses() {
      for (Workflow workflow : eventStormingBoard.getWorkflows()) {
         for (WorkflowNote note : workflow.getNotes()) {
            if (note instanceof EventNote) {
               EventNote eventNote = (EventNote) note;
               oneEventClass(eventNote);
            } else if (note instanceof DataNote) {
               DataNote dataNote = (DataNote) note;
               oneDataEventClass(dataNote);
            }
         }
      }
   }

   private void oneDataEventClass(DataNote note) {
      Clazz event = em.haveClass("DataEvent");
      boolean first = true;
      String dataType = note.getDataType();
      String dataEventType = dataType + "Built";
      Clazz clazz = em.haveClass(dataEventType);
      clazz.setSuperClass(event);
      LinkedHashSet<String> keys = new LinkedHashSet<>(note.getMap().keySet());
      keys.remove("data");
      keys.remove(dataType);
      keys.remove(StrUtil.decap(dataType));
      for (String key : keys) {
         if (key.endsWith(".back")) {
            continue;
         }
         if (key.startsWith("@")) {
            continue;
         }
         mm.haveAttribute(clazz, key, "String");
      }
   }

   private void oneEventClass(EventNote note) {
      Clazz event = em.haveClass("Event");
      Clazz command = em.haveClass("Command");
      command.setSuperClass(event);
      boolean first = true;
      Clazz clazz = em.haveClass(note.getEventTypeName());
      if (note instanceof CommandNote) {
         clazz.setSuperClass(command);
      } else {
         clazz.setSuperClass(event);
      }
      LinkedHashSet<String> keys = new LinkedHashSet<>(note.getMap().keySet());
      keys.remove(keys.iterator().next());
      for (String key : keys) {
         mm.haveAttribute(clazz, key, "String");
      }
   }

   private void generateModelElementsFor(String event) {
      // event handler
      int index = StrUtil.indexOfLastUpperChar(event);
      String dataClassName = event.substring(0, index);

      // data class
      Clazz dataClazz = mm.haveClass(dataClassName);
      mm.haveAttribute(dataClazz, "id", "String");

      // getOrCreate method
      String declaration = String.format("public %s getOrCreate%s(String id)", dataClassName, dataClassName);
      String body = String.format(
            "" + "Object obj = objectMap.computeIfAbsent(id, k -> new %s().setId(k));\n" + "return (%s) obj;",
            dataClassName, dataClassName);
      mm.haveMethod(modelClazz, declaration, body);
   }

   public WorkflowGenerator generate() {
      for (ClassModelManager manager : managerMap.values()) {
         Fulib.generator().generate(manager.getClassModel());
         String classDiagramName = getPackageDirName(manager) + "/classDiagram.svg";
         FulibTools.classDiagrams().dumpSVG(manager.getClassModel(), classDiagramName);
      }

      return this;
   }

   private String getPackageDirName(ClassModelManager manager) {
      String packageDirName = manager.getClassModel().getPackageName().replaceAll("\\.", "/");
      packageDirName = manager.getClassModel().getMainJavaDir() + "/" + packageDirName;
      return packageDirName;
   }

}
