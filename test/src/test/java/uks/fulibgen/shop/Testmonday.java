package uks.fulibgen.shop;
import org.junit.Test;
import uks.fulibgen.shop.events.EventBroker;
import java.util.Objects;
import java.beans.PropertyChangeSupport;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.fulib.yaml.Yaml;
import uks.fulibgen.shop.events.*;
import uks.fulibgen.shop.Shop.ShopService;
import uks.fulibgen.shop.Storage.StorageService;

public class Testmonday
{
   public static final String PROPERTY_EVENT_BROKER = "eventBroker";
   private EventBroker eventBroker;
   protected PropertyChangeSupport listeners;

   public EventBroker getEventBroker()
   {
      return this.eventBroker;
   }

   public Testmonday setEventBroker(EventBroker value)
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
   public void monday()
   {
      // start the event broker
      eventBroker = new EventBroker();
      eventBroker.start();

      // start service
      ShopService shop = new ShopService();
      shop.start();

      // start service
      StorageService storage = new StorageService();
      storage.start();

      // create ProductStored: 12:00
      ProductStored e1200 = new ProductStored();
      e1200.setId("12:00");
      e1200.setBox("box23");
      e1200.setProduct("shoes");
      e1200.setPlace("shelf23");
      e1200.setUser("Bob");
      publish(e1200);

      // create OrderRegistered: 13:00
      OrderRegistered e1300 = new OrderRegistered();
      e1300.setId("13:00");
      e1300.setProduct("shoes");
      e1300.setCustomer("Alice");
      e1300.setAddress("Wonderland 1");
      e1300.setUser("Alice");
      publish(e1300);

      // create OrderPicked: 14:00
      OrderPicked e1400 = new OrderPicked();
      e1400.setId("14:00");
      e1400.setPickTask("pick1300");
      e1400.setBox("box23");
      e1400.setUser("Bob");
      publish(e1400);
      System.out.println();
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
}
