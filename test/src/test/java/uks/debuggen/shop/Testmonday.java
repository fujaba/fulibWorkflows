package uks.debuggen.shop;
import org.junit.Test;

import java.util.Objects;
import java.beans.PropertyChangeSupport;
import uks.debuggen.shop.events.EventBroker;

public class Testmonday
{

   @Test
   public void monday()
   {
      // start the event broker
      eventBroker = new EventBroker();
      eventBroker.start();
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
