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
      Configuration.timeout = 10 * 60 * 1000;
      Configuration.pageLoadTimeout = Configuration.timeout;
      Configuration.browserPosition = "-3500x10";
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

      open("http://localhost:42000");
      $("body").shouldHave(text("event broker"));

      SelenideElement pre = $("pre");
      pre.shouldHave(text("http://localhost:42001/apply"));
      LinkedHashMap<String, Object> modelMap;

      // workflow FamilyDoctorDegen
      // workflow ImportKnowledge
      // workflow SimpleConsultations
      // create PatientRegisteredEvent: patient registered 14:00
      PatientRegisteredEvent e1400 = new PatientRegisteredEvent();
      e1400.setId("14:00");
      e1400.setName("Alice");
      e1400.setAddress("Wonderland 1");
      e1400.setBirthDate("1970-01-01");
      publish(e1400);

      open("http://localhost:42000");
      pre = $("#history");
      pre.shouldHave(text("- 14_00:"));

      // check DocMedical
      open("http://localhost:42001");
      pre = $("#history");
      pre.shouldHave(text("- 14_00:"));
      for (DataEvent dataEvent : docMedical.getBuilder().getEventStore().values()) {
         docMedical.getBuilder().load(dataEvent.getBlockId());
      }
      modelMap = docMedical.getBuilder().getModel().getModelMap();
      org.fulib.FulibTools.objectDiagrams().dumpSVG("tmp/docMedical14_00.svg", modelMap.values());

      open("http://localhost:42001");
      // check data note 14:00:01
      pre = $("#data");
      pre.shouldHave(text("- alice:"));
      pre.shouldHave(matchText("name:.*Alice"));
      pre.shouldHave(matchText("address:.*\"Wonderland 1\""));
      pre.shouldHave(matchText("birthDate:.*1970-01-01"));

      // create ConsultationEvent: consultation registered
      ConsultationEvent e1401 = new ConsultationEvent();
      e1401.setId("14:01");
      e1401.setPatient("Alice");
      e1401.setDate("2021-06-02T14:00");
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
      org.fulib.FulibTools.objectDiagrams().dumpSVG("tmp/docMedical14_01.svg", modelMap.values());

      open("http://localhost:42001");
      // check data note 14:01:01
      pre = $("#data");
      pre.shouldHave(text("- alice_2021_06_02T14_00:"));
      pre.shouldHave(matchText("cid:.*Alice#2021-06-02T14:00"));
      pre.shouldHave(matchText("patient:.*alice"));

      // create SymptomEvent: symptom validated
      SymptomEvent e1402 = new SymptomEvent();
      e1402.setId("14:02");
      e1402.setSymptom("cough");
      e1402.setConsultation("Alice#2021-06-02T14:00");
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
      org.fulib.FulibTools.objectDiagrams().dumpSVG("tmp/docMedical14_02.svg", modelMap.values());

      open("http://localhost:42001");
      // check data note 14:02:01
      pre = $("#data");
      pre.shouldHave(text("- cough:"));
      pre.shouldHave(matchText("name:.*cough"));
      pre.shouldHave(matchText("consultations:.*alice_2021_06_02T14_00"));

      // create SymptomEvent: symptom validated
      SymptomEvent e1403 = new SymptomEvent();
      e1403.setId("14:03");
      e1403.setSymptom("runny nose");
      e1403.setConsultations("[Alice#2021-06-02T14:00]");
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
      org.fulib.FulibTools.objectDiagrams().dumpSVG("tmp/docMedical14_03.svg", modelMap.values());

      open("http://localhost:42001");
      // check data note 14:03:01
      pre = $("#data");
      pre.shouldHave(text("- runny_nose:"));
      pre.shouldHave(matchText("name:.*\"runny nose\""));
      pre.shouldHave(matchText("consultations:.*alice_2021_06_02T14_00"));

      // create TestEvent: test done
      TestEvent e1404 = new TestEvent();
      e1404.setId("14:04");
      e1404.setTest("temperature");
      e1404.setResult("39.8 Celsius");
      e1404.setConsultation("[Alice#2021-06-02T14:00]");
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
      org.fulib.FulibTools.objectDiagrams().dumpSVG("tmp/docMedical14_04.svg", modelMap.values());

      open("http://localhost:42001");
      // check data note 14:04:01
      pre = $("#data");
      pre.shouldHave(text("- alice_t001_2021_06_02T14_00:"));
      pre.shouldHave(matchText("cid:.*Alice#t001#2021-06-02T14:00"));
      pre.shouldHave(matchText("kind:.*temperature"));
      pre.shouldHave(matchText("result:.*\"39.8 Celsius\""));
      pre.shouldHave(matchText("consultation:.*alice_2021_06_02T14_00"));
      // check data note 14:04:02
      pre = $("#data");
      pre.shouldHave(text("- fever:"));
      pre.shouldHave(matchText("name:.*fever"));
      pre.shouldHave(matchText("consultations:.*alice_2021_06_02T14_00"));

      // create DiagnosisEvent: diagnosis done
      DiagnosisEvent e1405 = new DiagnosisEvent();
      e1405.setId("14:05");
      e1405.setDiagnosis("common cold");
      e1405.setConsultation("Alice#2021-06-02T14:00");
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
      org.fulib.FulibTools.objectDiagrams().dumpSVG("tmp/docMedical14_05.svg", modelMap.values());

      open("http://localhost:42001");
      // check data note 14:05:01
      pre = $("#data");
      pre.shouldHave(text("- alice_2021_06_02T14_00:"));
      pre.shouldHave(matchText("cid:.*Alice#2021-06-02T14:00"));
      pre.shouldHave(matchText("diagnosis:.*common_cold"));

      // create TreatmentEvent: treatment initiated
      TreatmentEvent e1406 = new TreatmentEvent();
      e1406.setId("14:06");
      e1406.setTreatment("ibuprofen 400 1-1-1");
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
      org.fulib.FulibTools.objectDiagrams().dumpSVG("tmp/docMedical14_06.svg", modelMap.values());

      open("http://localhost:42001");
      // check data note 14:06:01
      pre = $("#data");
      pre.shouldHave(text("- alice_2021_06_02T14_00:"));
      pre.shouldHave(matchText("cid:.*Alice#2021-06-02T14:00"));
      pre.shouldHave(matchText("treatment:.*\"ibuprofen 400 1-1-1\""));

      // workflow Accounting
      // create InvoiceSentEvent: invoice sent 14:07
      InvoiceSentEvent e1407 = new InvoiceSentEvent();
      e1407.setId("14:07");
      publish(e1407);

      open("http://localhost:42000");
      pre = $("#history");
      pre.shouldHave(text("- 14_07:"));

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

      System.out.println();
   }

   public void publish(Event event)
   {
      String yaml = Yaml.encodeSimple(event);

      try {
         HttpResponse<String> response = Unirest.post("http://localhost:42000/publish")
               .body(yaml)
               .asString();
      }
      catch (UnirestException e) {
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