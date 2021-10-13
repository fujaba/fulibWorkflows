package uks.debuggen.microshop;

import static com.codeborne.selenide.Condition.matchText;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

import java.beans.PropertyChangeSupport;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.SelenideElement;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import static org.assertj.core.api.Assertions.assertThat;

import org.fulib.yaml.Yaml;
import org.fulib.yaml.YamlIdMap;
import org.junit.Before;
import org.junit.Test;

import spark.Request;
import spark.Response;
import spark.Service;
import uks.debuggen.Constants;
import uks.debuggen.microshop.MicroShop.MicroShopService;
import uks.debuggen.microshop.Warehouse.WarehouseService;
import uks.debuggen.microshop.events.*;
import com.mashape.unirest.http.exceptions.UnirestException;

public class TestMicroShop {
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

   public Service getSpark()
   {
      return this.spark;
   }

   public TestMicroShop setSpark(Service value)
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

   public TestMicroShop setEventQueue(LinkedBlockingQueue<Event> value)
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

   public TestMicroShop setHistory(LinkedHashMap<String, Event> value)
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

   public TestMicroShop setPort(int value)
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

   public void start()
   {
      eventQueue = new LinkedBlockingQueue<Event>();
      history  = new LinkedHashMap<>();
      port = 41999;
      ExecutorService executor = Executors.newSingleThreadExecutor();
      spark = Service.ignite();
      spark.port(port);
      spark.post("/apply", (req, res) -> executor.submit(() -> this.postApply(req, res)).get());
      executor.submit(() -> System.out.println("test executor works"));
      executor.submit(this::subscribeAndLoadOldEvents);
      executor.submit(() -> System.out.println("test executor has done subscribeAndLoadOldEvents"));
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

         System.out.println("Test got event " + e.getId());
         history.put(e.getId(), e);
      }
   }

   @Before
   public void setTimeOut() {
      Configuration.timeout = Constants.TIME_OUT;
      Configuration.pageLoadTimeout = Configuration.timeout;
      Configuration.browserPosition = "-1300x600"; // Constants.BROWSER_POS;
      Configuration.browserSize="400x500";
      // Configuration.headless = Constants.HEADLESS;
   }

   @Test
   public void MicroShop()
   {
      // start the event broker
      eventBroker = new EventBroker();
      eventBroker.start();

      this.start();
      waitForEvent("" + port);

      // start service
      WarehouseService warehouse = new WarehouseService();
      warehouse.start();
      waitForEvent("42001");

      // start service
      MicroShopService microShop = new MicroShopService();
      microShop.start();
      waitForEvent("42002");
      SelenideElement pre;
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
      waitForEvent("12:02:01");

      // check Warehouse
      open("http://localhost:42001");
      for (DataEvent dataEvent : warehouse.getBuilder().getEventStore().values()) {
         warehouse.getBuilder().load(dataEvent.getBlockId());
      }
      modelMap = warehouse.getBuilder().getModel().getModelMap();
      if (modelMap.values().size() > 0) {
         org.fulib.FulibTools.objectDiagrams().dumpSVG("tmp/warehouse12_02_01.svg", modelMap.values());
      }

      open("http://localhost:42001");
      // check data note 12:02:02
      BoxBuilt e12_02_02 = (BoxBuilt) waitForEvent("12:02:02");
      assertThat(e12_02_02.getBarcode()).isEqualTo("b001");
      assertThat(e12_02_02.getContent()).isEqualTo("red shoes");
      assertThat(e12_02_02.getLocation()).isEqualTo("shelf 42");

      // check MicroShop
      open("http://localhost:42002");
      for (DataEvent dataEvent : microShop.getBuilder().getEventStore().values()) {
         microShop.getBuilder().load(dataEvent.getBlockId());
      }
      modelMap = microShop.getBuilder().getModel().getModelMap();
      if (modelMap.values().size() > 0) {
         org.fulib.FulibTools.objectDiagrams().dumpSVG("tmp/microShop12_02_01.svg", modelMap.values());
      }

      open("http://localhost:42002");
      // check data note 12:03:01
      ProductBuilt e12_03_01 = (ProductBuilt) waitForEvent("12:03:01");
      assertThat(e12_03_01.getName()).isEqualTo("red shoes");
      assertThat(e12_03_01.getState()).isEqualTo("in stock");

      // page 12:04
      open("http://localhost:42001/page/12_04");

      // page 12:05
      open("http://localhost:42001/page/12_05");
      $("#barcode").setValue("b002");
      $("#type").setValue("red shoes");
      $("#location").setValue("shelf 23");
      $("#ok").click();
      waitForEvent("12:05:01");

      // check Warehouse
      open("http://localhost:42001");
      for (DataEvent dataEvent : warehouse.getBuilder().getEventStore().values()) {
         warehouse.getBuilder().load(dataEvent.getBlockId());
      }
      modelMap = warehouse.getBuilder().getModel().getModelMap();
      if (modelMap.values().size() > 0) {
         org.fulib.FulibTools.objectDiagrams().dumpSVG("tmp/warehouse12_05_01.svg", modelMap.values());
      }

      open("http://localhost:42001");
      // check data note 12:05:02
      BoxBuilt e12_05_02 = (BoxBuilt) waitForEvent("12:05:02");
      assertThat(e12_05_02.getBarcode()).isEqualTo("b002");
      assertThat(e12_05_02.getContent()).isEqualTo("red shoes");
      assertThat(e12_05_02.getLocation()).isEqualTo("shelf 23");

      // check MicroShop
      open("http://localhost:42002");
      for (DataEvent dataEvent : microShop.getBuilder().getEventStore().values()) {
         microShop.getBuilder().load(dataEvent.getBlockId());
      }
      modelMap = microShop.getBuilder().getModel().getModelMap();
      if (modelMap.values().size() > 0) {
         org.fulib.FulibTools.objectDiagrams().dumpSVG("tmp/microShop12_05_01.svg", modelMap.values());
      }

      open("http://localhost:42002");
      // check data note 12:06:01
      ProductBuilt e12_06_01 = (ProductBuilt) waitForEvent("12:06:01");
      assertThat(e12_06_01.getName()).isEqualTo("red shoes");
      assertThat(e12_06_01.getState()).isEqualTo("in stock");

      // page 12:07
      open("http://localhost:42001/page/12_07");

      // page 12:08
      open("http://localhost:42001/page/12_08");
      $("#barcode").setValue("b003");
      $("#type").setValue("blue jeans");
      $("#location").setValue("shelf 1337");
      $("#ok").click();
      waitForEvent("12:08:01");

      // check Warehouse
      open("http://localhost:42001");
      for (DataEvent dataEvent : warehouse.getBuilder().getEventStore().values()) {
         warehouse.getBuilder().load(dataEvent.getBlockId());
      }
      modelMap = warehouse.getBuilder().getModel().getModelMap();
      if (modelMap.values().size() > 0) {
         org.fulib.FulibTools.objectDiagrams().dumpSVG("tmp/warehouse12_08_01.svg", modelMap.values());
      }

      open("http://localhost:42001");
      // check data note 12:08:02
      BoxBuilt e12_08_02 = (BoxBuilt) waitForEvent("12:08:02");
      assertThat(e12_08_02.getBarcode()).isEqualTo("b003");
      assertThat(e12_08_02.getContent()).isEqualTo("blue jeans");
      assertThat(e12_08_02.getLocation()).isEqualTo("shelf 1337");

      // check MicroShop
      open("http://localhost:42002");
      for (DataEvent dataEvent : microShop.getBuilder().getEventStore().values()) {
         microShop.getBuilder().load(dataEvent.getBlockId());
      }
      modelMap = microShop.getBuilder().getModel().getModelMap();
      if (modelMap.values().size() > 0) {
         org.fulib.FulibTools.objectDiagrams().dumpSVG("tmp/microShop12_08_01.svg", modelMap.values());
      }

      open("http://localhost:42002");
      // check data note 12:09:01
      ProductBuilt e12_09_01 = (ProductBuilt) waitForEvent("12:09:01");
      assertThat(e12_09_01.getName()).isEqualTo("blue jeans");
      assertThat(e12_09_01.getState()).isEqualTo("in stock");

      // page 12:10
      open("http://localhost:42001/page/12_10");

      // page 12:11
      open("http://localhost:42002/page/12_11");

      // page 12:12
      open("http://localhost:42002/page/12_12");
      $("#product").setValue("red shoes");
      $("#price").setValue("$42");
      $("#ok").click();
      waitForEvent("12:14:42");

      // check MicroShop
      open("http://localhost:42002");
      for (DataEvent dataEvent : microShop.getBuilder().getEventStore().values()) {
         microShop.getBuilder().load(dataEvent.getBlockId());
      }
      modelMap = microShop.getBuilder().getModel().getModelMap();
      if (modelMap.values().size() > 0) {
         org.fulib.FulibTools.objectDiagrams().dumpSVG("tmp/microShop12_14_42.svg", modelMap.values());
      }

      open("http://localhost:42002");
      // check data note 12:14:43
      ProductBuilt e12_14_43 = (ProductBuilt) waitForEvent("12:14:43");
      assertThat(e12_14_43.getName()).isEqualTo("red shoes");
      assertThat(e12_14_43.getPrice()).isEqualTo("$42");

      // page 12:16
      open("http://localhost:42002/page/12_16");

      // page 12:17
      open("http://localhost:42002/page/12_17");
      $("#product").setValue("blue jeans");
      $("#price").setValue("$63");
      $("#ok").click();
      waitForEvent("12:17:01");

      // check MicroShop
      open("http://localhost:42002");
      for (DataEvent dataEvent : microShop.getBuilder().getEventStore().values()) {
         microShop.getBuilder().load(dataEvent.getBlockId());
      }
      modelMap = microShop.getBuilder().getModel().getModelMap();
      if (modelMap.values().size() > 0) {
         org.fulib.FulibTools.objectDiagrams().dumpSVG("tmp/microShop12_17_01.svg", modelMap.values());
      }

      open("http://localhost:42002");
      // check data note 12:17:02
      ProductBuilt e12_17_02 = (ProductBuilt) waitForEvent("12:17:02");
      assertThat(e12_17_02.getName()).isEqualTo("blue jeans");
      assertThat(e12_17_02.getPrice()).isEqualTo("$63");

      // page 12:19
      open("http://localhost:42002/page/12_19");

      // page 12:20
      open("http://localhost:42002/page/12_20");

      // page 12:21
      open("http://localhost:42002/page/12_21");
      $("#product").setValue("red shoes");
      $("#customer").setValue("Carli Customer");
      $("#address").setValue("Wonderland 1");
      $("#buy").click();
      waitForEvent("12:21:01");

      // check MicroShop
      open("http://localhost:42002");
      for (DataEvent dataEvent : microShop.getBuilder().getEventStore().values()) {
         microShop.getBuilder().load(dataEvent.getBlockId());
      }
      modelMap = microShop.getBuilder().getModel().getModelMap();
      if (modelMap.values().size() > 0) {
         org.fulib.FulibTools.objectDiagrams().dumpSVG("tmp/microShop12_21_01.svg", modelMap.values());
      }

      open("http://localhost:42002");
      // check data note 12:23:01
      OrderBuilt e12_23_01 = (OrderBuilt) waitForEvent("12:23:01");
      assertThat(e12_23_01.getCode()).isEqualTo("o0925_1");
      assertThat(e12_23_01.getState()).isEqualTo("picking");

      // check Warehouse
      open("http://localhost:42001");
      for (DataEvent dataEvent : warehouse.getBuilder().getEventStore().values()) {
         warehouse.getBuilder().load(dataEvent.getBlockId());
      }
      modelMap = warehouse.getBuilder().getModel().getModelMap();
      if (modelMap.values().size() > 0) {
         org.fulib.FulibTools.objectDiagrams().dumpSVG("tmp/warehouse12_21_01.svg", modelMap.values());
      }

      open("http://localhost:42001");
      // check data note 12:22:01
      PickTaskBuilt e12_22_01 = (PickTaskBuilt) waitForEvent("12:22:01");
      assertThat(e12_22_01.getCode()).isEqualTo("pt_o0925_1");
      assertThat(e12_22_01.getProduct()).isEqualTo("red shoes");
      assertThat(e12_22_01.getShelf()).isEqualTo("[shelf 42, shelf 23]");
      assertThat(e12_22_01.getCustomer()).isEqualTo("Carli Customer");
      assertThat(e12_22_01.getAddress()).isEqualTo("Wonderland 1");
      assertThat(e12_22_01.getState()).isEqualTo("picking");

      // page 12:24
      open("http://localhost:42002/page/12_24");

      // page 12:25
      open("http://localhost:42001/page/12_25");

      // page 12:26
      open("http://localhost:42001/page/12_26");
      $("#task").setValue("pt_o0925_1");
      $("#shelf").setValue("shelf 42");
      $("#done").click();
      waitForEvent("12:26:01");

      // check Warehouse
      open("http://localhost:42001");
      for (DataEvent dataEvent : warehouse.getBuilder().getEventStore().values()) {
         warehouse.getBuilder().load(dataEvent.getBlockId());
      }
      modelMap = warehouse.getBuilder().getModel().getModelMap();
      if (modelMap.values().size() > 0) {
         org.fulib.FulibTools.objectDiagrams().dumpSVG("tmp/warehouse12_26_01.svg", modelMap.values());
      }

      open("http://localhost:42001");
      // check data note 12:26:02
      PickTaskBuilt e12_26_02 = (PickTaskBuilt) waitForEvent("12:26:02");
      assertThat(e12_26_02.getCode()).isEqualTo("pt_o0925_1");
      assertThat(e12_26_02.getFrom()).isEqualTo("shelf 42");
      assertThat(e12_26_02.getState()).isEqualTo("shipping");

      // check MicroShop
      open("http://localhost:42002");
      for (DataEvent dataEvent : microShop.getBuilder().getEventStore().values()) {
         microShop.getBuilder().load(dataEvent.getBlockId());
      }
      modelMap = microShop.getBuilder().getModel().getModelMap();
      if (modelMap.values().size() > 0) {
         org.fulib.FulibTools.objectDiagrams().dumpSVG("tmp/microShop12_26_01.svg", modelMap.values());
      }

      open("http://localhost:42002");
      // check data note 12:27:01
      OrderBuilt e12_27_01 = (OrderBuilt) waitForEvent("12:27:01");
      assertThat(e12_27_01.getCode()).isEqualTo("o0925_1");
      assertThat(e12_27_01.getState()).isEqualTo("shipping");

      // page 12:28
      open("http://localhost:42001/page/12_28");

      // page 12:29
      open("http://localhost:42001/page/12_29");

      // page 12:30
      open("http://localhost:42001/page/12_30");
      $("#order").setValue("o0925_1");
      $("#done").click();
      waitForEvent("12:30:01");

      // check MicroShop
      open("http://localhost:42002");
      for (DataEvent dataEvent : microShop.getBuilder().getEventStore().values()) {
         microShop.getBuilder().load(dataEvent.getBlockId());
      }
      modelMap = microShop.getBuilder().getModel().getModelMap();
      if (modelMap.values().size() > 0) {
         org.fulib.FulibTools.objectDiagrams().dumpSVG("tmp/microShop12_30_01.svg", modelMap.values());
      }

      open("http://localhost:42002");
      // check data note 12:30:02
      OrderBuilt e12_30_02 = (OrderBuilt) waitForEvent("12:30:02");
      assertThat(e12_30_02.getCode()).isEqualTo("o0925_1");
      assertThat(e12_30_02.getState()).isEqualTo("delivered");

      // page 12:32
      open("http://localhost:42001/page/12_32");

      // workflow OutOfStock

      // create ProductOrderedEvent: product ordered
      ProductOrderedEvent e1301 = new ProductOrderedEvent();
      e1301.setId("13:01");
      publish(e1301);
      waitForEvent("13:01");

      // create OrderRejectedEvent: order rejected
      OrderRejectedEvent e1302 = new OrderRejectedEvent();
      e1302.setId("13:02");
      publish(e1302);
      waitForEvent("13:02");
      try {
         Thread.sleep(3000);
      } catch (Exception e) {
      }
      eventBroker.stop();
      spark.stop();
      warehouse.stop();
      microShop.stop();

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
