package uks.debuggen.shop;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.fulib.yaml.Yaml;
import org.junit.Test;
import uks.debuggen.shop.Shop.ShopService;
import uks.debuggen.shop.Storage.StorageService;
import uks.debuggen.shop.events.*;
import uks.debuggen.shop.someservice.someserviceService;
import java.util.Objects;
import java.beans.PropertyChangeSupport;

public class TestOrdering
{
   public static final String PROPERTY_EVENT_BROKER = "eventBroker";
   private EventBroker eventBroker;
   protected PropertyChangeSupport listeners;

   public EventBroker getEventBroker()
   {
      return this.eventBroker;
   }

   public TestOrdering setEventBroker(EventBroker value)
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
   public void ordering_smooth()
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

      // create ProductStored: product stored 12:00
      ProductStored e1200 = new ProductStored();
      e1200.setId("12:00");
      e1200.setBox("box23");
      e1200.setProduct("shoes");
      e1200.setPlace("shelf23");
      publish(e1200);

      // create OrderRegistered: order registered 13:01
      OrderRegistered e1301 = new OrderRegistered();
      e1301.setId("13:01");
      e1301.setTrigger("button OK");
      e1301.setProduct("shoes");
      e1301.setCustomer("Alice");
      e1301.setAddress("Wonderland 1");
      publish(e1301);

      // create OrderApproved: order approved 13:05
      OrderApproved e1305 = new OrderApproved();
      e1305.setId("13:05");
      e1305.setOrder("order1300");
      publish(e1305);

      // create OrderPicked: order picked 14:00
      OrderPicked e1400 = new OrderPicked();
      e1400.setId("14:00");
      e1400.setPickTask("pick1300");
      e1400.setBox("box23");
      e1400.setUser("Bob");
      publish(e1400);
      System.out.println();
   }

   public void publish(Event event)
   {
      String yaml = Yaml.encode(event);

      try {
         HttpResponse<String> response = Unirest.post("http://localhost:42000/publish")
               .body(yaml)
               .asString();
         System.out.println(response.getBody());
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
