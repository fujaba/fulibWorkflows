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
<<<<<<< HEAD
import uks.debuggen.studyright.Welcome.WelcomeService;
=======
>>>>>>> d3ea970929d108027c425d763c0cb0d0be7e0237
import java.util.LinkedHashMap;
import static com.codeborne.selenide.Condition.matchText;

public class TestSomeEventStorming
{
   public static final String PROPERTY_EVENT_BROKER = "eventBroker";
   private EventBroker eventBroker;
   protected PropertyChangeSupport listeners;

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

   @Before
   public void setTimeOut() {
      Configuration.timeout = 10 * 60 * 1000;
      Configuration.pageLoadTimeout = Configuration.timeout;
   }

   @Test
   public void testImplentation() throws IOException
   {
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


      String html = new HtmlGenerator3().generateHtml(studyRight.getHistory());

      Files.write(Path.of("tmp/history.html"), html.getBytes(StandardCharsets.UTF_8));

      System.out.println();
   }

   @Test
   public void SomeEventStorming()
   {
      // start the event broker
      eventBroker = new EventBroker();
      eventBroker.start();

      // start service
      StudyRightService studyRight = new StudyRightService();
      studyRight.start();

      open("http://localhost:42000");
      $("body").shouldHave(text("event broker"));

      SelenideElement pre = $("pre");
      pre.shouldHave(text("http://localhost:42400/apply"));
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
<<<<<<< HEAD
      org.fulib.FulibTools.objectDiagrams().dumpSVG("tmp/studyRight12_00.svg", modelMap.values());

      open("http://localhost:42400");
      // check data note 12:00:00
      pre = $("#data");
      pre.shouldHave(text("- studyRight:"));
      pre.shouldHave(matchText("rooms:.*mathexam"));
      // check data note 12:00:01
      pre = $("#data");
      pre.shouldHave(text("- math:"));
      pre.shouldHave(matchText("credits:.*23"));
      pre.shouldHave(matchText("uni:.*studyRight"));
      pre.shouldHave(matchText("doors:.*modelingalgebra"));
      // check data note 12:00:02
      pre = $("#data");
      pre.shouldHave(text("- modeling:"));
      pre.shouldHave(matchText("uni:.*studyRight"));
      pre.shouldHave(matchText("credits:.*42"));
      pre.shouldHave(matchText("doors:.*mathalgebraexam"));
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
      pre.shouldHave(matchText("uni:.*StudyRight"));

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
      org.fulib.FulibTools.objectDiagrams().dumpSVG("tmp/studyRight12_01.svg", modelMap.values());

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
      org.fulib.FulibTools.objectDiagrams().dumpSVG("tmp/studyRight12_02.svg", modelMap.values());

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
      org.fulib.FulibTools.objectDiagrams().dumpSVG("tmp/studyRight12_03.svg", modelMap.values());

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
      org.fulib.FulibTools.objectDiagrams().dumpSVG("tmp/studyRight12_05.svg", modelMap.values());

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
      org.fulib.FulibTools.objectDiagrams().dumpSVG("tmp/studyRight12_07.svg", modelMap.values());

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
      org.fulib.FulibTools.objectDiagrams().dumpSVG("tmp/studyRight12_08.svg", modelMap.values());

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
      org.fulib.FulibTools.objectDiagrams().dumpSVG("tmp/studyRight12_09.svg", modelMap.values());

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
      org.fulib.FulibTools.objectDiagrams().dumpSVG("tmp/studyRight12_10.svg", modelMap.values());

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
      org.fulib.FulibTools.objectDiagrams().dumpSVG("tmp/studyRight12_11.svg", modelMap.values());

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
      org.fulib.FulibTools.objectDiagrams().dumpSVG("tmp/studyRight12_12.svg", modelMap.values());

=======
>>>>>>> d3ea970929d108027c425d763c0cb0d0be7e0237
      open("http://localhost:42400");
      // check data note 12:12:01
      pre = $("#data");
      pre.shouldHave(text("- tour1:"));
      pre.shouldHave(matchText("stops:.*\"math algebra modeling exam\""));

      // page 12:13
      open("http://localhost:42400/page/12_13");

      System.out.println();
   }

   public void publish(Event event)
   {
      String yaml = Yaml.encodeSimple(event);

      try {
         HttpResponse<String> response = Unirest.post("http://localhost:42000/publish")
               .body(yaml)
               .asString();
      }
      catch (UnirestException e) {
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
}
