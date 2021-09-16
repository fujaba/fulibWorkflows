package uks.fulibgen.shop;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.fulib.yaml.Yaml;
import org.junit.Test;
import uks.fulibgen.shop.Shop.ShopService;
import uks.fulibgen.shop.Storage.StorageService;
import uks.fulibgen.shop.events.*;
import uks.fulibgen.shop.someservice.someserviceService;

import java.util.LinkedHashMap;
import java.util.Objects;
import java.beans.PropertyChangeSupport;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
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

   @Test
   public void SomeEventStorming()
   {
      // start the event broker
      eventBroker = new EventBroker();
      eventBroker.start();

      // start service
      ShopService shop = new ShopService();
      shop.start();

      // start service
      StorageService storage = new StorageService();
      storage.start();
      try {
         Thread.sleep(500);
      } catch (Exception e) {
      }

      open("http://localhost:42000");
      $("body").shouldHave(text("event broker"));

      SelenideElement pre = $("pre");
      pre.shouldHave(text("http://localhost:42100/apply"));
      pre.shouldHave(text("http://localhost:42002/apply"));
      LinkedHashMap<String, Object> modelMap;

      // workflow working smoothly

      // create StoreBoxCommand: store box 12:00
      StoreBoxCommand e1200 = new StoreBoxCommand();
      e1200.setId("12:00");
      e1200.setBox("box23");
      e1200.setProduct("shoes");
      e1200.setPlace("shelf23");
      publish(e1200);

      open("http://localhost:42000");
      pre = $("#history");
      pre.shouldHave(text("- 12_00:"));

      // check Storage
      open("http://localhost:42002");
      pre = $("#history");
      pre.shouldHave(text("- 12_00:"));
      for (DataEvent dataEvent : storage.getBuilder().getEventStore().values()) {
         storage.getBuilder().load(dataEvent.getBlockId());
      }
      modelMap = storage.getBuilder().getModel().getModelMap();
      if (modelMap.values().size() > 0) {
         org.fulib.FulibTools.objectDiagrams().dumpSVG("tmp/storage12_00.svg", modelMap.values());
      }

      open("http://localhost:42002");
      // check data note 12:01
      pre = $("#data");
      pre.shouldHave(text("- box23:"));
      pre.shouldHave(matchText("product:.*shoes"));
      pre.shouldHave(matchText("place:.*shelf23"));

      // page 12:50
      open("http://localhost:42100/page/12_50");
      $("#shoes").click();

      open("http://localhost:42000");
      pre = $("#history");
      pre.shouldHave(text("- 12_51:"));

      // page 13:00
      open("http://localhost:42100/page/13_00");
      $("#product").setValue("shoes");
      $("#name").setValue("Alice");
      $("#address").setValue("Wonderland 1");
      $("#OK").click();

      open("http://localhost:42000");
      pre = $("#history");
      pre.shouldHave(text("- 13_01:"));

      // check Shop
      open("http://localhost:42100");
      pre = $("#history");
      pre.shouldHave(text("- 13_01:"));
      for (DataEvent dataEvent : shop.getBuilder().getEventStore().values()) {
         shop.getBuilder().load(dataEvent.getBlockId());
      }
      modelMap = shop.getBuilder().getModel().getModelMap();
      if (modelMap.values().size() > 0) {
         org.fulib.FulibTools.objectDiagrams().dumpSVG("tmp/shop13_01.svg", modelMap.values());
      }

      open("http://localhost:42100");
      // check data note 13:06
      pre = $("#data");
      pre.shouldHave(text("- order1300:"));
      pre.shouldHave(matchText("state:.*picking"));

      // check Storage
      open("http://localhost:42002");
      pre = $("#history");
      pre.shouldHave(text("- 13_01:"));
      for (DataEvent dataEvent : storage.getBuilder().getEventStore().values()) {
         storage.getBuilder().load(dataEvent.getBlockId());
      }
      modelMap = storage.getBuilder().getModel().getModelMap();
      if (modelMap.values().size() > 0) {
         org.fulib.FulibTools.objectDiagrams().dumpSVG("tmp/storage13_01.svg", modelMap.values());
      }

      open("http://localhost:42002");
      // check data note 13:05
      pre = $("#data");
      pre.shouldHave(text("- pick1300:"));
      pre.shouldHave(matchText("order:.*order1300"));
      pre.shouldHave(matchText("product:.*shoes"));
      pre.shouldHave(matchText("customer:.*Alice"));
      pre.shouldHave(matchText("address:.*\"Wonderland 1\""));
      pre.shouldHave(matchText("state:.*todo"));

      // create PickOrderCommand: pick order 14:00
      PickOrderCommand e1400 = new PickOrderCommand();
      e1400.setId("14:00");
      e1400.setPickTask("pick1300");
      e1400.setBox("box23");
      e1400.setUser("Bob");
      publish(e1400);

      open("http://localhost:42000");
      pre = $("#history");
      pre.shouldHave(text("- 14_00:"));

      // check Storage
      open("http://localhost:42002");
      pre = $("#history");
      pre.shouldHave(text("- 14_00:"));
      for (DataEvent dataEvent : storage.getBuilder().getEventStore().values()) {
         storage.getBuilder().load(dataEvent.getBlockId());
      }
      modelMap = storage.getBuilder().getModel().getModelMap();
      if (modelMap.values().size() > 0) {
         org.fulib.FulibTools.objectDiagrams().dumpSVG("tmp/storage14_00.svg", modelMap.values());
      }

      open("http://localhost:42002");
      // check data note 14:01
      pre = $("#data");
      pre.shouldHave(text("- pick1300:"));
      pre.shouldHave(matchText("state:.*done"));
      pre.shouldHave(matchText("box:.*box23"));
      // check data note 14:02
      pre = $("#data");
      pre.shouldHave(text("- box23:"));
      pre.shouldHave(matchText("place:.*shipping"));

      // check Shop
      open("http://localhost:42100");
      pre = $("#history");
      pre.shouldHave(text("- 14_00:"));
      for (DataEvent dataEvent : shop.getBuilder().getEventStore().values()) {
         shop.getBuilder().load(dataEvent.getBlockId());
      }
      modelMap = shop.getBuilder().getModel().getModelMap();
      if (modelMap.values().size() > 0) {
         org.fulib.FulibTools.objectDiagrams().dumpSVG("tmp/shop14_00.svg", modelMap.values());
      }

      open("http://localhost:42100");
      // check data note 14:04
      pre = $("#data");
      pre.shouldHave(text("- order1300:"));
      pre.shouldHave(matchText("state:.*shipping"));

      // workflow OrderOutOfStocks

      // create SubmitOrderCommand: submit order 13:11
      SubmitOrderCommand e1311 = new SubmitOrderCommand();
      e1311.setId("13:11");
      e1311.setTrigger("button OK");
      e1311.setProduct("tshirt");
      e1311.setCustomer("Alice");
      e1311.setAddress("Wonderland 1");
      publish(e1311);

      open("http://localhost:42000");
      pre = $("#history");
      pre.shouldHave(text("- 13_11:"));

      // check Shop
      open("http://localhost:42100");
      pre = $("#history");
      pre.shouldHave(text("- 13_11:"));
      for (DataEvent dataEvent : shop.getBuilder().getEventStore().values()) {
         shop.getBuilder().load(dataEvent.getBlockId());
      }
      modelMap = shop.getBuilder().getModel().getModelMap();
      if (modelMap.values().size() > 0) {
         org.fulib.FulibTools.objectDiagrams().dumpSVG("tmp/shop13_11.svg", modelMap.values());
      }

      open("http://localhost:42100");
      // check data note 13:16
      pre = $("#data");
      pre.shouldHave(text("- order1310:"));
      pre.shouldHave(matchText("state:.*\"out of stock\""));

      // check Storage
      open("http://localhost:42002");
      pre = $("#history");
      pre.shouldHave(text("- 13_11:"));
      for (DataEvent dataEvent : storage.getBuilder().getEventStore().values()) {
         storage.getBuilder().load(dataEvent.getBlockId());
      }
      modelMap = storage.getBuilder().getModel().getModelMap();
      if (modelMap.values().size() > 0) {
         org.fulib.FulibTools.objectDiagrams().dumpSVG("tmp/storage13_11.svg", modelMap.values());
      }

      open("http://localhost:42002");
      try {
         Thread.sleep(3000);
      } catch (Exception e) {
      }
      eventBroker.stop();
      shop.stop();
      storage.stop();

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
