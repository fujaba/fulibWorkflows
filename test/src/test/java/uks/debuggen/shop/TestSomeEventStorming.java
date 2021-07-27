package uks.debuggen.shop;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.fulib.yaml.Yaml;
import org.junit.Test;
import uks.debuggen.shop.events.*;
import java.util.Objects;
import java.beans.PropertyChangeSupport;
import uks.debuggen.shop.Shop.ShopService;
import uks.debuggen.shop.Storage.StorageService;
import uks.debuggen.shop.someservice.someserviceService;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static org.assertj.core.api.Assertions.assertThat;
import static com.codeborne.selenide.Condition.text;

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

   public void publish(Event event)
   {
      String yaml = Yaml.encode(event);

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
   public void UISomeEventStorming()
   {
      // start the event broker
      eventBroker = new EventBroker();
      eventBroker.start();

      // start service
      ShopService shop = new ShopService();
      shop.start();

      // start service
      someserviceService someservice = new someserviceService();
      someservice.start();

      // start service
      StorageService storage = new StorageService();
      storage.start();

      open("http://localhost:42000");
      $("body").shouldHave(text("event broker"));

      SelenideElement pre = $("pre");
      pre.shouldHave(text("http://localhost:42100/apply"));
      pre.shouldHave(text("http://localhost:42002/apply"));
      pre.shouldHave(text("http://localhost:42003/apply"));


      // manuel
      open("http://localhost:42100/page/42");
      pre = $("body");
      pre.shouldHave(text("42"));


      // workflow working smoothly
      // create ProductStored: product stored 12:00
      ProductStored e1200 = new ProductStored();
      e1200.setId("12:00");
      e1200.setBox("box23");
      e1200.setProduct("shoes");
      e1200.setPlace("shelf23");
      publish(e1200);
      open("http://localhost:42000");
      pre = $("#history");
      pre.shouldHave(text("- 12_00:"));

      // check someservice
      open("http://localhost:42002");
      pre = $("#history");
      pre.shouldHave(text("- 12_00:"));
      // check data note 12:01
      pre = $("#data");
      pre.shouldHave(text("- box23:"));
      pre.shouldHave(text("product: shoes"));
      pre.shouldHave(text("place: shelf23"));

      // check Storage
      open("http://localhost:42003");
      pre = $("#history");
      pre.shouldHave(text("- 12_00:"));
      // check data note 12:02
      pre = $("#data");
      pre.shouldHave(text("- box23:"));
      pre.shouldHave(text("product: shoes"));
      pre.shouldHave(text("place: shelf23"));

      // create OrderRegistered: order registered 13:01
      OrderRegistered e1301 = new OrderRegistered();
      e1301.setId("13:01");
      e1301.setTrigger("button OK");
      e1301.setProduct("shoes");
      e1301.setCustomer("Alice");
      e1301.setAddress("Wonderland 1");
      publish(e1301);
      open("http://localhost:42000");
      pre = $("#history");
      pre.shouldHave(text("- 13_01:"));

      // check Shop
      open("http://localhost:42100");
      pre = $("#history");
      pre.shouldHave(text("- 13_01:"));
      // check data note 13:06
      pre = $("#data");
      pre.shouldHave(text("- order1300:"));
      pre.shouldHave(text("state: picking"));

      // check Storage
      open("http://localhost:42003");
      pre = $("#history");
      pre.shouldHave(text("- 13_01:"));
      // check data note 13:04
      pre = $("#data");
      pre.shouldHave(text("- pick1300:"));
      pre.shouldHave(text("order: order1300"));
      pre.shouldHave(text("product: shoes"));
      pre.shouldHave(text("customer: Alice"));
      pre.shouldHave(text("address: \"Wonderland 1\""));
      pre.shouldHave(text("state: todo"));

      // create OrderPicked: order picked 14:00
      OrderPicked e1400 = new OrderPicked();
      e1400.setId("14:00");
      e1400.setPickTask("pick1300");
      e1400.setBox("box23");
      e1400.setUser("Bob");
      publish(e1400);
      open("http://localhost:42000");
      pre = $("#history");
      pre.shouldHave(text("- 14_00:"));

      // check Storage
      open("http://localhost:42003");
      pre = $("#history");
      pre.shouldHave(text("- 14_00:"));
      // check data note 14:01
      pre = $("#data");
      pre.shouldHave(text("- pick1300:"));
      pre.shouldHave(text("state: done"));
      pre.shouldHave(text("box: box23"));
      // check data note 14:02
      pre = $("#data");
      pre.shouldHave(text("- box23:"));
      pre.shouldHave(text("place: shipping"));

      // check Shop
      open("http://localhost:42100");
      pre = $("#history");
      pre.shouldHave(text("- 14_00:"));
      // check data note 14:03
      pre = $("#data");
      pre.shouldHave(text("- order1300:"));
      pre.shouldHave(text("state: shipping"));

      // workflow OrderOutOfStocks
      // create OrderRegistered: order registered 13:11
      OrderRegistered e1311 = new OrderRegistered();
      e1311.setId("13:11");
      e1311.setTrigger("button OK");
      e1311.setProduct("tshirt");
      e1311.setCustomer("Alice");
      e1311.setAddress("Wonderland 1");
      publish(e1311);
      open("http://localhost:42000");
      pre = $("#history");
      pre.shouldHave(text("- 13_11:"));

      // check Shop
      open("http://localhost:42100");
      pre = $("#history");
      pre.shouldHave(text("- 13_11:"));
      // check data note 13:12
      pre = $("#data");
      pre.shouldHave(text("- order1310:"));
      pre.shouldHave(text("state: \"out of stock\""));

      // check Storage
      open("http://localhost:42003");
      pre = $("#history");
      pre.shouldHave(text("- 13_11:"));

      System.out.println();
   }

   @Test
   public void SomeEventStorming()
   {
      // start the event broker
      eventBroker = new EventBroker();
      eventBroker.start();

      // start service
      ShopService shop = new ShopService();
      shop.start();

      // start service
      someserviceService someservice = new someserviceService();
      someservice.start();

      // start service
      StorageService storage = new StorageService();
      storage.start();

      open("http://localhost:42000");
      $("body").shouldHave(text("event broker"));

      SelenideElement pre = $("pre");
      pre.shouldHave(text("http://localhost:42100/apply"));
      pre.shouldHave(text("http://localhost:42002/apply"));
      pre.shouldHave(text("http://localhost:42003/apply"));

      // workflow working smoothly
      // create ProductStored: product stored 12:00
      ProductStored e1200 = new ProductStored();
      e1200.setId("12:00");
      e1200.setBox("box23");
      e1200.setProduct("shoes");
      e1200.setPlace("shelf23");
      publish(e1200);
      open("http://localhost:42000");
      pre = $("#history");
      pre.shouldHave(text("- 12_00:"));

      // check someservice
      open("http://localhost:42002");
      pre = $("#history");
      pre.shouldHave(text("- 12_00:"));
      // check data note 12:01
      pre = $("#data");
      pre.shouldHave(text("- box23:"));
      pre.shouldHave(text("product: shoes"));
      pre.shouldHave(text("place: shelf23"));

      // check Storage
      open("http://localhost:42003");
      pre = $("#history");
      pre.shouldHave(text("- 12_00:"));
      // check data note 12:02
      pre = $("#data");
      pre.shouldHave(text("- box23:"));
      pre.shouldHave(text("product: shoes"));
      pre.shouldHave(text("place: shelf23"));

      // create OrderRegistered: order registered 13:01
      OrderRegistered e1301 = new OrderRegistered();
      e1301.setId("13:01");
      e1301.setTrigger("button OK");
      e1301.setProduct("shoes");
      e1301.setCustomer("Alice");
      e1301.setAddress("Wonderland 1");
      publish(e1301);
      open("http://localhost:42000");
      pre = $("#history");
      pre.shouldHave(text("- 13_01:"));

      // check Shop
      open("http://localhost:42100");
      pre = $("#history");
      pre.shouldHave(text("- 13_01:"));
      // check data note 13:06
      pre = $("#data");
      pre.shouldHave(text("- order1300:"));
      pre.shouldHave(text("state: picking"));

      // check Storage
      open("http://localhost:42003");
      pre = $("#history");
      pre.shouldHave(text("- 13_01:"));
      // check data note 13:04
      pre = $("#data");
      pre.shouldHave(text("- pick1300:"));
      pre.shouldHave(text("order: order1300"));
      pre.shouldHave(text("product: shoes"));
      pre.shouldHave(text("customer: Alice"));
      pre.shouldHave(text("address: \"Wonderland 1\""));
      pre.shouldHave(text("state: todo"));

      // create OrderPicked: order picked 14:00
      OrderPicked e1400 = new OrderPicked();
      e1400.setId("14:00");
      e1400.setPickTask("pick1300");
      e1400.setBox("box23");
      e1400.setUser("Bob");
      publish(e1400);
      open("http://localhost:42000");
      pre = $("#history");
      pre.shouldHave(text("- 14_00:"));

      // check Storage
      open("http://localhost:42003");
      pre = $("#history");
      pre.shouldHave(text("- 14_00:"));
      // check data note 14:01
      pre = $("#data");
      pre.shouldHave(text("- pick1300:"));
      pre.shouldHave(text("state: done"));
      pre.shouldHave(text("box: box23"));
      // check data note 14:02
      pre = $("#data");
      pre.shouldHave(text("- box23:"));
      pre.shouldHave(text("place: shipping"));

      // check Shop
      open("http://localhost:42100");
      pre = $("#history");
      pre.shouldHave(text("- 14_00:"));
      // check data note 14:03
      pre = $("#data");
      pre.shouldHave(text("- order1300:"));
      pre.shouldHave(text("state: shipping"));

      // workflow OrderOutOfStocks
      // create OrderRegistered: order registered 13:11
      OrderRegistered e1311 = new OrderRegistered();
      e1311.setId("13:11");
      e1311.setTrigger("button OK");
      e1311.setProduct("tshirt");
      e1311.setCustomer("Alice");
      e1311.setAddress("Wonderland 1");
      publish(e1311);
      open("http://localhost:42000");
      pre = $("#history");
      pre.shouldHave(text("- 13_11:"));

      // check Shop
      open("http://localhost:42100");
      pre = $("#history");
      pre.shouldHave(text("- 13_11:"));
      // check data note 13:12
      pre = $("#data");
      pre.shouldHave(text("- order1310:"));
      pre.shouldHave(text("state: \"out of stock\""));

      // check Storage
      open("http://localhost:42003");
      pre = $("#history");
      pre.shouldHave(text("- 13_11:"));

      System.out.println();
   }
}
