package uks.debuggen.medical;
import com.codeborne.selenide.SelenideElement;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.fulib.yaml.Yaml;
import org.junit.Test;
import uks.debuggen.medical.events.*;
import static com.codeborne.selenide.Condition.matchText;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import java.util.Objects;
import java.beans.PropertyChangeSupport;

public class TestMedicalSystem
{
   public static final String PROPERTY_EVENT_BROKER = "eventBroker";
   private EventBroker eventBroker;
   protected PropertyChangeSupport listeners;

   public EventBroker getEventBroker()
   {
      return this.eventBroker;
   }

   public TestMedicalSystem setEventBroker(EventBroker value)
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
   public void MedicalSystem()
   {
      // start the event broker
      eventBroker = new EventBroker();
      eventBroker.start();

      open("http://localhost:42000");
      $("body").shouldHave(text("event broker"));

      SelenideElement pre = $("pre");

      // workflow Overview
      // workflow MarburgExpertSystem
      // create Disease_registeredEvent: disease_registered 13:00
      Disease_registeredEvent e1300 = new Disease_registeredEvent();
      e1300.setId("13:00");
      e1300.setName("common cold");
      e1300.setSymptoms("[cough, runny nose, hoarseness, fever]");
      e1300.setCounterSymptoms("[chills, joint pain]");
      publish(e1300);

      open("http://localhost:42000");
      pre = $("#history");
      pre.shouldHave(text("- 13_00:"));

      // create Disease_registeredEvent: disease_registered 13:01
      Disease_registeredEvent e1301 = new Disease_registeredEvent();
      e1301.setId("13:01");
      e1301.setName("influenza");
      e1301.setSymptoms("[cough, medium fever, chills, joint pain, headache]");
      e1301.setCounterSymptoms("[lung noises]");
      publish(e1301);

      open("http://localhost:42000");
      pre = $("#history");
      pre.shouldHave(text("- 13_01:"));

      // create Disease_registeredEvent: disease_registered 13:02
      Disease_registeredEvent e1302 = new Disease_registeredEvent();
      e1302.setId("13:02");
      e1302.setName("pneumonia");
      e1302.setSymptoms("[cough, medium fever, chills, joint pain, headache, lung noises]");
      publish(e1302);

      open("http://localhost:42000");
      pre = $("#history");
      pre.shouldHave(text("- 13_02:"));

      // workflow Stiko
      // create CovidRiskRegisteredEvent: covid risk registered 16:00
      CovidRiskRegisteredEvent e1600 = new CovidRiskRegisteredEvent();
      e1600.setId("16:00");
      e1600.setDisease("common cold");
      e1600.setRisk("2.0");
      publish(e1600);

      open("http://localhost:42000");
      pre = $("#history");
      pre.shouldHave(text("- 16_00:"));

      // create CovidRiskRegisteredEvent: covid risk registered 16:01
      CovidRiskRegisteredEvent e1601 = new CovidRiskRegisteredEvent();
      e1601.setId("16:01");
      e1601.setDisease("influenza");
      e1601.setRisk("4.0");
      publish(e1601);

      open("http://localhost:42000");
      pre = $("#history");
      pre.shouldHave(text("- 16_01:"));

      // workflow HealthInsurancesAssociation
      // create TestPriceAddedEvent: test price added 14:05
      TestPriceAddedEvent e1405 = new TestPriceAddedEvent();
      e1405.setId("14:05");
      e1405.setTest("temperature");
      e1405.setPrice("12.00");
      publish(e1405);

      open("http://localhost:42000");
      pre = $("#history");
      pre.shouldHave(text("- 14_05:"));

      // create GeneralPricesAddedEvent: general prices added 14:06
      GeneralPricesAddedEvent e1406 = new GeneralPricesAddedEvent();
      e1406.setId("14:06");
      e1406.setConsultation("10.00");
      e1406.setDiagnosis("12.00");
      e1406.setTreatment("10.00");
      publish(e1406);

      open("http://localhost:42000");
      pre = $("#history");
      pre.shouldHave(text("- 14_06:"));

      // workflow FamilyDoctorDegen
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

      // workflow Consultations
      // create PatientRegisteredEvent: patient registered 14:01
      PatientRegisteredEvent e1401 = new PatientRegisteredEvent();
      e1401.setId("14:01");
      e1401.setName("Alice");
      e1401.setAddress("Wonderland 1");
      e1401.setBirthDate("1970-01-01");
      publish(e1401);

      open("http://localhost:42000");
      pre = $("#history");
      pre.shouldHave(text("- 14_01:"));

      // create ConsultationRegisteredEvent: consultation registered 14:02
      ConsultationRegisteredEvent e1402 = new ConsultationRegisteredEvent();
      e1402.setId("14:02");
      e1402.setPatient("Alice");
      e1402.setDate("2021-06-02T11:00");
      publish(e1402);

      open("http://localhost:42000");
      pre = $("#history");
      pre.shouldHave(text("- 14_02:"));

      // create SymptomValidatedEvent: symptom validated 14:02:01
      SymptomValidatedEvent e140201 = new SymptomValidatedEvent();
      e140201.setId("14:02:01");
      e140201.setSymptom("cough");
      e140201.setConsultation("Alice#2021-06-02T11:00");
      publish(e140201);

      open("http://localhost:42000");
      pre = $("#history");
      pre.shouldHave(text("- 14_02_01:"));

      // create SymptomValidatedEvent: symptom validated 14:02:02
      SymptomValidatedEvent e140202 = new SymptomValidatedEvent();
      e140202.setId("14:02:02");
      e140202.setSymptom("runny nose");
      e140202.setConsultation("Alice#2021-06-02T11:00");
      publish(e140202);

      open("http://localhost:42000");
      pre = $("#history");
      pre.shouldHave(text("- 14_02_02:"));

      // create TestDoneEvent: test done 14:03
      TestDoneEvent e1403 = new TestDoneEvent();
      e1403.setId("14:03");
      e1403.setTest("temperature");
      e1403.setResult("39.8 Celsius");
      e1403.setConsultation("Alice#2021-06-02T11:00");
      publish(e1403);

      open("http://localhost:42000");
      pre = $("#history");
      pre.shouldHave(text("- 14_03:"));

      // create DiagnosisDoneEvent: diagnosis done 14:04
      DiagnosisDoneEvent e1404 = new DiagnosisDoneEvent();
      e1404.setId("14:04");
      e1404.setDiagnosis("common cold");
      e1404.setConsultation("Alice#2021-06-02T11:00");
      publish(e1404);

      open("http://localhost:42000");
      pre = $("#history");
      pre.shouldHave(text("- 14_04:"));

      // create TreatmentInitiatedEvent: treatment initiated 14:05:01
      TreatmentInitiatedEvent e140501 = new TreatmentInitiatedEvent();
      e140501.setId("14:05:01");
      e140501.setTreatment("ibuprofen 400 1-1-1");
      e140501.setConsultation("Alice#2021-06-02T11:00");
      publish(e140501);

      open("http://localhost:42000");
      pre = $("#history");
      pre.shouldHave(text("- 14_05_01:"));

      // workflow Accounting
      // create InvoiceSentEvent: invoice sent 14:07
      InvoiceSentEvent e1407 = new InvoiceSentEvent();
      e1407.setId("14:07");
      publish(e1407);

      open("http://localhost:42000");
      pre = $("#history");
      pre.shouldHave(text("- 14_07:"));

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
