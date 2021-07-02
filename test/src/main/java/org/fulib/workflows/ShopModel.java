package org.fulib.workflows;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.beans.PropertyChangeSupport;

public class ShopModel
{
   public static final String PROPERTY_OBJECT_MAP = "objectMap";
   protected PropertyChangeSupport listeners;
   private LinkedHashMap<String, Object> objectMap = new LinkedHashMap<>();

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

   public Order getOrCreateOrder(String id)
   {
      Object obj = objectMap.computeIfAbsent(id, k -> new Order().setId(k));
      return (Order) obj;
   }

   public LinkedHashMap<String, Object> getObjectMap()
   {
      return this.objectMap;
   }

   public ShopModel setObjectMap(LinkedHashMap<String, Object> value)
   {
      if (Objects.equals(value, this.objectMap))
      {
         return this;
      }

      final LinkedHashMap<String, Object> oldValue = this.objectMap;
      this.objectMap = value;
      this.firePropertyChange(PROPERTY_OBJECT_MAP, oldValue, value);
      return this;
   }
}
