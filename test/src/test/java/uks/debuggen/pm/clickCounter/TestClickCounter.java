package uks.debuggen.pm.clickCounter;
import java.util.LinkedHashMap;
import org.fulib.FulibTools;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.JUnitCore;

import static org.assertj.core.api.Assertions.assertThat;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.SelenideElement;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.fulib.yaml.Yaml;
import org.fulib.yaml.YamlIdMap;
import spark.Request;
import spark.Response;
import spark.Service;
import uks.debuggen.Constants;
import uks.debuggen.pm.clickCounter.ClickCounter.ClickCounterService;
import uks.debuggen.pm.clickCounter.events.*;
import static com.codeborne.selenide.Condition.matchText;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import java.util.Objects;
import java.beans.PropertyChangeSupport;

public class TestClickCounter
{

   public static void main(String[] args) {
      JUnitCore.main(TestClickCounter.class.getName());
   }

   @Before
   public void setTimeOut() {
      Configuration.timeout = Constants.TIME_OUT * 20;
      Configuration.pageLoadTimeout = Configuration.timeout;
      Configuration.browserPosition = Constants.BROWSER_POS; // "-1300x600"; //
      Configuration.browserSize="400x500";
      // Configuration.headless = Constants.HEADLESS;
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
      spark.init();
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

   @Test
   public void ClickCounter()
   {
      // start the event broker
      eventBroker = new EventBroker();
      eventBroker.start();

      this.start();
      waitForEvent("" + port);

      // start service
      ClickCounterService clickCounter = new ClickCounterService();
      clickCounter.start();
      waitForEvent("42001");
      SelenideElement pre;
      LinkedHashMap<String, Object> modelMap;

      // workflow working smoothly

      // page 12:01
      open("http://localhost:42001/page/12_01");
      $("#Click_Me").click();
      waitForEvent("12:01:01");

      // check ClickCounter
      open("http://localhost:42001");
      for (DataEvent dataEvent : clickCounter.getBuilder().getEventStore().values()) {
         clickCounter.getBuilder().load(dataEvent.getBlockId());
      }
      modelMap = clickCounter.getBuilder().getModel().getModelMap();
      if (modelMap.values().size() > 0) {
         org.fulib.FulibTools.objectDiagrams().dumpSVG("../event-models/ClickCounter/clickCounter12_01_01.svg", modelMap.values());
      }

      open("http://localhost:42001");
      // check data note 12:01:02
      CounterBuilt e12_01_02 = (CounterBuilt) waitForEvent("12:01:02");
      assertThat(e12_01_02.getCount()).isEqualTo("1");

      // page 12:02
      open("http://localhost:42001/page/12_02");
      $("#Click_Me").click();
      waitForEvent("12:02:01");

      // check ClickCounter
      open("http://localhost:42001");
      for (DataEvent dataEvent : clickCounter.getBuilder().getEventStore().values()) {
         clickCounter.getBuilder().load(dataEvent.getBlockId());
      }
      modelMap = clickCounter.getBuilder().getModel().getModelMap();
      if (modelMap.values().size() > 0) {
         org.fulib.FulibTools.objectDiagrams().dumpSVG("../event-models/ClickCounter/clickCounter12_02_01.svg", modelMap.values());
      }

      open("http://localhost:42001");
      // check data note 12:02:02
      CounterBuilt e12_02_02 = (CounterBuilt) waitForEvent("12:02:02");
      assertThat(e12_02_02.getCount()).isEqualTo("2");

      // page 12:03
      open("http://localhost:42001/page/12_03");
      $("#Click_Me").click();
      waitForEvent("12:03:01");

      // check ClickCounter
      open("http://localhost:42001");
      for (DataEvent dataEvent : clickCounter.getBuilder().getEventStore().values()) {
         clickCounter.getBuilder().load(dataEvent.getBlockId());
      }
      modelMap = clickCounter.getBuilder().getModel().getModelMap();
      if (modelMap.values().size() > 0) {
         org.fulib.FulibTools.objectDiagrams().dumpSVG("../event-models/ClickCounter/clickCounter12_03_01.svg", modelMap.values());
      }

      open("http://localhost:42001");
      // check data note 12:03:02
      CounterBuilt e12_03_02 = (CounterBuilt) waitForEvent("12:03:02");
      assertThat(e12_03_02.getCount()).isEqualTo("3");

      // page 12:04
      open("http://localhost:42001/page/12_04");
      try {
         Thread.sleep(3000);
      } catch (Exception e) {
      }
      eventBroker.stop();
      spark.stop();
      clickCounter.stop();

      System.err.println("ClickCounter completed good and gracefully");
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
   public static final String PROPERTY_EVENT_BROKER = "eventBroker";
   public static final String PROPERTY_SPARK = "spark";
   public static final String PROPERTY_EVENT_QUEUE = "eventQueue";
   public static final String PROPERTY_HISTORY = "history";
   public static final String PROPERTY_PORT = "port";
   private EventBroker eventBroker;
   private Service spark;
   private LinkedBlockingQueue<Event> eventQueue;
   private LinkedHashMap<String, Event> history;
   private int port;
   protected PropertyChangeSupport listeners;

   public EventBroker getEventBroker()
   {
      return this.eventBroker;
   }

   public TestClickCounter setEventBroker(EventBroker value)
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

   public TestClickCounter setSpark(Service value)
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

   public TestClickCounter setEventQueue(LinkedBlockingQueue<Event> value)
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

   public TestClickCounter setHistory(LinkedHashMap<String, Event> value)
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

   public TestClickCounter setPort(int value)
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
}
