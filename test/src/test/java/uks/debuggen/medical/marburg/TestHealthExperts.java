package uks.debuggen.medical.marburg;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.SelenideElement;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.fulib.yaml.Yaml;
import org.junit.Before;
import org.junit.Test;
import uks.debuggen.medical.marburg.MarburgHealthSystem.Disease;
import uks.debuggen.medical.marburg.MarburgHealthSystem.MarburgHealthSystemModel;
import uks.debuggen.medical.marburg.MarburgHealthSystem.MarburgHealthSystemService;
import uks.debuggen.medical.marburg.events.*;
import static com.codeborne.selenide.Condition.matchText;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.LinkedHashMap;
import java.util.Objects;
import java.beans.PropertyChangeSupport;

public class TestHealthExperts
{
   public static final String PROPERTY_EVENT_BROKER = "eventBroker";
   private EventBroker eventBroker;
   protected PropertyChangeSupport listeners;

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

   @Before
   public void setTimeOut() {
      Configuration.timeout = 10 * 60 * 1000;
      Configuration.pageLoadTimeout = Configuration.timeout;
      Configuration.browserPosition = "-3500x10";
   }

   @Test
   public void HealthExpertsManual()
   {
      // start the event broker
      eventBroker = new EventBroker();
      eventBroker.start();

      // start service
      MarburgHealthSystemService marburgHealthSystem = new MarburgHealthSystemService();
      marburgHealthSystem.start();

      open("http://localhost:42000");
      $("body").shouldHave(text("event broker"));

      SelenideElement pre = $("pre");
      pre.shouldHave(text("http://localhost:42001/apply"));

      // workflow MarburgExpertSystem
      // create LoadDiseasesCommand: load diseases 12:00
      LoadDiseasesCommand e1200 = new LoadDiseasesCommand();
      e1200.setId("12:00");
      e1200.setNames("[common cold, influenza, pneumonia]");
      publish(e1200);

      open("http://localhost:42000");
      pre = $("#history");
      pre.shouldHave(text("- 12_00:"));

      LinkedHashMap<String, Object> modelMap;

      // check MarburgHealthSystem
      open("http://localhost:42001");
      pre = $("#history");
      pre.shouldHave(text("- 12_00:"));

      for (DataEvent dataEvent : marburgHealthSystem.getBuilder().getEventStore().values()) {
         marburgHealthSystem.getBuilder().load(dataEvent.getBlockId());
      }

      modelMap = marburgHealthSystem.getBuilder().getModel().getModelMap();
      Disease disease = (Disease) modelMap.get("commonCold");
      assertThat(disease).isNotNull();
      assertThat(disease.getName()).isEqualTo("common cold");


      System.out.println();

//      // check data note 12:00:01
//      pre = $("#data");
//      pre.shouldHave(text("- commonCold:"));
//      pre.shouldHave(matchText("name:.*\"common cold\""));
//      pre.shouldHave(matchText("symptoms:.*cough, runny nose, hoarseness, fever"));
//      pre.shouldHave(matchText("counterSymptoms:.*chills, joint pain"));
//      // check data note 12:00:02
//      pre = $("#data");
//      pre.shouldHave(text("- influenza:"));
//      pre.shouldHave(matchText("name:.*influenza"));
//      pre.shouldHave(matchText("symptoms:.*cough, medium fever, chills, joint pain, headache"));
//      pre.shouldHave(matchText("counterSymptoms:.*lung noises"));
//      // check data note 12:00:03
//      pre = $("#data");
//      pre.shouldHave(text("- pneumonia:"));
//      pre.shouldHave(matchText("name:.*pneumonia"));
//      pre.shouldHave(matchText("symptoms:.*cough, medium fever, chills, joint pain, headache, lung noises"));

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

   @Test
   public void HealthExperts()
   {
      // start the event broker
      eventBroker = new EventBroker();
      eventBroker.start();

      // start service
      MarburgHealthSystemService marburgHealthSystem = new MarburgHealthSystemService();
      marburgHealthSystem.start();

      open("http://localhost:42000");
      $("body").shouldHave(text("event broker"));

      SelenideElement pre = $("pre");
      pre.shouldHave(text("http://localhost:42001/apply"));
      LinkedHashMap<String, Object> modelMap;

      // workflow MarburgExpertSystem
      // create LoadDiseasesCommand: load diseases 12:00
      LoadDiseasesCommand e1200 = new LoadDiseasesCommand();
      e1200.setId("12:00");
      e1200.setNames("[common cold, influenza, pneumonia]");
      publish(e1200);

      open("http://localhost:42000");
      pre = $("#history");
      pre.shouldHave(text("- 12_00:"));

      // check MarburgHealthSystem
      open("http://localhost:42001");
      pre = $("#history");
      pre.shouldHave(text("- 12_00:"));
      for (DataEvent dataEvent : marburgHealthSystem.getBuilder().getEventStore().values()) {
         marburgHealthSystem.getBuilder().load(dataEvent.getBlockId());
      }
      modelMap = marburgHealthSystem.getBuilder().getModel().getModelMap();
      open("http://localhost:42001");
      // check data note 12:00:01
      pre = $("#data");
      pre.shouldHave(text("- commonCold:"));
      pre.shouldHave(matchText("name:.*\"common cold\""));
      pre.shouldHave(matchText("symptoms:.*cough, runny nose, hoarseness, fever"));
      pre.shouldHave(matchText("counterSymptoms:.*chills, joint pain"));
      // check data note 12:00:02
      pre = $("#data");
      pre.shouldHave(text("- influenza:"));
      pre.shouldHave(matchText("name:.*influenza"));
      pre.shouldHave(matchText("symptoms:.*cough, medium fever, chills, joint pain, headache"));
      pre.shouldHave(matchText("counterSymptoms:.*lung noises"));
      // check data note 12:00:03
      pre = $("#data");
      pre.shouldHave(text("- pneumonia:"));
      pre.shouldHave(matchText("name:.*pneumonia"));
      pre.shouldHave(matchText("symptoms:.*cough, medium fever, chills, joint pain, headache, lung noises"));

      System.out.println();
   }
}
