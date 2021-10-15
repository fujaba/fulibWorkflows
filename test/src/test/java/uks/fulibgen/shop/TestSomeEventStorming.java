package uks.fulibgen.shop;
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
import uks.fulibgen.shop.Shop.ShopService;
import uks.fulibgen.shop.Storage.StorageService;
import uks.fulibgen.shop.events.*;
import com.mashape.unirest.http.exceptions.UnirestException;
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

public class TestSomeEventStorming
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

   public Service getSpark()
   {
      return this.spark;
   }

   public TestSomeEventStorming setSpark(Service value)
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

   public TestSomeEventStorming setEventQueue(LinkedBlockingQueue<Event> value)
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

   public TestSomeEventStorming setHistory(LinkedHashMap<String, Event> value)
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

   public TestSomeEventStorming setPort(int value)
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
   public void SomeEventStorming()
   {
      // start the event broker
      eventBroker = new EventBroker();
      eventBroker.start();

      this.start();
      waitForEvent("" + port);

      // start service
      ShopService shop = new ShopService();
      shop.start();
      waitForEvent("42100");

      // start service
      StorageService storage = new StorageService();
      storage.start();
      waitForEvent("42002");
      SelenideElement pre;
      LinkedHashMap<String, Object> modelMap;

      // workflow OrderingSmooth

      // create StoreBoxCommand: store box 12:00
      StoreBoxCommand e1200 = new StoreBoxCommand();
      e1200.setId("12:00");
      e1200.setBox("box23");
      e1200.setProduct("shoes");
      e1200.setPlace("shelf23");
      publish(e1200);
      waitForEvent("12:00");

      // check Storage
      open("http://localhost:42002");
      for (DataEvent dataEvent : storage.getBuilder().getEventStore().values()) {
         storage.getBuilder().load(dataEvent.getBlockId());
      }
      modelMap = storage.getBuilder().getModel().getModelMap();
      if (modelMap.values().size() > 0) {
         org.fulib.FulibTools.objectDiagrams().dumpSVG("tmp/storage12_00.svg", modelMap.values());
      }

      open("http://localhost:42002");
      // check data note 12:01
      BoxBuilt e12_01 = (BoxBuilt) waitForEvent("12:01");
      assertThat(e12_01.getProduct()).isEqualTo("shoes");
      assertThat(e12_01.getPlace()).isEqualTo("shelf23");

      // page 12:50
      open("http://localhost:42100/page/12_50");
      $("#shoes").click();
      waitForEvent("12:51");

      // page 13:00
      open("http://localhost:42100/page/13_00");
      $("#product").setValue("shoes");
      $("#name").setValue("Alice");
      $("#address").setValue("Wonderland 1");
      $("#OK").click();
      waitForEvent("13:01");

      // check Shop
      open("http://localhost:42100");
      for (DataEvent dataEvent : shop.getBuilder().getEventStore().values()) {
         shop.getBuilder().load(dataEvent.getBlockId());
      }
      modelMap = shop.getBuilder().getModel().getModelMap();
      if (modelMap.values().size() > 0) {
         org.fulib.FulibTools.objectDiagrams().dumpSVG("tmp/shop13_01.svg", modelMap.values());
      }

      open("http://localhost:42100");
      // check data note 13:06:01
      OrderBuilt e13_06_01 = (OrderBuilt) waitForEvent("13:06:01");
      assertThat(e13_06_01.getState()).isEqualTo("picking");

      // check Storage
      open("http://localhost:42002");
      for (DataEvent dataEvent : storage.getBuilder().getEventStore().values()) {
         storage.getBuilder().load(dataEvent.getBlockId());
      }
      modelMap = storage.getBuilder().getModel().getModelMap();
      if (modelMap.values().size() > 0) {
         org.fulib.FulibTools.objectDiagrams().dumpSVG("tmp/storage13_01.svg", modelMap.values());
      }

      open("http://localhost:42002");
      // check data note 13:05
      PickTaskBuilt e13_05 = (PickTaskBuilt) waitForEvent("13:05");
      assertThat(e13_05.getOrder()).isEqualTo("order1300");
      assertThat(e13_05.getProduct()).isEqualTo("shoes");
      assertThat(e13_05.getCustomer()).isEqualTo("Alice");
      assertThat(e13_05.getAddress()).isEqualTo("Wonderland 1");
      assertThat(e13_05.getState()).isEqualTo("todo");

      // create PickOrderCommand: pick order 14:00
      PickOrderCommand e1400 = new PickOrderCommand();
      e1400.setId("14:00");
      e1400.setPickTask("pick1300");
      e1400.setBox("box23");
      e1400.setUser("Bob");
      publish(e1400);
      waitForEvent("14:00");

      // check Storage
      open("http://localhost:42002");
      for (DataEvent dataEvent : storage.getBuilder().getEventStore().values()) {
         storage.getBuilder().load(dataEvent.getBlockId());
      }
      modelMap = storage.getBuilder().getModel().getModelMap();
      if (modelMap.values().size() > 0) {
         org.fulib.FulibTools.objectDiagrams().dumpSVG("tmp/storage14_00.svg", modelMap.values());
      }

      open("http://localhost:42002");
      // check data note 14:01
      PickTaskBuilt e14_01 = (PickTaskBuilt) waitForEvent("14:01");
      assertThat(e14_01.getState()).isEqualTo("done");
      assertThat(e14_01.getBox()).isEqualTo("box23");
      // check data note 14:02
      BoxBuilt e14_02 = (BoxBuilt) waitForEvent("14:02");
      assertThat(e14_02.getPlace()).isEqualTo("shipping");

      // check Shop
      open("http://localhost:42100");
      for (DataEvent dataEvent : shop.getBuilder().getEventStore().values()) {
         shop.getBuilder().load(dataEvent.getBlockId());
      }
      modelMap = shop.getBuilder().getModel().getModelMap();
      if (modelMap.values().size() > 0) {
         org.fulib.FulibTools.objectDiagrams().dumpSVG("tmp/shop14_00.svg", modelMap.values());
      }

      open("http://localhost:42100");
      // check data note 14:04
      OrderBuilt e14_04 = (OrderBuilt) waitForEvent("14:04");
      assertThat(e14_04.getState()).isEqualTo("shipping");

      // workflow OrderOutOfStocks

      // create SubmitOrderCommand: submit order 13:11
      SubmitOrderCommand e1311 = new SubmitOrderCommand();
      e1311.setId("13:11");
      e1311.setTrigger("button OK");
      e1311.setProduct("tshirt");
      e1311.setCustomer("Alice");
      e1311.setAddress("Wonderland 1");
      publish(e1311);
      waitForEvent("13:11");

      // check Shop
      open("http://localhost:42100");
      for (DataEvent dataEvent : shop.getBuilder().getEventStore().values()) {
         shop.getBuilder().load(dataEvent.getBlockId());
      }
      modelMap = shop.getBuilder().getModel().getModelMap();
      if (modelMap.values().size() > 0) {
         org.fulib.FulibTools.objectDiagrams().dumpSVG("tmp/shop13_11.svg", modelMap.values());
      }

      open("http://localhost:42100");
      // check data note 13:16
      OrderBuilt e13_16 = (OrderBuilt) waitForEvent("13:16");
      assertThat(e13_16.getState()).isEqualTo("out of stock");

      // check Storage
      open("http://localhost:42002");
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
      spark.stop();
      shop.stop();
      storage.stop();

      System.err.println("SomeEventStorming completed good and gracefully");
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
