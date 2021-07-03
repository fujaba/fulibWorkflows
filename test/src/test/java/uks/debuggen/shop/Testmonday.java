package uks.debuggen.shop;
import org.junit.Test;
import uks.debuggen.shop.Shop.ShopService;
import uks.debuggen.shop.events.*;
import java.util.Objects;
import java.beans.PropertyChangeSupport;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.fulib.yaml.Yaml;
import com.mashape.unirest.http.Unirest;
import uks.debuggen.shop.Storage.StorageService;

public class Testmonday
{

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

      // send user event ProductStored: 12:00
      ProductStored e1200 = new ProductStored();
      e1200.setId("e1200");
      e1200.setBox("box23");
      e1200.setProduct("shoes");
      e1200.setPlace("shelf23");
      e1200.setUser("Bob");
      publish(e1200);
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
   public static final String PROPERTY_EVENT_BROKER = "eventBroker";
   protected PropertyChangeSupport listeners;
   private EventBroker eventBroker;

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
}
