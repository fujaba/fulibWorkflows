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

      // register new user
      open("http://localhost:42001/page/getUserName");
      $("#name").setValue("Alice");
      $("#ok").click();

      $("#email").setValue("a@b.de");
      $("#ok").click();

      $("#password").setValue("secret");
      $("#ok").click();

      System.out.printf("");
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
