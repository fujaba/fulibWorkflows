package uks.debuggen.party2;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.SelenideElement;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.fulib.FulibTools;
import org.fulib.yaml.Yaml;
import org.junit.Before;
import org.junit.Test;

import uks.debuggen.Constants;
import uks.debuggen.party.PartyApp.Party;
import uks.debuggen.party2.PartyApp.Party2;
import uks.debuggen.party2.PartyApp.PartyAppService;
import uks.debuggen.party2.PartyApp.User;
import uks.debuggen.party2.events.*;
import static com.codeborne.selenide.Condition.matchText;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static org.assertj.core.api.Assertions.assertThat;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.Objects;
import java.beans.PropertyChangeSupport;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.LinkedHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import spark.Service;
import spark.Request;
import spark.Response;
import org.fulib.yaml.YamlIdMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TestPartyApp implements PropertyChangeListener {
   public static final String PROPERTY_EVENT_BROKER = "eventBroker";
   public static final String PROPERTY_SPARK = "spark";
   public static final String PROPERTY_EVENT_QUEUE = "eventQueue";
   public static final String PROPERTY_HISTORY = "history";
   public static final String PROPERTY_PORT = "port";
   private EventBroker eventBroker;
   protected PropertyChangeSupport listeners;
   private LinkedBlockingQueue party1EventQueue = new LinkedBlockingQueue();
   private LinkedBlockingQueue party2EventQueue = new LinkedBlockingQueue();
   private Service spark;
   private LinkedBlockingQueue<Event> eventQueue;
   private LinkedHashMap<String, Event> history;
   private int port;

   @Override
   public void propertyChange(PropertyChangeEvent evt) {
      try {
         party2EventQueue.put(evt.getNewValue());
      } catch (InterruptedException e) {
         e.printStackTrace();
      }
   }

   class AliceListener implements PropertyChangeListener {

      @Override
      public void propertyChange(PropertyChangeEvent evt) {
         try {
            System.err.println("Alice listener got " + evt.getNewValue());
            party1EventQueue.put(evt.getNewValue());
         } catch (InterruptedException e) {
            e.printStackTrace();
         }
      }
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
      Configuration.browserSize = "400x500";
      Configuration.headless = Constants.HEADLESS;
   }

   @Test
   public void testLoadAndStoreConcept() throws IOException {
      System.err.println("starting party2 TestPartyApp testLoadAndStoreConcept");
      // start the event broker
      eventBroker = new EventBroker();
      eventBroker.start();

      this.start();
      waitForEvent("" + port);

      waitForEvent("41999");

      PartyAppService bobNewPartyApp2 = new PartyAppService().setPort(42007);
      bobNewPartyApp2.listeners().addPropertyChangeListener(PartyAppService.PROPERTY_HISTORY, this);
      bobNewPartyApp2.start();

      waitForEvent("42007");


      open("http://localhost:42000");
      $("body").shouldHave(text("event broker"));
      login("http://localhost:42007/page/getUserName", "Bob", "b@b.de");

      User bob = (User) bobNewPartyApp2.getBuilder().load("Bob");
      assertThat(bob.getEmail()).isEqualTo("b@b.de");

      $("body").shouldHave(text("In which region is your party"));
      $("#region").setValue("Kassel");
      $("#ok").click();

      $("body").shouldHave(text("Choose a party in Kassel"));
      $("#party").setValue("Finals");
      $("#location").setValue("Fritze");
      $("#ok").click();

      Party2 party = (Party2) bobNewPartyApp2.getBuilder().load("Kassel.Finals");
      assertThat(party.getRegion().getId()).isEqualTo("Kassel");

      $("body").shouldHave(text("Let's do the Finals on in Kassel"));

      bookItem("Wine", "9.99", "Bob");

      for (DataEvent dataEvent : bobNewPartyApp2.getBuilder().getEventStore().values()) {
         bobNewPartyApp2.getBuilder().load(dataEvent.getBlockId());
      }
      party = (Party2) bobNewPartyApp2.getBuilder().load("Kassel.Finals");
      assertThat(party.getItems().size()).isEqualTo(1);

      FulibTools.objectDiagrams().dumpSVG("tmp/FinalsParty.svg", party);

      try {
         Thread.sleep(4000);
      } catch (Exception e) {
      }
      eventBroker.stop();
      spark.stop();
      bobNewPartyApp2.stop();
      System.err.println("party2 TestPartyApp testLoadAndStoreConcept done");
   }

   @Test
   public void testMigration() throws IOException {
      System.err.println("starting party2 TestPartyApp testMigration");
      // start the event broker
      eventBroker = new EventBroker();
      eventBroker.start();

      start();

      waitForEvent("41999");

      // start old service
      uks.debuggen.party.PartyApp.PartyAppService aliceOldPartyApp = new uks.debuggen.party.PartyApp.PartyAppService();
      aliceOldPartyApp.listeners().addPropertyChangeListener(new AliceListener());
      aliceOldPartyApp.start();


      waitForEvent("42001");

      // start new service
      PartyAppService bobNewPartyApp2 = new PartyAppService().setPort(42002);
      bobNewPartyApp2.listeners().addPropertyChangeListener(PartyAppService.PROPERTY_HISTORY, this);
      bobNewPartyApp2.start();

      waitForEvent("42002");

      open("http://localhost:42000");
      $("body").shouldHave(text("event broker"));
      login("http://localhost:42001/page/getUserName", "Alice", "a@b.de");
      login("http://localhost:42002/page/getUserName", "Bob", "b@b.de");

      // Alice create SE BBQ in old app
      aliceOpenSEBBQ();

      // check
      Party oldBBQ = (Party) aliceOldPartyApp.getBuilder().load("SE BBQ");
      assertThat(oldBBQ).isNotNull();
      assertThat(oldBBQ.getDate()).isEqualTo("Friday");

      // bob should be informed about the SE BBQ
      Party2Built party2Built = (Party2Built) retrieveBuiltEventFromParty2("SE BBQ");

      assertThat(party2Built).isNotNull();
      assertThat(party2Built.getDate()).isEqualTo("Friday");

      // bob adds region kassel
      bobOpenKasselParty("SE BBQ", "Friday", "Uni");

      $("#add").click();

      $("body").shouldHave(text("Book an item"));
      $("#item").setValue("beer");
      $("#price").setValue("12.00");
      $("#buyer").setValue("Bob");
      $("#ok").click();

      retrieveBuiltEventFromParty1("SE BBQ#beer");

      // Alice Buys meat for the SE BBQ on the old app
      aliceOpenSEBBQ();

      $("body").shouldHave(text("beer 12.00 Bob"));
      bookItem("meat", "21.00", "Alice");

      $("body").shouldHave(text("meat 21.00 Alice"));

      bobOpenKasselParty("SE BBQ", "Friday", "Uni");

      $("body").shouldHave(text("meat 21.00 Alice"));

      // bob starts the Finals party at the Fritze in Kassel
      bobOpenKasselParty("Finals", "Saturday", "Fritze");

      bookItem("Wine", "9.99", "Carli");

      uks.debuggen.party.events.DataEvent dataEvent = retrieveBuiltEventFromParty1("Kassel.Finals#Wine");

      FulibTools.objectDiagrams().dumpSVG("tmp/KasselFinalsOld.svg",
            aliceOldPartyApp.getBuilder().getModel().getModelMap().values());
      FulibTools.objectDiagrams().dumpSVG("tmp/KasselFinalsNew.svg",
            bobNewPartyApp2.getBuilder().getModel().getModelMap().values());

      try {
         Thread.sleep(4000);
      } catch (Exception e) {
      }

      eventBroker.stop();
      spark.stop();
      aliceOldPartyApp.stop();
      bobNewPartyApp2.stop();
      System.err.println("party2 TestPartyApp testMigration done");
   }

   private void bookItem(String item, String price, String buyer) {
      $("#add").click();

      $("#item").setValue(item);
      $("#price").setValue(price);
      $("#buyer").setValue(buyer);
      $("#ok").click();
   }

   private void bobOpenKasselParty(String partyName, String date, String location) {
      open("http://localhost:42002/page/withPassword?password=secret&name=Bob&email=b%40b.de&button=ok");
      $("body").shouldHave(text("In which region is your party"));
      $("#region").setValue("Kassel");
      $("#ok").click();

      $("body").shouldHave(text("Choose a party in Kassel"));
      $("#party").setValue(partyName);
      $("#date").setValue(date);
      $("#location").setValue(location);
      $("#ok").click();
   }

   private void aliceOpenSEBBQ() {
      open("http://localhost:42001/page/withPassword?password=secret&name=Alice&email=a%40b.de&button=ok");
      $("body").shouldHave(text("Choose a party"));
      $("#party").setValue("SE BBQ");
      $("#date").setValue("Friday");
      $("#location").setValue("Uni");
      $("#ok").click();
   }

   private uks.debuggen.party.events.DataEvent retrieveBuiltEventFromParty1(String blockId) {
      uks.debuggen.party.events.DataEvent dataEvent = null;
      while (true) {
         try {
            Object e = party1EventQueue.take();
            if (e instanceof uks.debuggen.party.events.DataEvent) {
               dataEvent = (uks.debuggen.party.events.DataEvent) e;
               if (dataEvent.getBlockId().equals(blockId)) {
                  break;
               }
            }
         } catch (InterruptedException ex) {
            ex.printStackTrace();
         }
      }
      return dataEvent;
   }

   private DataEvent retrieveBuiltEventFromParty2(String blockId) {
      DataEvent dataEvent = null;
      while (true) {
         try {
            Object e = party2EventQueue.take();
            if (e instanceof DataEvent) {
               dataEvent = (DataEvent) e;
               if (dataEvent.getBlockId().equals(blockId)) {
                  break;
               }
            }
         } catch (InterruptedException ex) {
            ex.printStackTrace();
         }
      }
      return dataEvent;
   }

   private void login(String s, String alice, String s2) {
      // Alice login to old party app
      open(s);
      $("#name").setValue(alice);
      $("#ok").click();

      $("#email").setValue(s2);
      $("#ok").click();

      $("#password").setValue("secret");
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

      // page 13:55
      open("http://localhost:42001/page/13_55");
      $("#region").setValue("Kassel");
      $("#ok").click();
      waitForEvent("13:56");

      // check PartyApp
      open("http://localhost:42001");
      for (DataEvent dataEvent : partyApp.getBuilder().getEventStore().values()) {
         partyApp.getBuilder().load(dataEvent.getBlockId());
      }
      modelMap = partyApp.getBuilder().getModel().getModelMap();
      if (modelMap.values().size() > 0) {
         org.fulib.FulibTools.objectDiagrams().dumpSVG("tmp/partyApp13_56.svg", modelMap.values());
      }

      open("http://localhost:42001");
      // check data note 13:56:02
      RegionBuilt e13_56_02 = (RegionBuilt) waitForEvent("13:56:02");

      // page 14:00
      open("http://localhost:42001/page/14_00");
      $("#party").setValue("SE BBQ");
      $("#date").setValue("after work");
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
      Party2Built e14_01_02 = (Party2Built) waitForEvent("14:01:02");
      assertThat(e14_01_02.getName()).isEqualTo("SE BBQ");
      assertThat(e14_01_02.getRegion()).isEqualTo("Kassel");
      assertThat(e14_01_02.getDate()).isEqualTo("after work");
      assertThat(e14_01_02.getAddress()).isEqualTo("Uni");

      // page 14:02
      open("http://localhost:42001/page/14_02");
      $("#add").click();
      waitForEvent("14:02:01");

      // page 14:03
      open("http://localhost:42001/page/14_03");
      $("#item").setValue("beer");
      $("#price").setValue("12.00");
      $("#buyer").setValue("Bob");
      $("#ok").click();
      waitForEvent("14:03:01");

      // check PartyApp
      open("http://localhost:42001");
      for (DataEvent dataEvent : partyApp.getBuilder().getEventStore().values()) {
         partyApp.getBuilder().load(dataEvent.getBlockId());
      }
      modelMap = partyApp.getBuilder().getModel().getModelMap();
      if (modelMap.values().size() > 0) {
         org.fulib.FulibTools.objectDiagrams().dumpSVG("tmp/partyApp14_03_01.svg", modelMap.values());
      }

      open("http://localhost:42001");
      // check data note 14:03:02
      ItemBuilt e14_03_02 = (ItemBuilt) waitForEvent("14:03:02");
      assertThat(e14_03_02.getName()).isEqualTo("beer");
      assertThat(e14_03_02.getPrice()).isEqualTo("12.00");
      assertThat(e14_03_02.getBuyer()).isEqualTo("sE_BBQ_Bob");
      assertThat(e14_03_02.getParty()).isEqualTo("SE BBQ");
      // check data note 14:03:03
      GuestBuilt e14_03_03 = (GuestBuilt) waitForEvent("14:03:03");
      assertThat(e14_03_03.getName()).isEqualTo("Bob");
      assertThat(e14_03_03.getParty()).isEqualTo("SE BBQ");

      // page 14:04
      open("http://localhost:42001/page/14_04");
      $("#add").click();
      waitForEvent("14:07");

      // page 14:08
      open("http://localhost:42001/page/14_08");
      $("#item").setValue("meat");
      $("#price").setValue("21.00");
      $("#buyer").setValue("Alice");
      $("#ok").click();
      waitForEvent("14:08:01");

      // check PartyApp
      open("http://localhost:42001");
      for (DataEvent dataEvent : partyApp.getBuilder().getEventStore().values()) {
         partyApp.getBuilder().load(dataEvent.getBlockId());
      }
      modelMap = partyApp.getBuilder().getModel().getModelMap();
      if (modelMap.values().size() > 0) {
         org.fulib.FulibTools.objectDiagrams().dumpSVG("tmp/partyApp14_08_01.svg", modelMap.values());
      }

      open("http://localhost:42001");
      // check data note 14:08:02
      ItemBuilt e14_08_02 = (ItemBuilt) waitForEvent("14:08:02");
      assertThat(e14_08_02.getName()).isEqualTo("meat");
      assertThat(e14_08_02.getPrice()).isEqualTo("21.00");
      assertThat(e14_08_02.getBuyer()).isEqualTo("sE_BBQ_Alice");
      assertThat(e14_08_02.getParty()).isEqualTo("SE BBQ");
      // check data note 14:09:01
      GuestBuilt e14_09_01 = (GuestBuilt) waitForEvent("14:09:01");
      assertThat(e14_09_01.getName()).isEqualTo("Alice");
      assertThat(e14_09_01.getExpenses()).isEqualTo("0.00");
      assertThat(e14_09_01.getParty()).isEqualTo("SE BBQ");

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

}
