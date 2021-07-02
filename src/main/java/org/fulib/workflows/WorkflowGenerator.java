package org.fulib.workflows;

import org.fulib.Fulib;
import org.fulib.FulibTools;
import org.fulib.builder.ClassModelManager;
import org.fulib.builder.Type;
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
   private Clazz modelClazz;
   private LinkedHashMap<String, ClassModelManager> managerMap;
   private TreeMap<String, LinkedHashMap<String, String>> eventMap;
   private ClassModelManager em;
   private String workflowName;
   private ClassModelManager tm;
   private STGroupFile group;

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
      buildTest();

      for (Map.Entry<String, LinkedHashMap<String, String>> entry : eventMap.entrySet()) {
         oneEventClass(entry.getValue());
      }

      System.out.println();
      return this;
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

      Clazz testClazz = tm.haveClass("Test" + workflowName);
      testClazz.withImports("import org.junit.Test;");
      testClazz.withImports(String.format("import %s;",
            em.getClassModel().getPackageName() + ".EventBroker"));
      tm.haveAttribute(testClazz, "eventBroker", "EventBroker");

      String declaration = "@Test\n" +
            "public void " + workflowName + "()";
      ST st = group.getInstanceOf("startEventBroker");
      String body = st.render();
      tm.haveMethod(testClazz, declaration, body);
   }

   private void buildEventMap(String yaml)
   {
      eventMap = new TreeMap<>();

      ArrayList<LinkedHashMap<String, String>> maps = new Yamler().decodeList(yaml);

      for (LinkedHashMap<String, String> map : maps) {
         Map.Entry<String, String> entry = map.entrySet().iterator().next();
         if (entry.getKey().equals("WorkflowStarted")) {
            workflowName = entry.getValue();
            continue;
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
      packageDirName = manager.getClassModel().getMainJavaDir() + "/" +  packageDirName;
      return packageDirName;
   }
}
