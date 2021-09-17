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

public class TestPartyApp implements PropertyChangeListener {
   public static final String PROPERTY_EVENT_BROKER = "eventBroker";
   private EventBroker eventBroker;
   protected PropertyChangeSupport listeners;
   private LinkedBlockingQueue party1EventQueue = new LinkedBlockingQueue();
   private LinkedBlockingQueue party2EventQueue = new LinkedBlockingQueue();

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
            System.out.println("Alice listener got " + evt.getNewValue());
            party1EventQueue.put(evt.getNewValue());
         } catch (InterruptedException e) {
            e.printStackTrace();
         }
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

   @Before
   public void setTimeOut() {
      Configuration.timeout = Constants.TIME_OUT;
      Configuration.pageLoadTimeout = Configuration.timeout;
      Configuration.browserPosition = Constants.BROWSER_POS;
      Configuration.headless = Constants.HEADLESS;
   }

   @Test
   public void testLoadAndStoreConcept() throws IOException {
      System.err.println("starting party2 TestPartyApp testLoadAndStoreConcept");
      // start the event broker
      eventBroker = new EventBroker();
      eventBroker.start();

      PartyAppService bobNewPartyApp2 = new PartyAppService().setPort(42007);
      bobNewPartyApp2.listeners().addPropertyChangeListener(PartyAppService.PROPERTY_HISTORY, this);
      bobNewPartyApp2.start();

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
      bobNewPartyApp2.stop();
      System.out.println("party2 TestPartyApp testLoadAndStoreConcept done");
   }

   @Test
   public void testMigration() throws IOException {
      System.err.println("starting party2 TestPartyApp testMigration");
      // start the event broker
      eventBroker = new EventBroker();
      eventBroker.start();

      // start old service
      uks.debuggen.party.PartyApp.PartyAppService aliceOldPartyApp = new uks.debuggen.party.PartyApp.PartyAppService();
      aliceOldPartyApp.listeners().addPropertyChangeListener(new AliceListener());
      aliceOldPartyApp.start();

      // start new service
      PartyAppService bobNewPartyApp2 = new PartyAppService().setPort(42002);
      bobNewPartyApp2.listeners().addPropertyChangeListener(PartyAppService.PROPERTY_HISTORY, this);
      bobNewPartyApp2.start();

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
      aliceOldPartyApp.stop();
      bobNewPartyApp2.stop();
      System.out.println("party2 TestPartyApp testMigration done");
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

      // start service
      PartyAppService partyApp = new PartyAppService();
      partyApp.start();
      try {
         Thread.sleep(500);
      } catch (Exception e) {
      }

      open("http://localhost:42000");
      $("body").shouldHave(text("event broker"));

      SelenideElement pre = $("pre");
      pre.shouldHave(text("http://localhost:42001/apply"));
      LinkedHashMap<String, Object> modelMap;

      // workflow Overview

      // create UserRegisteredEvent: user registered 12:00
      UserRegisteredEvent e1200 = new UserRegisteredEvent();
      e1200.setId("12:00");
      publish(e1200);

      open("http://localhost:42000");
      pre = $("#history");
      pre.shouldHave(text("- 12_00:"));

      // create LoginSucceededEvent: login succeeded 13:00
      LoginSucceededEvent e1300 = new LoginSucceededEvent();
      e1300.setId("13:00");
      publish(e1300);

      open("http://localhost:42000");
      pre = $("#history");
      pre.shouldHave(text("- 13_00:"));

      // create PartyCreatedEvent: party created 14:00
      PartyCreatedEvent e1400 = new PartyCreatedEvent();
      e1400.setId("14:00");
      publish(e1400);

      open("http://localhost:42000");
      pre = $("#history");
      pre.shouldHave(text("- 14_00:"));

      // create ItemBookedEvent: item booked 15:00
      ItemBookedEvent e1500 = new ItemBookedEvent();
      e1500.setId("15:00");
      publish(e1500);

      open("http://localhost:42000");
      pre = $("#history");
      pre.shouldHave(text("- 15_00:"));

      // create SaldiComputedEvent: saldi computed 16:00
      SaldiComputedEvent e1600 = new SaldiComputedEvent();
      e1600.setId("16:00");
      publish(e1600);

      open("http://localhost:42000");
      pre = $("#history");
      pre.shouldHave(text("- 16_00:"));

      // workflow RegisterNewUser

      // page 12:00
      open("http://localhost:42001/page/12_00");
      $("#name").setValue("Alice");
      $("#ok").click();

      open("http://localhost:42000");
      pre = $("#history");
      pre.shouldHave(text("- 12_01:"));

      // check PartyApp
      open("http://localhost:42001");
      pre = $("#history");
      pre.shouldHave(text("- 12_01:"));
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

      open("http://localhost:42000");
      pre = $("#history");
      pre.shouldHave(text("- 12_03:"));

      // check PartyApp
      open("http://localhost:42001");
      pre = $("#history");
      pre.shouldHave(text("- 12_03:"));
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

      open("http://localhost:42000");
      pre = $("#history");
      pre.shouldHave(text("- 12_05:"));

      // check PartyApp
      open("http://localhost:42001");
      pre = $("#history");
      pre.shouldHave(text("- 12_05:"));
      for (DataEvent dataEvent : partyApp.getBuilder().getEventStore().values()) {
         partyApp.getBuilder().load(dataEvent.getBlockId());
      }
      modelMap = partyApp.getBuilder().getModel().getModelMap();
      if (modelMap.values().size() > 0) {
         org.fulib.FulibTools.objectDiagrams().dumpSVG("tmp/partyApp12_05.svg", modelMap.values());
      }

      open("http://localhost:42001");
      // check data note 12:05:02
      pre = $("#data");
      pre.shouldHave(text("- alice:"));
      pre.shouldHave(matchText("name:.*Alice"));
      pre.shouldHave(matchText("email:.*a@b.de"));
      pre.shouldHave(matchText("password:.*secret"));

      // page 12:06
      open("http://localhost:42001/page/12_06");
      $("#party").setValue("SE BBQ");
      $("#ok").click();

      open("http://localhost:42000");
      pre = $("#history");
      pre.shouldHave(text("- 12_07:"));

      // workflow LoginOldUser

      // page 13:00
      open("http://localhost:42001/page/13_00");
      $("#name").setValue("Alice");
      $("#ok").click();

      open("http://localhost:42000");
      pre = $("#history");
      pre.shouldHave(text("- 13_01:"));

      // check PartyApp
      open("http://localhost:42001");
      pre = $("#history");
      pre.shouldHave(text("- 13_01:"));
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

      open("http://localhost:42000");
      pre = $("#history");
      pre.shouldHave(text("- 13_03:"));

      // check PartyApp
      open("http://localhost:42001");
      pre = $("#history");
      pre.shouldHave(text("- 13_03:"));
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

      open("http://localhost:42000");
      pre = $("#history");
      pre.shouldHave(text("- 13_05:"));

      // workflow StartParty

      // page 13:55
      open("http://localhost:42001/page/13_55");
      $("#region").setValue("Kassel");
      $("#ok").click();

      open("http://localhost:42000");
      pre = $("#history");
      pre.shouldHave(text("- 13_56:"));

      // check PartyApp
      open("http://localhost:42001");
      pre = $("#history");
      pre.shouldHave(text("- 13_56:"));
      for (DataEvent dataEvent : partyApp.getBuilder().getEventStore().values()) {
         partyApp.getBuilder().load(dataEvent.getBlockId());
      }
      modelMap = partyApp.getBuilder().getModel().getModelMap();
      if (modelMap.values().size() > 0) {
         org.fulib.FulibTools.objectDiagrams().dumpSVG("tmp/partyApp13_56.svg", modelMap.values());
      }

      open("http://localhost:42001");
      // check data note 13:56:02
      pre = $("#data");
      pre.shouldHave(text("- kassel:"));

      // page 14:00
      open("http://localhost:42001/page/14_00");
      $("#party").setValue("SE BBQ");
      $("#date").setValue("after work");
      $("#location").setValue("Uni");
      $("#ok").click();

      open("http://localhost:42000");
      pre = $("#history");
      pre.shouldHave(text("- 14_01:"));

      // check PartyApp
      open("http://localhost:42001");
      pre = $("#history");
      pre.shouldHave(text("- 14_01:"));
      for (DataEvent dataEvent : partyApp.getBuilder().getEventStore().values()) {
         partyApp.getBuilder().load(dataEvent.getBlockId());
      }
      modelMap = partyApp.getBuilder().getModel().getModelMap();
      if (modelMap.values().size() > 0) {
         org.fulib.FulibTools.objectDiagrams().dumpSVG("tmp/partyApp14_01.svg", modelMap.values());
      }

      open("http://localhost:42001");
      // check data note 14:01:02
      pre = $("#data");
      pre.shouldHave(text("- sE_BBQ:"));
      // check data note 14:01:03
      pre = $("#data");
      pre.shouldHave(text("- sE_BBQ:"));
      pre.shouldHave(matchText("name:.*\"SE BBQ\""));
      pre.shouldHave(matchText("region:.*kassel"));
      pre.shouldHave(matchText("date:.*\"after work\""));
      pre.shouldHave(matchText("address:.*Uni"));

      // page 14:02
      open("http://localhost:42001/page/14_02");
      $("#add").click();

      open("http://localhost:42000");
      pre = $("#history");
      pre.shouldHave(text("- 14_03:"));

      // page 14:04
      open("http://localhost:42001/page/14_04");
      $("#item").setValue("beer");
      $("#price").setValue("12.00");
      $("#buyer").setValue("Bob");
      $("#ok").click();

      open("http://localhost:42000");
      pre = $("#history");
      pre.shouldHave(text("- 14_05:"));

      // check PartyApp
      open("http://localhost:42001");
      pre = $("#history");
      pre.shouldHave(text("- 14_05:"));
      for (DataEvent dataEvent : partyApp.getBuilder().getEventStore().values()) {
         partyApp.getBuilder().load(dataEvent.getBlockId());
      }
      modelMap = partyApp.getBuilder().getModel().getModelMap();
      if (modelMap.values().size() > 0) {
         org.fulib.FulibTools.objectDiagrams().dumpSVG("tmp/partyApp14_05.svg", modelMap.values());
      }

      open("http://localhost:42001");
      // check data note 14:05:01
      pre = $("#data");
      pre.shouldHave(text("- beer:"));
      pre.shouldHave(matchText("name:.*beer"));
      pre.shouldHave(matchText("price:.*12.00"));
      pre.shouldHave(matchText("buyer:.*sE_BBQ_Bob"));
      pre.shouldHave(matchText("party:.*sE_BBQ"));
      // check data note 14:05:02
      pre = $("#data");
      pre.shouldHave(text("- sE_BBQ_Bob:"));
      pre.shouldHave(matchText("name:.*Bob"));
      pre.shouldHave(matchText("party:.*sE_BBQ"));

      // page 14:06
      open("http://localhost:42001/page/14_06");
      $("#add").click();

      open("http://localhost:42000");
      pre = $("#history");
      pre.shouldHave(text("- 14_07:"));

      // page 14:08
      open("http://localhost:42001/page/14_08");
      $("#item").setValue("meat");
      $("#price").setValue("21.00");
      $("#buyer").setValue("Alice");
      $("#ok").click();

      open("http://localhost:42000");
      pre = $("#history");
      pre.shouldHave(text("- 14_09:"));

      // check PartyApp
      open("http://localhost:42001");
      pre = $("#history");
      pre.shouldHave(text("- 14_09:"));
      for (DataEvent dataEvent : partyApp.getBuilder().getEventStore().values()) {
         partyApp.getBuilder().load(dataEvent.getBlockId());
      }
      modelMap = partyApp.getBuilder().getModel().getModelMap();
      if (modelMap.values().size() > 0) {
         org.fulib.FulibTools.objectDiagrams().dumpSVG("tmp/partyApp14_09.svg", modelMap.values());
      }

      open("http://localhost:42001");
      // check data note 14:09:01
      pre = $("#data");
      pre.shouldHave(text("- meat:"));
      pre.shouldHave(matchText("name:.*meat"));
      pre.shouldHave(matchText("price:.*21.00"));
      pre.shouldHave(matchText("buyer:.*sE_BBQ_Alice"));
      pre.shouldHave(matchText("party:.*sE_BBQ"));
      // check data note 14:09:02
      pre = $("#data");
      pre.shouldHave(text("- sE_BBQ_Alice:"));
      pre.shouldHave(matchText("name:.*Alice"));
      pre.shouldHave(matchText("expenses:.*0.00"));
      pre.shouldHave(matchText("party:.*sE_BBQ"));

      // page 14:10
      open("http://localhost:42001/page/14_10");
      try {
         Thread.sleep(3000);
      } catch (Exception e) {
      }
      eventBroker.stop();
      partyApp.stop();

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

}
