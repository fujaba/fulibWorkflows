package heraklitcafe.data;

import java.beans.PropertyChangeSupport;
import java.util.Objects;

public class Table
{
   public static final String PROPERTY_NAME = "name";
   public static final String PROPERTY_PLACE = "place";
   public static final String PROPERTY_CLIENT = "client";
   public static final String PROPERTY_ORDER = "order";
   private String name;
   private Place place;
   protected PropertyChangeSupport listeners;
   private Client client;
   private Order order;

   public String getName()
   {
      return this.name;
   }

   public Table setName(String value)
   {
      if (Objects.equals(value, this.name))
      {
         return this;
      }

      final String oldValue = this.name;
      this.name = value;
      this.firePropertyChange(PROPERTY_NAME, oldValue, value);
      return this;
   }

   public Place getPlace()
   {
      return this.place;
   }

   public Table setPlace(Place value)
   {
      if (this.place == value)
      {
         return this;
      }

      final Place oldValue = this.place;
      if (this.place != null)
      {
         this.place = null;
         oldValue.withoutTables(this);
      }
      this.place = value;
      if (value != null)
      {
         value.withTables(this);
      }
      this.firePropertyChange(PROPERTY_PLACE, oldValue, value);
      return this;
   }

   public Client getClient()
   {
      return this.client;
   }

   public Table setClient(Client value)
   {
      if (this.client == value)
      {
         return this;
      }

      final Client oldValue = this.client;
      if (this.client != null)
      {
         this.client = null;
         oldValue.setTable(null);
      }
      this.client = value;
      if (value != null)
      {
         value.setTable(this);
      }
      this.firePropertyChange(PROPERTY_CLIENT, oldValue, value);
      return this;
   }

   public Order getOrder()
   {
      return this.order;
   }

   public Table setOrder(Order value)
   {
      if (this.order == value)
      {
         return this;
      }

      final Order oldValue = this.order;
      if (this.order != null)
      {
         this.order = null;
         oldValue.setTable(null);
      }
      this.order = value;
      if (value != null)
      {
         value.setTable(this);
      }
      this.firePropertyChange(PROPERTY_ORDER, oldValue, value);
      return this;
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

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder();
      result.append(' ').append(this.getName());
      return result.substring(1);
   }

   public void removeYou()
   {
      this.setPlace(null);
      this.setClient(null);
      this.setOrder(null);
   }
}
