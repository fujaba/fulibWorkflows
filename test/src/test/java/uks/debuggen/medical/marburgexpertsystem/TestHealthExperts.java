package uks.debuggen.medical.marburgexpertsystem;
import com.codeborne.selenide.SelenideElement;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.util.LinkedHashMap;
import org.fulib.yaml.Yaml;
import org.junit.Test;
import uks.debuggen.medical.marburgexpertsystem.MarburgHealthSystem.MarburgHealthSystemService;
import uks.debuggen.medical.marburgexpertsystem.events.*;
import static com.codeborne.selenide.Condition.matchText;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
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
      org.fulib.FulibTools.objectDiagrams().dumpSVG("tmp/marburgHealthSystem12_00.svg", modelMap.values());

      open("http://localhost:42001");
      // check data note 12:00:01
      pre = $("#data");
      pre.shouldHave(text("- common_cold:"));
      pre.shouldHave(matchText("name:.*\"common cold\""));
      pre.shouldHave(matchText("symptoms:.*runny_nosecoughhoarsenessmedium_fever"));
      pre.shouldHave(matchText("counterSymptoms:.*chillsjoint_pain"));
      // check data note 12:00:02
      pre = $("#data");
      pre.shouldHave(text("- influenza:"));
      pre.shouldHave(matchText("name:.*influenza"));
      pre.shouldHave(matchText("symptoms:.*coughmedium_feverchillsjoint_painheadache"));
      pre.shouldHave(matchText("counterSymptoms:.*lung_noises"));
      // check data note 12:00:03
      pre = $("#data");
      pre.shouldHave(text("- pneumonia:"));
      pre.shouldHave(matchText("name:.*pneumonia"));
      pre.shouldHave(matchText("symptoms:.*coughmedium_feverchillsjoint_painheadachelung_noises"));
      // check data note 12:00:04
      pre = $("#data");
      pre.shouldHave(text("- cough:"));
      pre.shouldHave(matchText("name:.*cough"));
      // check data note 12:00:05
      pre = $("#data");
      pre.shouldHave(text("- runny_nose:"));
      pre.shouldHave(matchText("name:.*\"runny nose\""));
      // check data note 12:00:06
      pre = $("#data");
      pre.shouldHave(text("- hoarseness:"));
      pre.shouldHave(matchText("name:.*hoarseness"));
      // check data note 12:00:07
      pre = $("#data");
      pre.shouldHave(text("- medium_fever:"));
      pre.shouldHave(matchText("name:.*\"medium fever\""));
      // check data note 12:00:08
      pre = $("#data");
      pre.shouldHave(text("- chills:"));
      pre.shouldHave(matchText("name:.*chills"));
      // check data note 12:00:09
      pre = $("#data");
      pre.shouldHave(text("- joint_pain:"));
      pre.shouldHave(matchText("name:.*\"joint pain\""));
      // check data note 12:00:10
      pre = $("#data");
      pre.shouldHave(text("- headache:"));
      pre.shouldHave(matchText("name:.*headache"));
      // check data note 12:00:11
      pre = $("#data");
      pre.shouldHave(text("- lung_noises:"));
      pre.shouldHave(matchText("name:.*\"lung noises\""));

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
