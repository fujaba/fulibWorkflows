package uks.debuggen.interconnect;
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
import uks.debuggen.interconnect.CityElectricsKassel.CityElectricsKasselService;
import uks.debuggen.interconnect.IonicKassel42.IonicKassel42Service;
import uks.debuggen.interconnect.events.CarConnectedEvent;
import uks.debuggen.interconnect.events.DataEvent;
import uks.debuggen.interconnect.events.Event;
import uks.debuggen.interconnect.events.EventBroker;
import com.mashape.unirest.http.exceptions.UnirestException;
import uks.debuggen.interconnect.events.*;
import static com.codeborne.selenide.Condition.matchText;
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

public class TestInterconnect
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

   public TestInterconnect setEventBroker(EventBroker value)
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

   public TestInterconnect setSpark(Service value)
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

   public TestInterconnect setEventQueue(LinkedBlockingQueue<Event> value)
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

   public TestInterconnect setHistory(LinkedHashMap<String, Event> value)
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

   public TestInterconnect setPort(int value)
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
   public void Interconnect()
   {
      // start the event broker
      eventBroker = new EventBroker();
      eventBroker.start();

      this.start();
      waitForEvent("" + port);

      // start service
      IonicKassel42Service ionicKassel42 = new IonicKassel42Service();
      ionicKassel42.start();
      waitForEvent("42001");

      // start service
      CityElectricsKasselService cityElectricsKassel = new CityElectricsKasselService();
      cityElectricsKassel.start();
      waitForEvent("42002");
      SelenideElement pre;
      LinkedHashMap<String, Object> modelMap;

      // workflow Overview

      // workflow UniKasselIonicCharger

      // create CarConnectedEvent: car connected 11:55
      CarConnectedEvent e1155 = new CarConnectedEvent();
      e1155.setId("11:55");
      publish(e1155);
      waitForEvent("11:55");

      // check IonicKassel42
      open("http://localhost:42001");
      for (DataEvent dataEvent : ionicKassel42.getBuilder().getEventStore().values()) {
         ionicKassel42.getBuilder().load(dataEvent.getBlockId());
      }
      modelMap = ionicKassel42.getBuilder().getModel().getModelMap();
      if (modelMap.values().size() > 0) {
         org.fulib.FulibTools.objectDiagrams().dumpSVG("tmp/ionicKassel4211_55.svg", modelMap.values());
      }

      open("http://localhost:42001");

      // check CityElectricsKassel
      open("http://localhost:42002");
      for (DataEvent dataEvent : cityElectricsKassel.getBuilder().getEventStore().values()) {
         cityElectricsKassel.getBuilder().load(dataEvent.getBlockId());
      }
      modelMap = cityElectricsKassel.getBuilder().getModel().getModelMap();
      if (modelMap.values().size() > 0) {
         org.fulib.FulibTools.objectDiagrams().dumpSVG("tmp/cityElectricsKassel11_55.svg", modelMap.values());
      }

      open("http://localhost:42002");
      try {
         Thread.sleep(3000);
      } catch (Exception e) {
      }
      eventBroker.stop();
      spark.stop();
      ionicKassel42.stop();
      cityElectricsKassel.stop();

      System.out.println("Interconnect completed gracefully");
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
}
