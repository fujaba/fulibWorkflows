package uks.debuggen.medical.marburgexpertsystem;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.SelenideElement;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.util.LinkedHashMap;
import org.fulib.yaml.Yaml;
import org.junit.Before;
import org.junit.Test;

import uks.debuggen.Constants;
import uks.debuggen.medical.marburgexpertsystem.MarburgHealthSystem.MarburgHealthSystemService;
import uks.debuggen.medical.marburgexpertsystem.events.*;
import static com.codeborne.selenide.Condition.matchText;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import java.util.Objects;
import java.beans.PropertyChangeSupport;
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

public class TestHealthExperts
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

   public TestHealthExperts setEventBroker(EventBroker value)
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

   public TestHealthExperts setSpark(Service value)
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

   public TestHealthExperts setEventQueue(LinkedBlockingQueue<Event> value)
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

   public TestHealthExperts setHistory(LinkedHashMap<String, Event> value)
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

   public TestHealthExperts setPort(int value)
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
   public void HealthExperts()
   {
      // start the event broker
      eventBroker = new EventBroker();
      eventBroker.start();

      try {
         Thread.sleep(2000);
      } catch (InterruptedException e1) {
         e1.printStackTrace();
      }

      this.start();
      waitForEvent("" + port);

      // start service
      MarburgHealthSystemService marburgHealthSystem = new MarburgHealthSystemService();
      marburgHealthSystem.start();
      waitForEvent("42001");
      SelenideElement pre;
      LinkedHashMap<String, Object> modelMap;

      // workflow MarburgExpertSystem

      // create LoadDiseasesCommand: load diseases 12:00
      LoadDiseasesCommand e1200 = new LoadDiseasesCommand();
      e1200.setId("12:00");
      e1200.setNames("[common cold, influenza, pneumonia]");
      publish(e1200);
      waitForEvent("12:00");

      // check MarburgHealthSystem
      open("http://localhost:42001");
      for (DataEvent dataEvent : marburgHealthSystem.getBuilder().getEventStore().values()) {
         marburgHealthSystem.getBuilder().load(dataEvent.getBlockId());
      }
      modelMap = marburgHealthSystem.getBuilder().getModel().getModelMap();
      if (modelMap.values().size() > 0) {
         org.fulib.FulibTools.objectDiagrams().dumpSVG("tmp/marburgHealthSystem12_00.svg", modelMap.values());
      }

      open("http://localhost:42001");
      // check data note 12:00:01
      DiseaseBuilt e12_00_01 = (DiseaseBuilt) waitForEvent("12:00:01");
      assertThat(e12_00_01.getName()).isEqualTo("common cold");
      assertThat(e12_00_01.getSymptoms()).isEqualTo("[runny nose, cough, hoarseness, medium fever]");
      assertThat(e12_00_01.getCounterSymptoms()).isEqualTo("[chills, joint pain]");
      // check data note 12:00:02
      DiseaseBuilt e12_00_02 = (DiseaseBuilt) waitForEvent("12:00:02");
      assertThat(e12_00_02.getName()).isEqualTo("influenza");
      assertThat(e12_00_02.getSymptoms()).isEqualTo("[cough, medium fever, chills, joint pain, headache]");
      assertThat(e12_00_02.getCounterSymptoms()).isEqualTo("[lung noises]");
      // check data note 12:00:03
      DiseaseBuilt e12_00_03 = (DiseaseBuilt) waitForEvent("12:00:03");
      assertThat(e12_00_03.getName()).isEqualTo("pneumonia");
      assertThat(e12_00_03.getSymptoms()).isEqualTo("[cough, medium fever, chills, joint pain, headache, lung noises]");
      // check data note 12:00:04
      SymptomBuilt e12_00_04 = (SymptomBuilt) waitForEvent("12:00:04");
      assertThat(e12_00_04.getName()).isEqualTo("cough");
      // check data note 12:00:05
      SymptomBuilt e12_00_05 = (SymptomBuilt) waitForEvent("12:00:05");
      assertThat(e12_00_05.getName()).isEqualTo("runny nose");
      // check data note 12:00:06
      SymptomBuilt e12_00_06 = (SymptomBuilt) waitForEvent("12:00:06");
      assertThat(e12_00_06.getName()).isEqualTo("hoarseness");
      // check data note 12:00:07
      SymptomBuilt e12_00_07 = (SymptomBuilt) waitForEvent("12:00:07");
      assertThat(e12_00_07.getName()).isEqualTo("medium fever");
      // check data note 12:00:08
      SymptomBuilt e12_00_08 = (SymptomBuilt) waitForEvent("12:00:08");
      assertThat(e12_00_08.getName()).isEqualTo("chills");
      // check data note 12:00:09
      SymptomBuilt e12_00_09 = (SymptomBuilt) waitForEvent("12:00:09");
      assertThat(e12_00_09.getName()).isEqualTo("joint pain");
      // check data note 12:00:10
      SymptomBuilt e12_00_10 = (SymptomBuilt) waitForEvent("12:00:10");
      assertThat(e12_00_10.getName()).isEqualTo("headache");
      // check data note 12:00:11
      SymptomBuilt e12_00_11 = (SymptomBuilt) waitForEvent("12:00:11");
      assertThat(e12_00_11.getName()).isEqualTo("lung noises");
      try {
         Thread.sleep(3000);
      } catch (Exception e) {
      }
      eventBroker.stop();
      spark.stop();
      marburgHealthSystem.stop();

      System.err.println("HealthExperts completed good and gracefully");
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
}
