package uks.debuggen.party;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.SelenideElement;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.fulib.yaml.Yaml;
import org.junit.Before;
import org.junit.Test;
import uks.debuggen.party.PartyApp.PartyAppService;
import uks.debuggen.party.events.*;

import static com.codeborne.selenide.Condition.matchText;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import java.util.Objects;
import java.beans.PropertyChangeSupport;
import java.util.LinkedHashMap;

public class TestPartyApp
{
   public static final String PROPERTY_EVENT_BROKER = "eventBroker";
   private EventBroker eventBroker;
   protected PropertyChangeSupport listeners;

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
      Configuration.timeout = 10 * 60 * 1000;
      Configuration.pageLoadTimeout = Configuration.timeout;
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

      System.out.printf("");
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

      // start service
      PartyAppService partyApp = new PartyAppService();
      partyApp.start();

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
      open("http://localhost:42001");

      // page 13:04
      open("http://localhost:42001/page/13_04");
      $("#party").setValue("SE BBQ");
      $("#ok").click();

      open("http://localhost:42000");
      pre = $("#history");
      pre.shouldHave(text("- 13_05:"));

      // workflow StartParty
      // page 14:00
      open("http://localhost:42001/page/14_00");
      $("#party").setValue("SE BBQ");
      $("#date").setValue("Friday");
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
      open("http://localhost:42001");
      // check data note 14:01:02
      pre = $("#data");
      pre.shouldHave(text("- sE_BBQ:"));
      pre.shouldHave(matchText("name:.*\"SE BBQ\""));
      pre.shouldHave(matchText("date:.*Friday"));
      pre.shouldHave(matchText("location:.*Uni"));

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
