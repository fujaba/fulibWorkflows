package uks.debuggen.party;
import com.codeborne.selenide.SelenideElement;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.fulib.yaml.Yaml;
import org.junit.Test;
import uks.debuggen.party.PartyApp.PartyAppService;
import uks.debuggen.party.events.*;

import static com.codeborne.selenide.Condition.matchText;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import java.util.Objects;
import java.beans.PropertyChangeSupport;

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

      $("body").shouldHave(text("choose party"));

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

      // page 12:05
      open("http://localhost:42001/page/12_05");
      $("#email").setValue("a@b.de");
      $("#ok").click();

      open("http://localhost:42000");
      pre = $("#history");
      pre.shouldHave(text("- 12_04:"));

      // page 12:06
      open("http://localhost:42001/page/12_06");
      $("#password").setValue("secret");
      $("#ok").click();

      open("http://localhost:42000");
      pre = $("#history");
      pre.shouldHave(text("- 12_07:"));

      // check PartyApp
      open("http://localhost:42001");
      pre = $("#history");
      pre.shouldHave(text("- 12_07:"));
      // check data note 12:07:01
      pre = $("#data");
      pre.shouldHave(text("- Alice:"));
      pre.shouldHave(matchText("name:.*Alice"));
      pre.shouldHave(matchText("email:.*a@b.de"));
      pre.shouldHave(matchText("password:.*secret"));

      // page 12:11
      open("http://localhost:42001/page/12_11");
      $("#party").setValue("SE BBQ");
      $("#ok").click();

      open("http://localhost:42000");
      pre = $("#history");
      pre.shouldHave(text("- 12_12:"));

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

      // page 13:06
      open("http://localhost:42001/page/13_06");
      $("#password").setValue("secret");
      $("#ok").click();

      open("http://localhost:42000");
      pre = $("#history");
      pre.shouldHave(text("- 13_07:"));

      // page 13:11
      open("http://localhost:42001/page/13_11");
      $("#party").setValue("SE BBQ");
      $("#ok").click();

      open("http://localhost:42000");
      pre = $("#history");
      pre.shouldHave(text("- 13_12:"));

      // workflow StartParty
      // page 14:00
      open("http://localhost:42001/page/14_00");
      $("#party").setValue("SE BBQ");
      $("#location").setValue("Uni");
      $("#ok").click();

      open("http://localhost:42000");
      pre = $("#history");
      pre.shouldHave(text("- 14_01:"));

      // check PartyApp
      open("http://localhost:42001");
      pre = $("#history");
      pre.shouldHave(text("- 14_01:"));
      // check data note 14:02
      pre = $("#data");
      pre.shouldHave(text("- sE_BBQ:"));
      pre.shouldHave(matchText("name:.*\"SE BBQ\""));
      pre.shouldHave(matchText("location:.*Uni"));

      // page 14:10
      open("http://localhost:42001/page/14_10");
      $("#add").click();

      open("http://localhost:42000");
      pre = $("#history");
      pre.shouldHave(text("- 14_11:"));

      // page 14:12
      open("http://localhost:42001/page/14_12");
      $("#item").setValue("beer");
      $("#price").setValue("12.00");
      $("#buyer").setValue("Bob");
      $("#ok").click();

      open("http://localhost:42000");
      pre = $("#history");
      pre.shouldHave(text("- 14_13:"));

      // check PartyApp
      open("http://localhost:42001");
      pre = $("#history");
      pre.shouldHave(text("- 14_13:"));
      // check data note 14:14
      pre = $("#data");
      pre.shouldHave(text("- beer:"));
      pre.shouldHave(matchText("name:.*beer"));
      pre.shouldHave(matchText("price:.*12.00"));
      pre.shouldHave(matchText("buyer:.*sE_BBQ_Bob"));
      pre.shouldHave(matchText("party:.*sE_BBQ"));
      // check data note 14:15
      pre = $("#data");
      pre.shouldHave(text("- sE_BBQ_Bob:"));
      pre.shouldHave(matchText("name:.*Bob"));
      pre.shouldHave(matchText("party:.*sE_BBQ"));

      // page 14:20
      open("http://localhost:42001/page/14_20");
      $("#add").click();

      open("http://localhost:42000");
      pre = $("#history");
      pre.shouldHave(text("- 14_21:"));

      // page 14:22
      open("http://localhost:42001/page/14_22");
      $("#item").setValue("meat");
      $("#price").setValue("21.00");
      $("#buyer").setValue("Alice");
      $("#ok").click();

      open("http://localhost:42000");
      pre = $("#history");
      pre.shouldHave(text("- 14_23:"));

      // check PartyApp
      open("http://localhost:42001");
      pre = $("#history");
      pre.shouldHave(text("- 14_23:"));
      // check data note 14:24
      pre = $("#data");
      pre.shouldHave(text("- meat:"));
      pre.shouldHave(matchText("name:.*meat"));
      pre.shouldHave(matchText("price:.*21.00"));
      pre.shouldHave(matchText("buyer:.*sE_BBQ_Alice"));
      pre.shouldHave(matchText("party:.*sE_BBQ"));
      // check data note 14:25
      pre = $("#data");
      pre.shouldHave(text("- sE_BBQ_Alice:"));
      pre.shouldHave(matchText("name:.*Alice"));
      pre.shouldHave(matchText("party:.*sE_BBQ"));

      // page 14:30
      open("http://localhost:42001/page/14_30");

      System.out.println();
   }

   public void publish(Event event)
   {
      String yaml = Yaml.encode(event);

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
