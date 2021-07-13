package uks.debuggen.shop.Storage;
import java.util.Objects;
import java.beans.PropertyChangeSupport;
import java.util.LinkedHashMap;

public class StorageModel
{
   public static final String PROPERTY_MODEL_MAP = "modelMap";
   private LinkedHashMap<String, Object> modelMap = new LinkedHashMap<>();
   protected PropertyChangeSupport listeners;

   public LinkedHashMap<String, Object> getModelMap()
   {
      return this.modelMap;
   }

   public StorageModel setModelMap(LinkedHashMap<String, Object> value)
   {
      if (Objects.equals(value, this.modelMap))
      {
         return this;
      }

      final LinkedHashMap<String, Object> oldValue = this.modelMap;
      this.modelMap = value;
      this.firePropertyChange(PROPERTY_MODEL_MAP, oldValue, value);
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

   public box getOrCreatebox(String id)
   {
      return (box) modelMap.computeIfAbsent(id, k -> new box().setId(k));
   }

   public pickTask getOrCreatepickTask(String id)
   {
      return (pickTask) modelMap.computeIfAbsent(id, k -> new pickTask().setId(k));
   }
}
