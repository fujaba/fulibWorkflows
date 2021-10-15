package uks.debuggen.studyright;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.SelenideElement;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.fulib.workflows.html.HtmlGenerator3;
import org.fulib.yaml.Yaml;
import org.junit.Before;
import org.junit.Test;

import uks.debuggen.Constants;
import uks.debuggen.studyright.StudyRight.StudyRightService;
import uks.debuggen.studyright.events.*;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.beans.PropertyChangeSupport;
import java.util.LinkedHashMap;
import static com.codeborne.selenide.Condition.matchText;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import spark.Service;
import static org.assertj.core.api.Assertions.assertThat;
import spark.Request;
import spark.Response;
import org.fulib.yaml.YamlIdMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TestSomeEventStorming
{
   public static final String PROPERTY_EVENT_BROKER = "eventBroker";
   public static final String PROPERTY_SPARK = "spark";
   public static final String PROPERTY_EVENT_QUEUE = "eventQueue";
   public static final String PROPERTY_HISTORY = "history";
   public static final String PROPERTY_PORT = "port";
   private EventBroker eventBroker;
   protected PropertyChangeSupport listeners;
   private Service spark;
   private LinkedBlockingQueue<Event> eventQueue;
   private LinkedHashMap<String, Event> history;
   private int port;

   public EventBroker getEventBroker()
   {
      return this.eventBroker;
   }

   public TestSomeEventStorming setEventBroker(EventBroker value)
   {
      if (Objects.equals(value, this.eventBroker))
      {
         return this;
      }

      final EventBroker oldValue = this.eventBroker;
      this.eventBroker = value;
      this.firePropertyChange(PROPERTY_EVENT_BROKER, oldValue, value);
      return this;
   }

   public Service getSpark()
   {
      return this.spark;
   }

   public TestSomeEventStorming setSpark(Service value)
   {
      if (Objects.equals(value, this.spark))
      {
         return this;
      }

      final Service oldValue = this.spark;
      this.spark = value;
      this.firePropertyChange(PROPERTY_SPARK, oldValue, value);
      return this;
   }

   public LinkedBlockingQueue<Event> getEventQueue()
   {
      return this.eventQueue;
   }

   public TestSomeEventStorming setEventQueue(LinkedBlockingQueue<Event> value)
   {
      if (Objects.equals(value, this.eventQueue))
      {
         return this;
      }

      final LinkedBlockingQueue<Event> oldValue = this.eventQueue;
      this.eventQueue = value;
      this.firePropertyChange(PROPERTY_EVENT_QUEUE, oldValue, value);
      return this;
   }

   public LinkedHashMap<String, Event> getHistory()
   {
      return this.history;
   }

   public TestSomeEventStorming setHistory(LinkedHashMap<String, Event> value)
   {
      if (Objects.equals(value, this.history))
      {
         return this;
      }

      final LinkedHashMap<String, Event> oldValue = this.history;
      this.history = value;
      this.firePropertyChange(PROPERTY_HISTORY, oldValue, value);
      return this;
   }

   public int getPort()
   {
      return this.port;
   }

   public TestSomeEventStorming setPort(int value)
   {
      if (value == this.port)
      {
         return this;
      }

      final int oldValue = this.port;
      this.port = value;
      this.firePropertyChange(PROPERTY_PORT, oldValue, value);
      return this;
   }

   @Before
   public void setTimeOut() {
      Configuration.timeout = Constants.TIME_OUT;
      Configuration.pageLoadTimeout = Configuration.timeout;
      Configuration.browserPosition = Constants.BROWSER_POS;
      Configuration.headless = Constants.HEADLESS;
   }

   @Test
   public void testImplementation() throws IOException, InterruptedException
   {
      System.out.println("This is the new testImplementation");
      // no fulib
      eventBroker = new EventBroker();
      eventBroker.start();

      // start service
      StudyRightService studyRight = new StudyRightService();
      studyRight.start();

      open("http://localhost:42000");
      $("body").shouldHave(text("event broker"));

      SelenideElement pre = $("pre");
      pre.shouldHave(text("http://localhost:42400/apply"));

      // workflow working smoothly
      // page 11:00
      open("http://localhost:42400/page/welcome");
      $("#ok").click();


      // open("http://localhost:42000");
      // open("http://localhost:42400/page/welcome");
      String html = new HtmlGenerator3().generateHtml(studyRight.getHistory());

      Files.createDirectories(Path.of("tmp"));
      Files.write(Path.of("tmp/history.html"), html.getBytes(StandardCharsets.UTF_8));

      try {
         Thread.sleep(4000);
      } catch (Exception e) {
      }
      eventBroker.stop();
      studyRight.stop();

      System.out.println("testImplementation done");
   }

   @Test
   public void SomeEventStorming()
   {
      // no fulib
      // start the event broker
      eventBroker = new EventBroker();
      eventBroker.start();

      // start service
      StudyRightService studyRight = new StudyRightService();
      studyRight.start();

      try {
         Thread.sleep(2000);
      } catch (InterruptedException e1) {
      }

      open("http://localhost:42000");
      $("body").shouldHave(text("event broker"));

      System.out.println("SomeEventStorming selenide was able to check body");

      SelenideElement pre = $("pre");
      System.out.println("SomeEventStorming selenide was able to find <pre>");

      pre.shouldHave(text("http://localhost:42400/apply"));

      System.out.println("SomeEventStorming selenide was able to check 42400/apply");


      LinkedHashMap<String, Object> modelMap;

      // workflow working smoothly

      // page 11:00
      open("http://localhost:42400/page/11_00");
      $("#ok").click();

      open("http://localhost:42000");
      pre = $("#history");
      pre.shouldHave(text("- 12_00:"));

      // check StudyRight
      open("http://localhost:42400");
      pre = $("#history");
      pre.shouldHave(text("- 12_00:"));
      for (DataEvent dataEvent : studyRight.getBuilder().getEventStore().values()) {
         studyRight.getBuilder().load(dataEvent.getBlockId());
      }
      modelMap = studyRight.getBuilder().getModel().getModelMap();
      if (modelMap.values().size() > 0) {
         org.fulib.FulibTools.objectDiagrams().dumpSVG("tmp/studyRight12_00.svg", modelMap.values());
      }

      open("http://localhost:42400");
      // check data note 12:00:00
      pre = $("#data");
      pre.shouldHave(text("- studyRight:"));
      pre.shouldHave(matchText("rooms:.*math.*exam.*"));
      // check data note 12:00:01
      pre = $("#data");
      pre.shouldHave(text("- math:"));
      pre.shouldHave(matchText("credits:.*23"));
      pre.shouldHave(matchText("uni:.*studyRight"));
      pre.shouldHave(matchText("doors:.*modeling.*algebra.*"));
      // check data note 12:00:02
      pre = $("#data");
      pre.shouldHave(text("- modeling:"));
      pre.shouldHave(matchText("uni:.*studyRight"));
      pre.shouldHave(matchText("credits:.*42"));
      pre.shouldHave(matchText("doors:.*math.*algebra.*exam.*"));
      // check data note 12:00:03
      pre = $("#data");
      pre.shouldHave(text("- algebra:"));
      pre.shouldHave(matchText("uni:.*studyRight"));
      pre.shouldHave(matchText("credits:.*12"));
      // check data note 12:00:04
      pre = $("#data");
      pre.shouldHave(text("- exam:"));
      pre.shouldHave(matchText("credits:.*0"));
      pre.shouldHave(matchText("uni:.*studyRight"));
      // check data note 12:00:05
      pre = $("#data");
      pre.shouldHave(text("- carli:"));
      pre.shouldHave(matchText("name:.*Carli"));
      pre.shouldHave(matchText("birthYear:.*1970"));
      pre.shouldHave(matchText("studentId:.*stud42"));

      // create FindToursCommand: find tours 12:01
      FindToursCommand e1201 = new FindToursCommand();
      e1201.setId("12:01");
      publish(e1201);

      open("http://localhost:42000");
      pre = $("#history");
      pre.shouldHave(text("- 12_01:"));

      // check StudyRight
      open("http://localhost:42400");
      pre = $("#history");
      pre.shouldHave(text("- 12_01:"));
      for (DataEvent dataEvent : studyRight.getBuilder().getEventStore().values()) {
         studyRight.getBuilder().load(dataEvent.getBlockId());
      }
      modelMap = studyRight.getBuilder().getModel().getModelMap();
      if (modelMap.values().size() > 0) {
         org.fulib.FulibTools.objectDiagrams().dumpSVG("tmp/studyRight12_01.svg", modelMap.values());
      }

      open("http://localhost:42400");
      // check data note 12:01:00
      pre = $("#data");
      pre.shouldHave(text("- allTours:"));
      // check data note 12:01:01
      pre = $("#data");
      pre.shouldHave(text("- s01:"));
      pre.shouldHave(matchText("motivation:.*77"));

      // create VisitRoomCommand: visit room 12:02
      VisitRoomCommand e1202 = new VisitRoomCommand();
      e1202.setId("12:02");
      e1202.setRoom("math");
      e1202.setPreviousStop("s01");
      publish(e1202);

      open("http://localhost:42000");
      pre = $("#history");
      pre.shouldHave(text("- 12_02:"));

      // check StudyRight
      open("http://localhost:42400");
      pre = $("#history");
      pre.shouldHave(text("- 12_02:"));
      for (DataEvent dataEvent : studyRight.getBuilder().getEventStore().values()) {
         studyRight.getBuilder().load(dataEvent.getBlockId());
      }
      modelMap = studyRight.getBuilder().getModel().getModelMap();
      if (modelMap.values().size() > 0) {
         org.fulib.FulibTools.objectDiagrams().dumpSVG("tmp/studyRight12_02.svg", modelMap.values());
      }

      open("http://localhost:42400");
      // check data note 12:02:01
      pre = $("#data");
      pre.shouldHave(text("- s02:"));
      pre.shouldHave(matchText("room:.*math"));
      pre.shouldHave(matchText("motivation:.*54"));
      pre.shouldHave(matchText("previousStop:.*s01"));

      // create VisitRoomCommand: visit room 12:03
      VisitRoomCommand e1203 = new VisitRoomCommand();
      e1203.setId("12:03");
      e1203.setRoom("algebra");
      e1203.setPreviousStop("s02");
      publish(e1203);

      open("http://localhost:42000");
      pre = $("#history");
      pre.shouldHave(text("- 12_03:"));

      // check StudyRight
      open("http://localhost:42400");
      pre = $("#history");
      pre.shouldHave(text("- 12_03:"));
      for (DataEvent dataEvent : studyRight.getBuilder().getEventStore().values()) {
         studyRight.getBuilder().load(dataEvent.getBlockId());
      }
      modelMap = studyRight.getBuilder().getModel().getModelMap();
      if (modelMap.values().size() > 0) {
         org.fulib.FulibTools.objectDiagrams().dumpSVG("tmp/studyRight12_03.svg", modelMap.values());
      }

      open("http://localhost:42400");
      // check data note 12:03:01
      pre = $("#data");
      pre.shouldHave(text("- s03:"));
      pre.shouldHave(matchText("room:.*algebra"));
      pre.shouldHave(matchText("previousStop:.*s02"));
      pre.shouldHave(matchText("motivation:.*42"));

      // create VisitRoomCommand: visit room 12:04
      VisitRoomCommand e1204 = new VisitRoomCommand();
      e1204.setId("12:04");
      e1204.setRoom("modeling");
      e1204.setPreviousStop("s02");
      publish(e1204);

      open("http://localhost:42000");
      pre = $("#history");
      pre.shouldHave(text("- 12_04:"));

      // create VisitRoomCommand: visit room 12:05
      VisitRoomCommand e1205 = new VisitRoomCommand();
      e1205.setId("12:05");
      e1205.setPreviousStop("s03");
      e1205.setRoom("modeling");
      publish(e1205);

      open("http://localhost:42000");
      pre = $("#history");
      pre.shouldHave(text("- 12_05:"));

      // check StudyRight
      open("http://localhost:42400");
      pre = $("#history");
      pre.shouldHave(text("- 12_05:"));
      for (DataEvent dataEvent : studyRight.getBuilder().getEventStore().values()) {
         studyRight.getBuilder().load(dataEvent.getBlockId());
      }
      modelMap = studyRight.getBuilder().getModel().getModelMap();
      if (modelMap.values().size() > 0) {
         org.fulib.FulibTools.objectDiagrams().dumpSVG("tmp/studyRight12_05.svg", modelMap.values());
      }

      open("http://localhost:42400");
      // check data note 12:05:01
      pre = $("#data");
      pre.shouldHave(text("- s05:"));
      pre.shouldHave(matchText("room:.*modeling"));
      pre.shouldHave(matchText("previousStop:.*s03"));
      pre.shouldHave(matchText("motivation:.*0"));

      // create VisitRoomCommand: visit room 12:06
      VisitRoomCommand e1206 = new VisitRoomCommand();
      e1206.setId("12:06");
      e1206.setPreviousStop("s03");
      e1206.setRoom("math");
      publish(e1206);

      open("http://localhost:42000");
      pre = $("#history");
      pre.shouldHave(text("- 12_06:"));

      // create VisitRoomCommand: visit room 12:07
      VisitRoomCommand e1207 = new VisitRoomCommand();
      e1207.setId("12:07");
      e1207.setPreviousStop("s05");
      e1207.setRoom("math");
      publish(e1207);

      open("http://localhost:42000");
      pre = $("#history");
      pre.shouldHave(text("- 12_07:"));

      // check StudyRight
      open("http://localhost:42400");
      pre = $("#history");
      pre.shouldHave(text("- 12_07:"));
      for (DataEvent dataEvent : studyRight.getBuilder().getEventStore().values()) {
         studyRight.getBuilder().load(dataEvent.getBlockId());
      }
      modelMap = studyRight.getBuilder().getModel().getModelMap();
      if (modelMap.values().size() > 0) {
         org.fulib.FulibTools.objectDiagrams().dumpSVG("tmp/studyRight12_07.svg", modelMap.values());
      }

      open("http://localhost:42400");
      // check data note 12:07:01
      pre = $("#data");
      pre.shouldHave(text("- s07:"));
      pre.shouldHave(matchText("room:.*math"));
      pre.shouldHave(matchText("previousStop:.*s05"));
      pre.shouldHave(matchText("motivation:.*-23"));

      // create VisitRoomCommand: visit room 12:08
      VisitRoomCommand e1208 = new VisitRoomCommand();
      e1208.setId("12:08");
      e1208.setPreviousStop("s05");
      e1208.setRoom("exam");
      publish(e1208);

      open("http://localhost:42000");
      pre = $("#history");
      pre.shouldHave(text("- 12_08:"));

      // check StudyRight
      open("http://localhost:42400");
      pre = $("#history");
      pre.shouldHave(text("- 12_08:"));
      for (DataEvent dataEvent : studyRight.getBuilder().getEventStore().values()) {
         studyRight.getBuilder().load(dataEvent.getBlockId());
      }
      modelMap = studyRight.getBuilder().getModel().getModelMap();
      if (modelMap.values().size() > 0) {
         org.fulib.FulibTools.objectDiagrams().dumpSVG("tmp/studyRight12_08.svg", modelMap.values());
      }

      open("http://localhost:42400");
      // check data note 12:08:01
      pre = $("#data");
      pre.shouldHave(text("- s08:"));
      pre.shouldHave(matchText("room:.*exam"));
      pre.shouldHave(matchText("previousStop:.*s05"));
      pre.shouldHave(matchText("motivation:.*0"));

      // create CollectTourStopsCommand: collect tour stops 12:09
      CollectTourStopsCommand e1209 = new CollectTourStopsCommand();
      e1209.setId("12:09");
      e1209.setStop("s08");
      e1209.setTour("tour1");
      publish(e1209);

      open("http://localhost:42000");
      pre = $("#history");
      pre.shouldHave(text("- 12_09:"));

      // check StudyRight
      open("http://localhost:42400");
      pre = $("#history");
      pre.shouldHave(text("- 12_09:"));
      for (DataEvent dataEvent : studyRight.getBuilder().getEventStore().values()) {
         studyRight.getBuilder().load(dataEvent.getBlockId());
      }
      modelMap = studyRight.getBuilder().getModel().getModelMap();
      if (modelMap.values().size() > 0) {
         org.fulib.FulibTools.objectDiagrams().dumpSVG("tmp/studyRight12_09.svg", modelMap.values());
      }

      open("http://localhost:42400");
      // check data note 12:09:02
      pre = $("#data");
      pre.shouldHave(text("- tour1:"));
      pre.shouldHave(matchText("tourList:.*allTours"));
      pre.shouldHave(matchText("stops:.*exam"));

      // create CollectTourStopsCommand: collect tour stops 12:10
      CollectTourStopsCommand e1210 = new CollectTourStopsCommand();
      e1210.setId("12:10");
      e1210.setStop("s05");
      e1210.setTour("tour1");
      publish(e1210);

      open("http://localhost:42000");
      pre = $("#history");
      pre.shouldHave(text("- 12_10:"));

      // check StudyRight
      open("http://localhost:42400");
      pre = $("#history");
      pre.shouldHave(text("- 12_10:"));
      for (DataEvent dataEvent : studyRight.getBuilder().getEventStore().values()) {
         studyRight.getBuilder().load(dataEvent.getBlockId());
      }
      modelMap = studyRight.getBuilder().getModel().getModelMap();
      if (modelMap.values().size() > 0) {
         org.fulib.FulibTools.objectDiagrams().dumpSVG("tmp/studyRight12_10.svg", modelMap.values());
      }

      open("http://localhost:42400");
      // check data note 12:10:01
      pre = $("#data");
      pre.shouldHave(text("- tour1:"));
      pre.shouldHave(matchText("stops:.*\"modeling exam\""));

      // create CollectTourStopsCommand: collect tour stops 12:11
      CollectTourStopsCommand e1211 = new CollectTourStopsCommand();
      e1211.setId("12:11");
      e1211.setStop("s03");
      e1211.setTour("tour1");
      publish(e1211);

      open("http://localhost:42000");
      pre = $("#history");
      pre.shouldHave(text("- 12_11:"));

      // check StudyRight
      open("http://localhost:42400");
      pre = $("#history");
      pre.shouldHave(text("- 12_11:"));
      for (DataEvent dataEvent : studyRight.getBuilder().getEventStore().values()) {
         studyRight.getBuilder().load(dataEvent.getBlockId());
      }
      modelMap = studyRight.getBuilder().getModel().getModelMap();
      if (modelMap.values().size() > 0) {
         org.fulib.FulibTools.objectDiagrams().dumpSVG("tmp/studyRight12_11.svg", modelMap.values());
      }

      open("http://localhost:42400");
      // check data note 12:11:01
      pre = $("#data");
      pre.shouldHave(text("- tour1:"));
      pre.shouldHave(matchText("stops:.*\"algebra modeling exam\""));

      // create CollectTourStopsCommand: collect tour stops 12:12
      CollectTourStopsCommand e1212 = new CollectTourStopsCommand();
      e1212.setId("12:12");
      e1212.setStop("s02");
      e1212.setTour("tour1");
      publish(e1212);

      open("http://localhost:42000");
      pre = $("#history");
      pre.shouldHave(text("- 12_12:"));

      // check StudyRight
      open("http://localhost:42400");
      pre = $("#history");
      pre.shouldHave(text("- 12_12:"));
      for (DataEvent dataEvent : studyRight.getBuilder().getEventStore().values()) {
         studyRight.getBuilder().load(dataEvent.getBlockId());
      }
      modelMap = studyRight.getBuilder().getModel().getModelMap();
      if (modelMap.values().size() > 0) {
         org.fulib.FulibTools.objectDiagrams().dumpSVG("tmp/studyRight12_12.svg", modelMap.values());
      }

      open("http://localhost:42400");
      // check data note 12:12:01
      pre = $("#data");
      pre.shouldHave(text("- tour1:"));
      pre.shouldHave(matchText("stops:.*\"math algebra modeling exam\""));

      // page 12:13
      open("http://localhost:42400/page/12_13");
      try {
         Thread.sleep(3000);
      } catch (Exception e) {
      }
      eventBroker.stop();
      studyRight.stop();

      System.out.println();
   }

   public void publish(Event event)
   {
      String yaml = Yaml.encodeSimple(event);

      try {
         HttpResponse<String> response = Unirest.post("http://localhost:42000/publish")
               .body(yaml)
               .asString();
               Thread.sleep(200);
      }
      catch (Exception e) {
         e.printStackTrace();
      }
   }

   public boolean firePropertyChange(String propertyName, Object oldValue, Object newValue)
   {
      if (this.listeners != null)
      {
         this.listeners.firePropertyChange(propertyName, oldValue, newValue);
         return true;
      }
      return false;
   }

   public PropertyChangeSupport listeners()
   {
      if (this.listeners == null)
      {
         this.listeners = new PropertyChangeSupport(this);
      }
      return this.listeners;
   }

   public void start()
   {
      eventQueue = new LinkedBlockingQueue<Event>();
      history  = new LinkedHashMap<>();
      port = 41999;
      ExecutorService executor = Executors.newSingleThreadExecutor();
      spark = Service.ignite();
      spark.port(port);
      spark.post("/apply", (req, res) -> executor.submit(() -> this.postApply(req, res)).get());
      executor.submit(() -> System.out.println("test executor works"));
      executor.submit(this::subscribeAndLoadOldEvents);
      executor.submit(() -> System.out.println("test executor has done subscribeAndLoadOldEvents"));
   }

   private String postApply(Request req, Response res)
   {
      String body = req.body();
      try {
         YamlIdMap idMap = new YamlIdMap(Event.class.getPackageName());
         idMap.decode(body);
         Map<String, Object> map = idMap.getObjIdMap();
         for (Object value : map.values()) {
            Event event = (Event) value;
            eventQueue.put(event);
         }
      } catch (Exception e) {
         String message = e.getMessage();
         if (message.contains("ReflectorMap could not find class description")) {
            Logger.getGlobal().info("post apply ignores unknown event " + body);
         } else {
            Logger.getGlobal().log(Level.SEVERE, "postApply failed", e);
         }
      }
      return "apply done";
   }

   private void subscribeAndLoadOldEvents()
   {
      ServiceSubscribed serviceSubscribed = new ServiceSubscribed()
            .setServiceUrl(String.format("http://localhost:%d/apply", port));
      String json = Yaml.encodeSimple(serviceSubscribed);
      try {
         String url = "http://localhost:42000/subscribe";
         HttpResponse<String> response = Unirest.post(url).body(json).asString();
         String body = response.getBody();
         YamlIdMap idMap = new YamlIdMap(Event.class.getPackageName());
         idMap.decode(body);
         Map<String, Object> objectMap = idMap.getObjIdMap();
         for (Object obj : objectMap.values()) {
            Event event = (Event) obj;
            eventQueue.put(event);
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   public Event waitForEvent(String id)
   {
      while (true) {
         Event e = history.get(id);

         if (e != null) {
            return e;
         }

         try {
            e = eventQueue.poll(Configuration.timeout, TimeUnit.MILLISECONDS);
         }
         catch (Exception x) {
            throw new RuntimeException(x);
         }

         if (e == null) {
            throw new RuntimeException("event timeout waiting for " + id);
         }

         System.out.println("Test got event " + e.getId());
         history.put(e.getId(), e);
      }
   }
}
