package uks.debuggen.microshop;
import com.codeborne.selenide.SelenideElement;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.util.LinkedHashMap;
import org.fulib.yaml.Yaml;
import org.junit.Test;
import uks.debuggen.microshop.events.*;
import static com.codeborne.selenide.Condition.matchText;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import java.util.Objects;
import java.beans.PropertyChangeSupport;
import uks.debuggen.microshop.Warehouse.WarehouseService;

public class TestSomeEventStorming
{
   public static final String PROPERTY_EVENT_BROKER = "eventBroker";
   private EventBroker eventBroker;
   protected PropertyChangeSupport listeners;

   public EventBroker getEventBroker()
   {
      return this.eventBroker;
   }

   public TestSomeEventStorming setEventBroker(EventBroker value)
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
   public void SomeEventStorming()
   {
      // start the event broker
      eventBroker = new EventBroker();
      eventBroker.start();

      // start service
      WarehouseService warehouse = new WarehouseService();
      warehouse.start();
      try {
         Thread.sleep(1500);
      } catch (Exception e) {
      }

      open("http://localhost:42000");
      $("body").shouldHave(text("event broker"));

      SelenideElement pre = $("pre");
      pre.shouldHave(text("http://localhost:42001/apply"));
      LinkedHashMap<String, Object> modelMap;

      // workflow SmoothCase

      // create StoreProductCommand: store product
      StoreProductCommand e1201 = new StoreProductCommand();
      e1201.setId("12:01");
      e1201.setBarcode("b001");
      e1201.setType("red shoes");
      e1201.setLocation("shelf 42");
      publish(e1201);

      open("http://localhost:42000");
      pre = $("#history");
      pre.shouldHave(text("- 12_01:"));

      // check Warehouse
      open("http://localhost:42001");
      pre = $("#history");
      pre.shouldHave(text("- 12_01:"));
      for (DataEvent dataEvent : warehouse.getBuilder().getEventStore().values()) {
         warehouse.getBuilder().load(dataEvent.getBlockId());
      }
      modelMap = warehouse.getBuilder().getModel().getModelMap();
      if (modelMap.values().size() > 0) {
         org.fulib.FulibTools.objectDiagrams().dumpSVG("tmp/warehouse12_01.svg", modelMap.values());
      }

      open("http://localhost:42001");
      // check data note 12:01:01
      pre = $("#data");
      pre.shouldHave(text("- b001:"));
      pre.shouldHave(matchText("barcode:.*b001"));
      pre.shouldHave(matchText("content:.*\"red shoes\""));
      pre.shouldHave(matchText("location:.*\"shelf 42\""));

      // workflow OutOfStock
      try {
         Thread.sleep(3000);
      } catch (Exception e) {
      }
      eventBroker.stop();
      warehouse.stop();

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
