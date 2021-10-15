package uks.debuggen.party;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.SelenideElement;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.fulib.yaml.Yaml;
import org.junit.Before;
import org.junit.Test;

import uks.debuggen.Constants;
import uks.debuggen.party.PartyApp.PartyAppService;
import uks.debuggen.party.events.*;

import static com.codeborne.selenide.Condition.matchText;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import java.util.Objects;
import java.beans.PropertyChangeSupport;
import java.util.LinkedHashMap;
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

public class TestPartyApp
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

   public TestPartyApp setEventBroker(EventBroker value)
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

   public TestPartyApp setSpark(Service value)
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

   public TestPartyApp setEventQueue(LinkedBlockingQueue<Event> value)
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

   public TestPartyApp setHistory(LinkedHashMap<String, Event> value)
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

   public TestPartyApp setPort(int value)
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
   public void testManual()
   {
      // start the event broker
      eventBroker = new EventBroker();
      eventBroker.start();

      // start service
      PartyAppService partyApp = new PartyAppService();
      partyApp.start();

      testLogin();

      $("body").shouldHave(text("Choose a party"));
      $("#party").setValue("SE BBQ");
      $("#location").setValue("Uni");
      $("#ok").click();

      $("body").shouldHave(text("no items yet"));

      $("#add").click();

      $("body").shouldHave(text("Let's do the"));

      $("#item").setValue("beer");
      $("#price").setValue("12.00");
      $("#buyer").setValue("Bob");
      $("#ok").click();

      $("#add").click();

      $("#item").setValue("meat");
      $("#price").setValue("21.00");
      $("#buyer").setValue("Alice");
      $("#ok").click();

      try {
         Thread.sleep(3000);
      } catch (Exception e) {
      }
      eventBroker.stop();
      partyApp.stop();

      System.err.println("");
   }

   private void testLogin()
   {
      // register new user
      open("http://localhost:42001/page/getUserName");
      $("#name").setValue("Alice");
      $("#ok").click();

      $("#email").setValue("a@b.de");
      $("#ok").click();

      $("#password").setValue("secret");
      $("#ok").click();

      $("body").shouldHave(text("Choose a party"));

      // another login
      open("http://localhost:42001/page/getUserName");
      $("#name").setValue("Alice");
      $("#ok").click();

      // wrong password
      $("#password").setValue("wrong");
      $("#ok").click();

      $("body").shouldHave(text("Please try again"));

      $("#changepassword").click();

      $("#email").setValue("wrong");
      $("#ok").click();

      $("body").shouldHave(text("Please use original email"));

      $("#email").setValue("a@b.de");
      $("#ok").click();

      $("#password").setValue("42");
      $("#ok").click();
   }

   @Test
   public void PartyApp()
   {
      // start the event broker
      eventBroker = new EventBroker();
      eventBroker.start();

      try {
         Thread.sleep(2000);
      } catch (InterruptedException e1) {
         e1.printStackTrace();
      }

      this.start();
      waitForEvent("" + port);

      // start service
      PartyAppService partyApp = new PartyAppService();
      partyApp.start();
      waitForEvent("42001");
      SelenideElement pre;
      LinkedHashMap<String, Object> modelMap;

      // workflow Overview

      // create UserRegisteredEvent: user registered 12:00
      UserRegisteredEvent e1200 = new UserRegisteredEvent();
      e1200.setId("12:00");
      publish(e1200);
      waitForEvent("12:00");

      // create LoginSucceededEvent: login succeeded 13:00
      LoginSucceededEvent e1300 = new LoginSucceededEvent();
      e1300.setId("13:00");
      publish(e1300);
      waitForEvent("13:00");

      // create PartyCreatedEvent: party created 14:00
      PartyCreatedEvent e1400 = new PartyCreatedEvent();
      e1400.setId("14:00");
      publish(e1400);
      waitForEvent("14:00");

      // create ItemBookedEvent: item booked 15:00
      ItemBookedEvent e1500 = new ItemBookedEvent();
      e1500.setId("15:00");
      publish(e1500);
      waitForEvent("15:00");

      // create SaldiComputedEvent: saldi computed 16:00
      SaldiComputedEvent e1600 = new SaldiComputedEvent();
      e1600.setId("16:00");
      publish(e1600);
      waitForEvent("16:00");

      // workflow RegisterNewUser

      // page 12:00
      open("http://localhost:42001/page/12_00");
      $("#name").setValue("Alice");
      $("#ok").click();
      waitForEvent("12:01");

      // check PartyApp
      open("http://localhost:42001");
      for (DataEvent dataEvent : partyApp.getBuilder().getEventStore().values()) {
         partyApp.getBuilder().load(dataEvent.getBlockId());
      }
      modelMap = partyApp.getBuilder().getModel().getModelMap();
      if (modelMap.values().size() > 0) {
         org.fulib.FulibTools.objectDiagrams().dumpSVG("tmp/partyApp12_01.svg", modelMap.values());
      }

      open("http://localhost:42001");

      // page 12:02
      open("http://localhost:42001/page/12_02");
      $("#email").setValue("a@b.de");
      $("#ok").click();
      waitForEvent("12:03");

      // check PartyApp
      open("http://localhost:42001");
      for (DataEvent dataEvent : partyApp.getBuilder().getEventStore().values()) {
         partyApp.getBuilder().load(dataEvent.getBlockId());
      }
      modelMap = partyApp.getBuilder().getModel().getModelMap();
      if (modelMap.values().size() > 0) {
         org.fulib.FulibTools.objectDiagrams().dumpSVG("tmp/partyApp12_03.svg", modelMap.values());
      }

      open("http://localhost:42001");

      // page 12:04
      open("http://localhost:42001/page/12_04");
      $("#password").setValue("secret");
      $("#ok").click();
      waitForEvent("12:05");

      // check PartyApp
      open("http://localhost:42001");
      for (DataEvent dataEvent : partyApp.getBuilder().getEventStore().values()) {
         partyApp.getBuilder().load(dataEvent.getBlockId());
      }
      modelMap = partyApp.getBuilder().getModel().getModelMap();
      if (modelMap.values().size() > 0) {
         org.fulib.FulibTools.objectDiagrams().dumpSVG("tmp/partyApp12_05.svg", modelMap.values());
      }

      open("http://localhost:42001");
      // check data note 12:05:02
      UserBuilt e12_05_02 = (UserBuilt) waitForEvent("12:05:02");
      assertThat(e12_05_02.getName()).isEqualTo("Alice");
      assertThat(e12_05_02.getEmail()).isEqualTo("a@b.de");
      assertThat(e12_05_02.getPassword()).isEqualTo("secret");

      // page 12:06
      open("http://localhost:42001/page/12_06");
      $("#party").setValue("SE BBQ");
      $("#ok").click();
      waitForEvent("12:07");

      // workflow LoginOldUser

      // page 13:00
      open("http://localhost:42001/page/13_00");
      $("#name").setValue("Alice");
      $("#ok").click();
      waitForEvent("13:01");

      // check PartyApp
      open("http://localhost:42001");
      for (DataEvent dataEvent : partyApp.getBuilder().getEventStore().values()) {
         partyApp.getBuilder().load(dataEvent.getBlockId());
      }
      modelMap = partyApp.getBuilder().getModel().getModelMap();
      if (modelMap.values().size() > 0) {
         org.fulib.FulibTools.objectDiagrams().dumpSVG("tmp/partyApp13_01.svg", modelMap.values());
      }

      open("http://localhost:42001");

      // page 13:02
      open("http://localhost:42001/page/13_02");
      $("#password").setValue("secret");
      $("#ok").click();
      waitForEvent("13:03");

      // check PartyApp
      open("http://localhost:42001");
      for (DataEvent dataEvent : partyApp.getBuilder().getEventStore().values()) {
         partyApp.getBuilder().load(dataEvent.getBlockId());
      }
      modelMap = partyApp.getBuilder().getModel().getModelMap();
      if (modelMap.values().size() > 0) {
         org.fulib.FulibTools.objectDiagrams().dumpSVG("tmp/partyApp13_03.svg", modelMap.values());
      }

      open("http://localhost:42001");

      // page 13:04
      open("http://localhost:42001/page/13_04");
      $("#party").setValue("SE BBQ");
      $("#ok").click();
      waitForEvent("13:05");

      // workflow StartParty

      // page 14:00
      open("http://localhost:42001/page/14_00");
      $("#party").setValue("SE BBQ");
      $("#date").setValue("Friday");
      $("#location").setValue("Uni");
      $("#ok").click();
      waitForEvent("14:01");

      // check PartyApp
      open("http://localhost:42001");
      for (DataEvent dataEvent : partyApp.getBuilder().getEventStore().values()) {
         partyApp.getBuilder().load(dataEvent.getBlockId());
      }
      modelMap = partyApp.getBuilder().getModel().getModelMap();
      if (modelMap.values().size() > 0) {
         org.fulib.FulibTools.objectDiagrams().dumpSVG("tmp/partyApp14_01.svg", modelMap.values());
      }

      open("http://localhost:42001");
      // check data note 14:01:02
      PartyBuilt e14_01_02 = (PartyBuilt) waitForEvent("14:01:02");
      assertThat(e14_01_02.getName()).isEqualTo("SE BBQ");
      assertThat(e14_01_02.getDate()).isEqualTo("Friday");
      assertThat(e14_01_02.getLocation()).isEqualTo("Uni");

      // page 14:02
      open("http://localhost:42001/page/14_02");
      $("#add").click();
      waitForEvent("14:03");

      // page 14:04
      open("http://localhost:42001/page/14_04");
      $("#item").setValue("beer");
      $("#price").setValue("12.00");
      $("#buyer").setValue("Bob");
      $("#ok").click();
      waitForEvent("14:05");

      // check PartyApp
      open("http://localhost:42001");
      for (DataEvent dataEvent : partyApp.getBuilder().getEventStore().values()) {
         partyApp.getBuilder().load(dataEvent.getBlockId());
      }
      modelMap = partyApp.getBuilder().getModel().getModelMap();
      if (modelMap.values().size() > 0) {
         org.fulib.FulibTools.objectDiagrams().dumpSVG("tmp/partyApp14_05.svg", modelMap.values());
      }

      open("http://localhost:42001");
      // check data note 14:05:01
      ItemBuilt e14_05_01 = (ItemBuilt) waitForEvent("14:05:01");
      assertThat(e14_05_01.getName()).isEqualTo("beer");
      assertThat(e14_05_01.getPrice()).isEqualTo("12.00");
      assertThat(e14_05_01.getBuyer()).isEqualTo("sE_BBQ_Bob");
      assertThat(e14_05_01.getParty()).isEqualTo("sE_BBQ");
      // check data note 14:05:02
      GuestBuilt e14_05_02 = (GuestBuilt) waitForEvent("14:05:02");
      assertThat(e14_05_02.getName()).isEqualTo("Bob");
      assertThat(e14_05_02.getParty()).isEqualTo("sE_BBQ");

      // page 14:06
      open("http://localhost:42001/page/14_06");
      $("#add").click();
      waitForEvent("14:07");

      // page 14:08
      open("http://localhost:42001/page/14_08");
      $("#item").setValue("meat");
      $("#price").setValue("21.00");
      $("#buyer").setValue("Alice");
      $("#ok").click();
      waitForEvent("14:09");

      // check PartyApp
      open("http://localhost:42001");
      for (DataEvent dataEvent : partyApp.getBuilder().getEventStore().values()) {
         partyApp.getBuilder().load(dataEvent.getBlockId());
      }
      modelMap = partyApp.getBuilder().getModel().getModelMap();
      if (modelMap.values().size() > 0) {
         org.fulib.FulibTools.objectDiagrams().dumpSVG("tmp/partyApp14_09.svg", modelMap.values());
      }

      open("http://localhost:42001");
      // check data note 14:09:01
      ItemBuilt e14_09_01 = (ItemBuilt) waitForEvent("14:09:01");
      assertThat(e14_09_01.getName()).isEqualTo("meat");
      assertThat(e14_09_01.getPrice()).isEqualTo("21.00");
      assertThat(e14_09_01.getBuyer()).isEqualTo("sE_BBQ_Alice");
      assertThat(e14_09_01.getParty()).isEqualTo("sE_BBQ");
      // check data note 14:09:02
      GuestBuilt e14_09_02 = (GuestBuilt) waitForEvent("14:09:02");
      assertThat(e14_09_02.getName()).isEqualTo("Alice");
      assertThat(e14_09_02.getExpenses()).isEqualTo("0.00");
      assertThat(e14_09_02.getParty()).isEqualTo("sE_BBQ");

      // page 14:10
      open("http://localhost:42001/page/14_10");
      try {
         Thread.sleep(3000);
      } catch (Exception e) {
      }
      eventBroker.stop();
      spark.stop();
      partyApp.stop();

      System.err.println("PartyApp completed good and gracefully");
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
      Unirest.setTimeouts(3*60*1000, 3*60*1000);
      eventQueue = new LinkedBlockingQueue<Event>();
      history  = new LinkedHashMap<>();
      port = 41999;
      ExecutorService executor = Executors.newSingleThreadExecutor();
      spark = Service.ignite();
      spark.port(port);
      spark.post("/apply", (req, res) -> executor.submit(() -> this.postApply(req, res)).get());
      spark.init();
      executor.submit(() -> System.err.println("test executor works"));
      executor.submit(this::subscribeAndLoadOldEvents);
      executor.submit(() -> System.err.println("test executor has done subscribeAndLoadOldEvents"));
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
         System.err.println("Test has completed subscribeAndLoadOldEvents");
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

         System.err.println("Test got event " + e.getId());
         history.put(e.getId(), e);
      }
   }
}
