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

public class TestFamilyDoctor
{
   public static final String PROPERTY_EVENT_BROKER = "eventBroker";
   private EventBroker eventBroker;
   protected PropertyChangeSupport listeners;

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

      // start service
      DocMedicalService docMedical = new DocMedicalService();
      docMedical.start();
      try {
         Thread.sleep(1500);
      } catch (Exception e) {
      }

      open("http://localhost:42000");
      $("body").shouldHave(text("event broker"));

      SelenideElement pre = $("pre");
      pre.shouldHave(text("http://localhost:42001/apply"));
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

      open("http://localhost:42000");
      pre = $("#history");
      pre.shouldHave(text("- 14_01:"));

      // check DocMedical
      open("http://localhost:42001");
      pre = $("#history");
      pre.shouldHave(text("- 14_01:"));
      for (DataEvent dataEvent : docMedical.getBuilder().getEventStore().values()) {
         docMedical.getBuilder().load(dataEvent.getBlockId());
      }
      modelMap = docMedical.getBuilder().getModel().getModelMap();
      if (modelMap.values().size() > 0) {
         org.fulib.FulibTools.objectDiagrams().dumpSVG("tmp/docMedical14_01.svg", modelMap.values());
      }

      open("http://localhost:42001");
      // check data note 14:01:01
      pre = $("#data");
      pre.shouldHave(text("- alice:"));
      pre.shouldHave(matchText("name:.*Alice"));
      pre.shouldHave(matchText("address:.*\"Wonderland 1\""));
      pre.shouldHave(matchText("birthDate:.*1970-01-01"));

      // create ConsultationRegisteredEvent: consultation registered
      ConsultationRegisteredEvent e1402 = new ConsultationRegisteredEvent();
      e1402.setId("14:02");
      e1402.setPatient("Alice");
      e1402.setDate("2021-06-02T14:00");
      publish(e1402);

      open("http://localhost:42000");
      pre = $("#history");
      pre.shouldHave(text("- 14_02:"));

      // check DocMedical
      open("http://localhost:42001");
      pre = $("#history");
      pre.shouldHave(text("- 14_02:"));
      for (DataEvent dataEvent : docMedical.getBuilder().getEventStore().values()) {
         docMedical.getBuilder().load(dataEvent.getBlockId());
      }
      modelMap = docMedical.getBuilder().getModel().getModelMap();
      if (modelMap.values().size() > 0) {
         org.fulib.FulibTools.objectDiagrams().dumpSVG("tmp/docMedical14_02.svg", modelMap.values());
      }

      open("http://localhost:42001");
      // check data note 14:02:01
      pre = $("#data");
      pre.shouldHave(text("- alice_2021_06_02T14_00:"));
      pre.shouldHave(matchText("cid:.*Alice#2021-06-02T14:00"));
      pre.shouldHave(matchText("patient:.*alice"));

      // create SymptomValidatedEvent: symptom validated
      SymptomValidatedEvent e1403 = new SymptomValidatedEvent();
      e1403.setId("14:03");
      e1403.setSymptom("cough");
      e1403.setConsultation("Alice#2021-06-02T14:00");
      publish(e1403);

      open("http://localhost:42000");
      pre = $("#history");
      pre.shouldHave(text("- 14_03:"));

      // check DocMedical
      open("http://localhost:42001");
      pre = $("#history");
      pre.shouldHave(text("- 14_03:"));
      for (DataEvent dataEvent : docMedical.getBuilder().getEventStore().values()) {
         docMedical.getBuilder().load(dataEvent.getBlockId());
      }
      modelMap = docMedical.getBuilder().getModel().getModelMap();
      if (modelMap.values().size() > 0) {
         org.fulib.FulibTools.objectDiagrams().dumpSVG("tmp/docMedical14_03.svg", modelMap.values());
      }

