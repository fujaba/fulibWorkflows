package uks.debuggen.microshop;
import static com.codeborne.selenide.Condition.matchText;
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
import uks.debuggen.microshop.Warehouse.WarehouseService;
import uks.debuggen.microshop.events.DataEvent;
import uks.debuggen.microshop.events.Event;
import uks.debuggen.microshop.events.EventBroker;
import uks.debuggen.microshop.events.StoreProductCommand;
import com.mashape.unirest.http.exceptions.UnirestException;
import uks.debuggen.microshop.events.*;

public class TestMicroShop
{
   public static final String PROPERTY_EVENT_BROKER = "eventBroker";
   private EventBroker eventBroker;
   protected PropertyChangeSupport listeners;

   public EventBroker getEventBroker()
   {
      return this.eventBroker;
   }

   public TestMicroShop setEventBroker(EventBroker value)
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
      Configuration.timeout = Constants.TIME_OUT * 10;
      Configuration.pageLoadTimeout = Configuration.timeout;
      Configuration.browserPosition = "-3500x10"; // Constants.BROWSER_POS;
      // Configuration.headless = Constants.HEADLESS;
   }

   @Test
   public void MicroShop()
   {
      // start the event broker
      eventBroker = new EventBroker();
      eventBroker.start();

      // start service
      WarehouseService warehouse = new WarehouseService();
      warehouse.start();
      try {
         Thread.sleep(1500);
      } catch (Exception e) {
      }

      open("http://localhost:42000");
      $("body").shouldHave(text("event broker"));

      SelenideElement pre = $("pre");
      pre.shouldHave(text("http://localhost:42001/apply"));
      LinkedHashMap<String, Object> modelMap;

      // workflow SmoothCase

      // page 12:01
      open("http://localhost:42001/page/12_01");

      // page 12:02
      open("http://localhost:42001/page/12_02");
      $("#barcode").setValue("b001");
      $("#type").setValue("red shoes");
      $("#location").setValue("shelf 42");
      $("#ok").click();

      open("http://localhost:42000");
      pre = $("#history");
      pre.shouldHave(text("- 12_02_01:"));

      // check Warehouse
      open("http://localhost:42001");
      pre = $("#history");
      pre.shouldHave(text("- 12_02_01:"));
      for (DataEvent dataEvent : warehouse.getBuilder().getEventStore().values()) {
         warehouse.getBuilder().load(dataEvent.getBlockId());
      }
      modelMap = warehouse.getBuilder().getModel().getModelMap();
      if (modelMap.values().size() > 0) {
         org.fulib.FulibTools.objectDiagrams().dumpSVG("tmp/warehouse12_02_01.svg", modelMap.values());
      }

      open("http://localhost:42001");
      // check data note 12:02:02
      pre = $("#data");
      pre.shouldHave(text("- b001:"));
      pre.shouldHave(matchText("barcode:.*b001"));
      pre.shouldHave(matchText("content:.*\"red shoes\""));
      pre.shouldHave(matchText("location:.*\"shelf 42\""));

      // page 12:04
      open("http://localhost:42001/page/12_04");

      // page 12:05
      open("http://localhost:42001/page/12_05");
      $("#barcode").setValue("b002");
      $("#type").setValue("red shoes");
      $("#location").setValue("shelf 23");
      $("#ok").click();

      open("http://localhost:42000");
      pre = $("#history");
      pre.shouldHave(text("- 12_05_01:"));

      // check Warehouse
      open("http://localhost:42001");
      pre = $("#history");
      pre.shouldHave(text("- 12_05_01:"));
      for (DataEvent dataEvent : warehouse.getBuilder().getEventStore().values()) {
         warehouse.getBuilder().load(dataEvent.getBlockId());
      }
      modelMap = warehouse.getBuilder().getModel().getModelMap();
      if (modelMap.values().size() > 0) {
         org.fulib.FulibTools.objectDiagrams().dumpSVG("tmp/warehouse12_05_01.svg", modelMap.values());
      }

      open("http://localhost:42001");
      // check data note 12:05:02
      pre = $("#data");
      pre.shouldHave(text("- b002:"));
      pre.shouldHave(matchText("barcode:.*b002"));
      pre.shouldHave(matchText("content:.*\"red shoes\""));
      pre.shouldHave(matchText("location:.*\"shelf 23\""));

      // page 12:07
      open("http://localhost:42001/page/12_07");

      // page 12:08
      open("http://localhost:42001/page/12_08");
      $("#barcode").setValue("b003");
      $("#type").setValue("blue jeans");
      $("#location").setValue("shelf 1337");
      $("#ok").click();

      open("http://localhost:42000");
      pre = $("#history");
      pre.shouldHave(text("- 12_08_01:"));

      // check Warehouse
      open("http://localhost:42001");
      pre = $("#history");
      pre.shouldHave(text("- 12_08_01:"));
      for (DataEvent dataEvent : warehouse.getBuilder().getEventStore().values()) {
         warehouse.getBuilder().load(dataEvent.getBlockId());
      }
      modelMap = warehouse.getBuilder().getModel().getModelMap();
      if (modelMap.values().size() > 0) {
         org.fulib.FulibTools.objectDiagrams().dumpSVG("tmp/warehouse12_08_01.svg", modelMap.values());
      }

      open("http://localhost:42001");
      // check data note 12:08:02
      pre = $("#data");
      pre.shouldHave(text("- b003:"));
      pre.shouldHave(matchText("barcode:.*b003"));
      pre.shouldHave(matchText("content:.*\"blue jeans\""));
      pre.shouldHave(matchText("location:.*\"shelf 1337\""));

      // page 12:10
      open("http://localhost:42001/page/12_10");

      // create ProductOfferedEvent: product offered
      ProductOfferedEvent e1211 = new ProductOfferedEvent();
      e1211.setId("12:11");
      publish(e1211);

      open("http://localhost:42000");
      pre = $("#history");
      pre.shouldHave(text("- 12_11:"));

      // create ProductOrderedEvent: product ordered
      ProductOrderedEvent e1212 = new ProductOrderedEvent();
      e1212.setId("12:12");
      publish(e1212);

      open("http://localhost:42000");
      pre = $("#history");
      pre.shouldHave(text("- 12_12:"));

      // create OrderPickedEvent: order picked
      OrderPickedEvent e1213 = new OrderPickedEvent();
      e1213.setId("12:13");
      publish(e1213);

      open("http://localhost:42000");
      pre = $("#history");
      pre.shouldHave(text("- 12_13:"));

      // create OrderDeliveredEvent: order delivered
      OrderDeliveredEvent e1214 = new OrderDeliveredEvent();
      e1214.setId("12:14");
      publish(e1214);

      open("http://localhost:42000");
      pre = $("#history");
      pre.shouldHave(text("- 12_14:"));

      // workflow OutOfStock

      // create ProductOrderedEvent: product ordered
      ProductOrderedEvent e1301 = new ProductOrderedEvent();
      e1301.setId("13:01");
      publish(e1301);

      open("http://localhost:42000");
      pre = $("#history");
      pre.shouldHave(text("- 13_01:"));

      // create OrderRejectedEvent: order rejected
      OrderRejectedEvent e1302 = new OrderRejectedEvent();
      e1302.setId("13:02");
      publish(e1302);

      open("http://localhost:42000");
      pre = $("#history");
      pre.shouldHave(text("- 13_02:"));
      try {
         Thread.sleep(3000);
      } catch (Exception e) {
      }
      eventBroker.stop();
      warehouse.stop();

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
