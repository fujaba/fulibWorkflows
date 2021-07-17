package org.fulib.workflows;
import java.util.Map;
import java.util.Objects;
import java.beans.PropertyChangeSupport;
import java.util.LinkedHashMap;

public class Note
{
   public static final String PROPERTY_MAP = "map";
   protected PropertyChangeSupport listeners;
   private LinkedHashMap<String, String> map;

   public LinkedHashMap<String, String> getMap()
   {
      return this.map;
   }

   public Note setMap(LinkedHashMap<String, String> value)
   {
      if (Objects.equals(value, this.map))
      {
         return this;
      }

      final LinkedHashMap<String, String> oldValue = this.map;
      this.map = value;
      this.firePropertyChange(PROPERTY_MAP, oldValue, value);
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
}
