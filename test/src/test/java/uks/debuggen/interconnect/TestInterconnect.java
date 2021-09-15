package uks.debuggen.interconnect;
import com.codeborne.selenide.SelenideElement;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.fulib.yaml.Yaml;
import org.junit.Test;
import uks.debuggen.interconnect.CityElectricsKassel.CityElectricsKasselService;
import uks.debuggen.interconnect.IonicKassel42.IonicKassel42Service;
import uks.debuggen.interconnect.events.*;
import static com.codeborne.selenide.Condition.matchText;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import java.util.Objects;
import java.beans.PropertyChangeSupport;
import java.util.LinkedHashMap;

public class TestInterconnect
{
   public static final String PROPERTY_EVENT_BROKER = "eventBroker";
   private EventBroker eventBroker;
   protected PropertyChangeSupport listeners;

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

   @Test
   public void Interconnect()
   {
      // start the event broker
      eventBroker = new EventBroker();
      eventBroker.start();

      // start service
      IonicKassel42Service ionicKassel42 = new IonicKassel42Service();
      ionicKassel42.start();

      // start service
      CityElectricsKasselService cityElectricsKassel = new CityElectricsKasselService();
      cityElectricsKassel.start();

      open("http://localhost:42000");
      $("body").shouldHave(text("event broker"));

      SelenideElement pre = $("pre");
      pre.shouldHave(text("http://localhost:42001/apply"));
      pre.shouldHave(text("http://localhost:42002/apply"));
      LinkedHashMap<String, Object> modelMap;

      // workflow Overview

      // workflow UniKasselIonicCharger

      // create CarConnectedEvent: car connected 11:55
      CarConnectedEvent e1155 = new CarConnectedEvent();
      e1155.setId("11:55");
      publish(e1155);

      open("http://localhost:42000");
      pre = $("#history");
      pre.shouldHave(text("- 11_55:"));

      // check IonicKassel42
      open("http://localhost:42001");
      pre = $("#history");
      pre.shouldHave(text("- 11_55:"));
      for (DataEvent dataEvent : ionicKassel42.getBuilder().getEventStore().values()) {
         ionicKassel42.getBuilder().load(dataEvent.getBlockId());
      }
      modelMap = ionicKassel42.getBuilder().getModel().getModelMap();
<<<<<<< HEAD
      org.fulib.FulibTools.objectDiagrams().dumpSVG("tmp/ionicKassel4211_55.svg", modelMap.values());

=======
>>>>>>> d3ea970929d108027c425d763c0cb0d0be7e0237
      open("http://localhost:42001");

      // check CityElectricsKassel
      open("http://localhost:42002");
      pre = $("#history");
      pre.shouldHave(text("- 11_55:"));
      for (DataEvent dataEvent : cityElectricsKassel.getBuilder().getEventStore().values()) {
         cityElectricsKassel.getBuilder().load(dataEvent.getBlockId());
      }
      modelMap = cityElectricsKassel.getBuilder().getModel().getModelMap();
<<<<<<< HEAD
      org.fulib.FulibTools.objectDiagrams().dumpSVG("tmp/cityElectricsKassel11_55.svg", modelMap.values());

=======
>>>>>>> d3ea970929d108027c425d763c0cb0d0be7e0237
      open("http://localhost:42002");

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
