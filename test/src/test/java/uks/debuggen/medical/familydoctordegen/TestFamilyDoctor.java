package uks.debuggen.medical.familydoctordegen;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.SelenideElement;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.util.LinkedHashMap;

import org.fulib.FulibTools;
import org.fulib.yaml.Yaml;
import org.junit.Before;
import org.junit.Test;

import uks.debuggen.Constants;
import uks.debuggen.medical.familydoctordegen.DocMedical.Consultation;
import uks.debuggen.medical.familydoctordegen.DocMedical.DocMedicalService;
import uks.debuggen.medical.familydoctordegen.events.*;
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

public class TestFamilyDoctor
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

   public TestFamilyDoctor setEventBroker(EventBroker value)
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

   public TestFamilyDoctor setSpark(Service value)
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

   public TestFamilyDoctor setEventQueue(LinkedBlockingQueue<Event> value)
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

   public TestFamilyDoctor setHistory(LinkedHashMap<String, Event> value)
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

   public TestFamilyDoctor setPort(int value)
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
   public void FamilyDoctor()
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
      DocMedicalService docMedical = new DocMedicalService();
      docMedical.start();
      waitForEvent("42001");
      SelenideElement pre;
      LinkedHashMap<String, Object> modelMap;

      // workflow FamilyDoctorDegen

      // workflow ImportKnowledge
         DiseaseBuilt common_coldEvent = new DiseaseBuilt();
         common_coldEvent.setId("12:00:01");
         common_coldEvent.setBlockId("common_cold");
         common_coldEvent.setName("common cold");
         common_coldEvent.setSymptoms("[runny nose, cough, hoarseness, medium fever]");
         common_coldEvent.setCounterSymptoms("[chills, joint pain]");
         publish(common_coldEvent);

         DiseaseBuilt influenzaEvent = new DiseaseBuilt();
         influenzaEvent.setId("12:00:02");
         influenzaEvent.setBlockId("influenza");
         influenzaEvent.setName("influenza");
         influenzaEvent.setSymptoms("[cough, medium fever, chills, joint pain, headache]");
         influenzaEvent.setCounterSymptoms("[lung noises]");
         publish(influenzaEvent);

         DiseaseBuilt pneumoniaEvent = new DiseaseBuilt();
         pneumoniaEvent.setId("12:00:03");
         pneumoniaEvent.setBlockId("pneumonia");
         pneumoniaEvent.setName("pneumonia");
         pneumoniaEvent.setSymptoms("[cough, medium fever, chills, joint pain, headache, lung noises]");
         publish(pneumoniaEvent);

         SymptomBuilt coughEvent = new SymptomBuilt();
         coughEvent.setId("12:00:04");
         coughEvent.setBlockId("cough");
         coughEvent.setName("cough");
         publish(coughEvent);

         SymptomBuilt runny_noseEvent = new SymptomBuilt();
         runny_noseEvent.setId("12:00:05");
         runny_noseEvent.setBlockId("runny_nose");
         runny_noseEvent.setName("runny nose");
         publish(runny_noseEvent);

         SymptomBuilt hoarsenessEvent = new SymptomBuilt();
         hoarsenessEvent.setId("12:00:06");
         hoarsenessEvent.setBlockId("hoarseness");
         hoarsenessEvent.setName("hoarseness");
         publish(hoarsenessEvent);

         SymptomBuilt medium_feverEvent = new SymptomBuilt();
         medium_feverEvent.setId("12:00:07");
         medium_feverEvent.setBlockId("medium_fever");
         medium_feverEvent.setName("medium fever");
         publish(medium_feverEvent);

         SymptomBuilt chillsEvent = new SymptomBuilt();
         chillsEvent.setId("12:00:08");
         chillsEvent.setBlockId("chills");
         chillsEvent.setName("chills");
         publish(chillsEvent);

         SymptomBuilt joint_painEvent = new SymptomBuilt();
         joint_painEvent.setId("12:00:09");
         joint_painEvent.setBlockId("joint_pain");
         joint_painEvent.setName("joint pain");
         publish(joint_painEvent);

         SymptomBuilt headacheEvent = new SymptomBuilt();
         headacheEvent.setId("12:00:10");
         headacheEvent.setBlockId("headache");
         headacheEvent.setName("headache");
         publish(headacheEvent);

         SymptomBuilt lung_noisesEvent = new SymptomBuilt();
         lung_noisesEvent.setId("12:00:11");
         lung_noisesEvent.setBlockId("lung_noises");
         lung_noisesEvent.setName("lung noises");
         publish(lung_noisesEvent);

         DiseaseBuilt disease2Event = new DiseaseBuilt();
         disease2Event.setId("13:00:01");
         disease2Event.setBlockId("Disease2");
         disease2Event.setMigratedTo("Disease2");
         publish(disease2Event);


      // workflow SimpleConsultations

      // create PatientRegisteredEvent: patient registered
      PatientRegisteredEvent e1401 = new PatientRegisteredEvent();
      e1401.setId("14:01");
      e1401.setName("Alice");
      e1401.setAddress("Wonderland 1");
      e1401.setBirthDate("1970-01-01");
      publish(e1401);
      waitForEvent("14:01");

      // check DocMedical
      open("http://localhost:42001");
      for (DataEvent dataEvent : docMedical.getBuilder().getEventStore().values()) {
         docMedical.getBuilder().load(dataEvent.getBlockId());
      }
      modelMap = docMedical.getBuilder().getModel().getModelMap();
      if (modelMap.values().size() > 0) {
         org.fulib.FulibTools.objectDiagrams().dumpSVG("tmp/docMedical14_01.svg", modelMap.values());
      }

      open("http://localhost:42001");
      // check data note 14:01:01
      PatientBuilt e14_01_01 = (PatientBuilt) waitForEvent("14:01:01");
      assertThat(e14_01_01.getName()).isEqualTo("Alice");
      assertThat(e14_01_01.getAddress()).isEqualTo("Wonderland 1");
      assertThat(e14_01_01.getBirthDate()).isEqualTo("1970-01-01");

      // create ConsultationRegisteredEvent: consultation registered
      ConsultationRegisteredEvent e1402 = new ConsultationRegisteredEvent();
      e1402.setId("14:02");
      e1402.setPatient("Alice");
      e1402.setDate("2021-06-02T14:00");
      publish(e1402);
      waitForEvent("14:02");

      // check DocMedical
      open("http://localhost:42001");
      for (DataEvent dataEvent : docMedical.getBuilder().getEventStore().values()) {
         docMedical.getBuilder().load(dataEvent.getBlockId());
      }
      modelMap = docMedical.getBuilder().getModel().getModelMap();
      if (modelMap.values().size() > 0) {
         org.fulib.FulibTools.objectDiagrams().dumpSVG("tmp/docMedical14_02.svg", modelMap.values());
      }

      open("http://localhost:42001");
      // check data note 14:02:01
      ConsultationBuilt e14_02_01 = (ConsultationBuilt) waitForEvent("14:02:01");
      assertThat(e14_02_01.getCid()).isEqualTo("Alice#2021-06-02T14:00");
      assertThat(e14_02_01.getPatient()).isEqualTo("Alice");

      // create SymptomValidatedEvent: symptom validated
      SymptomValidatedEvent e1403 = new SymptomValidatedEvent();
      e1403.setId("14:03");
      e1403.setSymptom("cough");
      e1403.setConsultation("Alice#2021-06-02T14:00");
      publish(e1403);
      waitForEvent("14:03");

      // check DocMedical
      open("http://localhost:42001");
      for (DataEvent dataEvent : docMedical.getBuilder().getEventStore().values()) {
         docMedical.getBuilder().load(dataEvent.getBlockId());
      }
      modelMap = docMedical.getBuilder().getModel().getModelMap();
      if (modelMap.values().size() > 0) {
         org.fulib.FulibTools.objectDiagrams().dumpSVG("tmp/docMedical14_03.svg", modelMap.values());
      }

      open("http://localhost:42001");
      // check data note 14:03:01
      SymptomBuilt e14_03_01 = (SymptomBuilt) waitForEvent("14:03:01");
      assertThat(e14_03_01.getName()).isEqualTo("cough");
      assertThat(e14_03_01.getConsultations()).isEqualTo("[Alice#2021-06-02T14:00]");

      // create SymptomValidatedEvent: symptom validated
      SymptomValidatedEvent e1404 = new SymptomValidatedEvent();
      e1404.setId("14:04");
      e1404.setSymptom("runny nose");
      e1404.setConsultations("[Alice#2021-06-02T14:00]");
      publish(e1404);
      waitForEvent("14:04");

      // check DocMedical
      open("http://localhost:42001");
      for (DataEvent dataEvent : docMedical.getBuilder().getEventStore().values()) {
         docMedical.getBuilder().load(dataEvent.getBlockId());
      }
      modelMap = docMedical.getBuilder().getModel().getModelMap();
      if (modelMap.values().size() > 0) {
         org.fulib.FulibTools.objectDiagrams().dumpSVG("tmp/docMedical14_04.svg", modelMap.values());
      }

      open("http://localhost:42001");
      // check data note 14:04:01
      SymptomBuilt e14_04_01 = (SymptomBuilt) waitForEvent("14:04:01");
      assertThat(e14_04_01.getName()).isEqualTo("runny nose");
      assertThat(e14_04_01.getConsultations()).isEqualTo("[Alice#2021-06-02T14:00]");

      // create TestDoneEvent: test done
      TestDoneEvent e1405 = new TestDoneEvent();
      e1405.setId("14:05");
      e1405.setTest("temperature");
      e1405.setResult("39.8 Celsius");
      e1405.setConsultation("[Alice#2021-06-02T14:00]");
      publish(e1405);
      waitForEvent("14:05");

      // check DocMedical
      open("http://localhost:42001");
      for (DataEvent dataEvent : docMedical.getBuilder().getEventStore().values()) {
         docMedical.getBuilder().load(dataEvent.getBlockId());
      }
      modelMap = docMedical.getBuilder().getModel().getModelMap();
      if (modelMap.values().size() > 0) {
         org.fulib.FulibTools.objectDiagrams().dumpSVG("tmp/docMedical14_05.svg", modelMap.values());
      }

      open("http://localhost:42001");
      // check data note 14:05:01
      TestBuilt e14_05_01 = (TestBuilt) waitForEvent("14:05:01");
      assertThat(e14_05_01.getCid()).isEqualTo("Alice#t001#2021-06-02T14:00");
      assertThat(e14_05_01.getKind()).isEqualTo("temperature");
      assertThat(e14_05_01.getResult()).isEqualTo("39.8 Celsius");
      assertThat(e14_05_01.getConsultation()).isEqualTo("Alice#2021-06-02T14:00");
      // check data note 14:05:02
      SymptomBuilt e14_05_02 = (SymptomBuilt) waitForEvent("14:05:02");
      assertThat(e14_05_02.getName()).isEqualTo("medium fever");
      assertThat(e14_05_02.getConsultations()).isEqualTo("[Alice#2021-06-02T14:00]");

      // create DiagnosisDoneEvent: diagnosis done
      DiagnosisDoneEvent e1406 = new DiagnosisDoneEvent();
      e1406.setId("14:06");
      e1406.setDiagnosis("common cold");
      e1406.setConsultation("Alice#2021-06-02T14:00");
      publish(e1406);
      waitForEvent("14:06");

      // check DocMedical
      open("http://localhost:42001");
      for (DataEvent dataEvent : docMedical.getBuilder().getEventStore().values()) {
         docMedical.getBuilder().load(dataEvent.getBlockId());
      }
      modelMap = docMedical.getBuilder().getModel().getModelMap();
      if (modelMap.values().size() > 0) {
         org.fulib.FulibTools.objectDiagrams().dumpSVG("tmp/docMedical14_06.svg", modelMap.values());
      }

      open("http://localhost:42001");
      // check data note 14:06:01
      ConsultationBuilt e14_06_01 = (ConsultationBuilt) waitForEvent("14:06:01");
      assertThat(e14_06_01.getCid()).isEqualTo("Alice#2021-06-02T14:00");
      assertThat(e14_06_01.getDiagnosis()).isEqualTo("common cold");

      // create TreatmentInitiatedEvent: treatment initiated
      TreatmentInitiatedEvent e1407 = new TreatmentInitiatedEvent();
      e1407.setId("14:07");
      e1407.setTreatment("ibuprofen 400 1-1-1");
      e1407.setConsultation("Alice#2021-06-02T14:00");
      publish(e1407);
      waitForEvent("14:07");

      // check DocMedical
      open("http://localhost:42001");
      for (DataEvent dataEvent : docMedical.getBuilder().getEventStore().values()) {
         docMedical.getBuilder().load(dataEvent.getBlockId());
      }
      modelMap = docMedical.getBuilder().getModel().getModelMap();
      if (modelMap.values().size() > 0) {
         org.fulib.FulibTools.objectDiagrams().dumpSVG("tmp/docMedical14_07.svg", modelMap.values());
      }

      open("http://localhost:42001");
      // check data note 14:07:01
      ConsultationBuilt e14_07_01 = (ConsultationBuilt) waitForEvent("14:07:01");
      assertThat(e14_07_01.getCid()).isEqualTo("Alice#2021-06-02T14:00");
      assertThat(e14_07_01.getTreatment()).isEqualTo("ibuprofen 400 1-1-1");

      // workflow Accounting

      // create InvoiceSentEvent: invoice sent
      InvoiceSentEvent e1501 = new InvoiceSentEvent();
      e1501.setId("15:01");
      publish(e1501);
      waitForEvent("15:01");

      // workflow CovidVaccination

      // create CovidVaccinationRequestedEvent: covid vaccination requested 14:10
      CovidVaccinationRequestedEvent e1410 = new CovidVaccinationRequestedEvent();
      e1410.setId("14:10");
      publish(e1410);
      waitForEvent("14:10");

      // create DiagnosisDoneEvent: diagnosis done 14:20
      DiagnosisDoneEvent e1420 = new DiagnosisDoneEvent();
      e1420.setId("14:20");
      e1420.setDisease("covid");
      publish(e1420);
      waitForEvent("14:20");

      // create TreatmentInitiatedEvent: treatment initiated 14:21
      TreatmentInitiatedEvent e1421 = new TreatmentInitiatedEvent();
      e1421.setId("14:21");
      e1421.setTreatment("covid vaccination");
      publish(e1421);
      waitForEvent("14:21");

      // create DiagnosisDoneEvent: diagnosis done 14:20:02
      DiagnosisDoneEvent e142002 = new DiagnosisDoneEvent();
      e142002.setId("14:20:02");
      e142002.setDisease("fever from vaccination");
      publish(e142002);
      waitForEvent("14:20:02");
      try {
         Thread.sleep(3000);
      } catch (Exception e) {
      }
      eventBroker.stop();
      spark.stop();
      docMedical.stop();

      System.err.println("FamilyDoctor completed good and gracefully");
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
