package heraklitcafe.data;

import java.beans.PropertyChangeSupport;
import java.util.Objects;

public class Client
{
   public static final String PROPERTY_NAME = "name";
   public static final String PROPERTY_PLACE = "place";
   public static final String PROPERTY_TABLE = "table";
   private String name;
   private Place place;
   protected PropertyChangeSupport listeners;
   private Table table;

   public String getName()
   {
      return this.name;
   }

   public Client setName(String value)
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

   public Client setPlace(Place value)
   {
      if (this.place == value)
      {
         return this;
      }

      final Place oldValue = this.place;
      if (this.place != null)
      {
         this.place = null;
         oldValue.withoutClients(this);
      }
      this.place = value;
      if (value != null)
      {
         value.withClients(this);
      }
      this.firePropertyChange(PROPERTY_PLACE, oldValue, value);
      return this;
   }

   public Table getTable()
   {
      return this.table;
   }

   public Client setTable(Table value)
   {
      if (this.table == value)
      {
         return this;
      }

      final Table oldValue = this.table;
      if (this.table != null)
      {
         this.table = null;
         oldValue.setClient(null);
      }
      this.table = value;
      if (value != null)
      {
         value.setClient(this);
      }
      this.firePropertyChange(PROPERTY_TABLE, oldValue, value);
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
      this.setTable(null);
   }
}