      open("http://localhost:42001");
      // check data note 14:03:01
      pre = $("#data");
      pre.shouldHave(text("- cough:"));
      pre.shouldHave(matchText("name:.*cough"));
      pre.shouldHave(matchText("consultations:.*alice_2021_06_02T14_00.*"));

      // create SymptomValidatedEvent: symptom validated
      SymptomValidatedEvent e1404 = new SymptomValidatedEvent();
      e1404.setId("14:04");
      e1404.setSymptom("runny nose");
      e1404.setConsultations("[Alice#2021-06-02T14:00]");
      publish(e1404);

      open("http://localhost:42000");
      pre = $("#history");
      pre.shouldHave(text("- 14_04:"));

      // check DocMedical
      open("http://localhost:42001");
      pre = $("#history");
      pre.shouldHave(text("- 14_04:"));
      for (DataEvent dataEvent : docMedical.getBuilder().getEventStore().values()) {
         docMedical.getBuilder().load(dataEvent.getBlockId());
      }
      modelMap = docMedical.getBuilder().getModel().getModelMap();
      if (modelMap.values().size() > 0) {
         org.fulib.FulibTools.objectDiagrams().dumpSVG("tmp/docMedical14_04.svg", modelMap.values());
      }

      open("http://localhost:42001");
      // check data note 14:04:01
      pre = $("#data");
      pre.shouldHave(text("- runny_nose:"));
      pre.shouldHave(matchText("name:.*\"runny nose\""));
      pre.shouldHave(matchText("consultations:.*alice_2021_06_02T14_00.*"));

      // create TestDoneEvent: test done
      TestDoneEvent e1405 = new TestDoneEvent();
      e1405.setId("14:05");
      e1405.setTest("temperature");
      e1405.setResult("39.8 Celsius");
      e1405.setConsultation("[Alice#2021-06-02T14:00]");
      publish(e1405);

      open("http://localhost:42000");
      pre = $("#history");
      pre.shouldHave(text("- 14_05:"));

      // check DocMedical
      open("http://localhost:42001");
      pre = $("#history");
      pre.shouldHave(text("- 14_05:"));
      for (DataEvent dataEvent : docMedical.getBuilder().getEventStore().values()) {
         docMedical.getBuilder().load(dataEvent.getBlockId());
      }
      modelMap = docMedical.getBuilder().getModel().getModelMap();
      if (modelMap.values().size() > 0) {
         org.fulib.FulibTools.objectDiagrams().dumpSVG("tmp/docMedical14_05.svg", modelMap.values());
      }

      open("http://localhost:42001");
      // check data note 14:05:01
      pre = $("#data");
      pre.shouldHave(text("- alice_t001_2021_06_02T14_00:"));
      pre.shouldHave(matchText("cid:.*Alice#t001#2021-06-02T14:00"));
      pre.shouldHave(matchText("kind:.*temperature"));
      pre.shouldHave(matchText("result:.*\"39.8 Celsius\""));
      pre.shouldHave(matchText("consultation:.*alice_2021_06_02T14_00"));
      // check data note 14:05:02
      pre = $("#data");
      pre.shouldHave(text("- medium_fever:"));
      pre.shouldHave(matchText("name:.*\"medium fever\""));
      pre.shouldHave(matchText("consultations:.*alice_2021_06_02T14_00.*"));

      // create DiagnosisDoneEvent: diagnosis done
      DiagnosisDoneEvent e1406 = new DiagnosisDoneEvent();
      e1406.setId("14:06");
      e1406.setDiagnosis("common cold");
      e1406.setConsultation("Alice#2021-06-02T14:00");
      publish(e1406);

      open("http://localhost:42000");
      pre = $("#history");
      pre.shouldHave(text("- 14_06:"));

