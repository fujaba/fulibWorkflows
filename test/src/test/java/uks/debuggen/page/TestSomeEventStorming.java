package uks.debuggen.page;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

import java.beans.PropertyChangeSupport;
import java.util.LinkedHashMap;
import java.util.Objects;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.SelenideElement;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;

import org.fulib.yaml.Yaml;
import org.junit.Before;
import org.junit.Test;

import uks.debuggen.Constants;
import uks.debuggen.page.Shop.ShopService;
import uks.debuggen.page.events.Event;
import uks.debuggen.page.events.EventBroker;
import uks.debuggen.page.events.ProductStoredEvent;
import com.mashape.unirest.http.exceptions.UnirestException;
import uks.debuggen.page.events.*;
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
      Configuration.timeout = Constants.TIME_OUT;
      Configuration.pageLoadTimeout = Configuration.timeout;
      Configuration.browserPosition = Constants.BROWSER_POS;
      Configuration.headless = Constants.HEADLESS;
   }

   @Test
   public void SomeEventStorming()
   {
      // start the event broker
      eventBroker = new EventBroker();
      eventBroker.start();

      // start service
      ShopService shop = new ShopService();
      shop.start();
      try {
         Thread.sleep(1500);
      } catch (Exception e) {
      }

      open("http://localhost:42000");
      $("body").shouldHave(text("event broker"));

      SelenideElement pre = $("pre");
      pre.shouldHave(text("http://localhost:42001/apply"));
      LinkedHashMap<String, Object> modelMap;

      // workflow working smoothly

      // create ProductStoredEvent: product stored 12:00
      ProductStoredEvent e1200 = new ProductStoredEvent();
      e1200.setId("12:00");
      e1200.setBox("box23");
      e1200.setProduct("shoes");
      e1200.setPlace("shelf23");
      publish(e1200);

      open("http://localhost:42000");
      pre = $("#history");
      pre.shouldHave(text("- 12_00:"));

      // page 12:50
      open("http://localhost:42001/page/12_50");
      $("#shoes").click();

      open("http://localhost:42000");
      pre = $("#history");
      pre.shouldHave(text("- 12_51:"));

      // page 12:55
      open("http://localhost:42001/page/12_55");
      $("#name").setValue("Alice");
      $("#address").setValue("Wonderland 1");
      $("#ok").click();

      open("http://localhost:42000");
      pre = $("#history");
      pre.shouldHave(text("- 13_00:"));

      // page 13:07
      open("http://localhost:42001/page/13_07");
      $("#tshirt").click();

      open("http://localhost:42000");
      pre = $("#history");
      pre.shouldHave(text("- 13_10:"));
      try {
         Thread.sleep(3000);
      } catch (Exception e) {
      }
      eventBroker.stop();
      shop.stop();

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