      // check DocMedical
      open("http://localhost:42001");
      pre = $("#history");
      pre.shouldHave(text("- 14_06:"));
      for (DataEvent dataEvent : docMedical.getBuilder().getEventStore().values()) {
         docMedical.getBuilder().load(dataEvent.getBlockId());
      }
      modelMap = docMedical.getBuilder().getModel().getModelMap();
      if (modelMap.values().size() > 0) {
         org.fulib.FulibTools.objectDiagrams().dumpSVG("tmp/docMedical14_06.svg", modelMap.values());
      }

      open("http://localhost:42001");
      // check data note 14:06:01
      pre = $("#data");
      pre.shouldHave(text("- alice_2021_06_02T14_00:"));
      pre.shouldHave(matchText("cid:.*Alice#2021-06-02T14:00"));
      pre.shouldHave(matchText("diagnosis:.*common_cold"));

      // create TreatmentInitiatedEvent: treatment initiated
      TreatmentInitiatedEvent e1407 = new TreatmentInitiatedEvent();
      e1407.setId("14:07");
      e1407.setTreatment("ibuprofen 400 1-1-1");
      e1407.setConsultation("Alice#2021-06-02T14:00");
      publish(e1407);

      open("http://localhost:42000");
      pre = $("#history");
      pre.shouldHave(text("- 14_07:"));

      // check DocMedical
      open("http://localhost:42001");
      pre = $("#history");
      pre.shouldHave(text("- 14_07:"));
      for (DataEvent dataEvent : docMedical.getBuilder().getEventStore().values()) {
         docMedical.getBuilder().load(dataEvent.getBlockId());
      }
      modelMap = docMedical.getBuilder().getModel().getModelMap();
      if (modelMap.values().size() > 0) {
         org.fulib.FulibTools.objectDiagrams().dumpSVG("tmp/docMedical14_07.svg", modelMap.values());
      }

      open("http://localhost:42001");
      // check data note 14:07:01
      pre = $("#data");
      pre.shouldHave(text("- alice_2021_06_02T14_00:"));
      pre.shouldHave(matchText("cid:.*Alice#2021-06-02T14:00"));
      pre.shouldHave(matchText("treatment:.*\"ibuprofen 400 1-1-1\""));

      // workflow Accounting

      // create InvoiceSentEvent: invoice sent
      InvoiceSentEvent e1501 = new InvoiceSentEvent();
      e1501.setId("15:01");
      publish(e1501);

      open("http://localhost:42000");
      pre = $("#history");
      pre.shouldHave(text("- 15_01:"));

      // workflow CovidVaccination

      // create CovidVaccinationRequestedEvent: covid vaccination requested 14:10
      CovidVaccinationRequestedEvent e1410 = new CovidVaccinationRequestedEvent();
      e1410.setId("14:10");
      publish(e1410);

      open("http://localhost:42000");
      pre = $("#history");
      pre.shouldHave(text("- 14_10:"));

      // create DiagnosisDoneEvent: diagnosis done 14:20
      DiagnosisDoneEvent e1420 = new DiagnosisDoneEvent();
      e1420.setId("14:20");
      e1420.setDisease("covid");
      publish(e1420);

      open("http://localhost:42000");
      pre = $("#history");
      pre.shouldHave(text("- 14_20:"));

      // create TreatmentInitiatedEvent: treatment initiated 14:21
      TreatmentInitiatedEvent e1421 = new TreatmentInitiatedEvent();
      e1421.setId("14:21");
      e1421.setTreatment("covid vaccination");
      publish(e1421);

      open("http://localhost:42000");
      pre = $("#history");
      pre.shouldHave(text("- 14_21:"));

      // create DiagnosisDoneEvent: diagnosis done 14:20:02
      DiagnosisDoneEvent e142002 = new DiagnosisDoneEvent();
      e142002.setId("14:20:02");
      e142002.setDisease("fever from vaccination");
      publish(e142002);

      open("http://localhost:42000");
      pre = $("#history");
      pre.shouldHave(text("- 14_20_02:"));
      try {
         Thread.sleep(3000);
      } catch (Exception e) {
      }
      eventBroker.stop();
      docMedical.stop();

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
