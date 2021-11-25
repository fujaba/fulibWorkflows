package uks.debuggen.microshop.Warehouse;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.beans.PropertyChangeSupport;

public class WarehouseModel
{
   public static final String PROPERTY_MODEL_MAP = "modelMap";
   private LinkedHashMap<String, Object> modelMap = new LinkedHashMap<>();
   protected PropertyChangeSupport listeners;

   public LinkedHashMap<String, Object> getModelMap()
   {
      return this.modelMap;
   }

   public WarehouseModel setModelMap(LinkedHashMap<String, Object> value)
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

   public PickTask getOrCreatePickTask(String id)
   {
      if (id == null) return null;
      return (PickTask) modelMap.computeIfAbsent(id, k -> new PickTask().setId(k));
   }

   public Palette getOrCreatePalette(String id)
   {
      if (id == null) return null;
      return (Palette) modelMap.computeIfAbsent(id, k -> new Palette().setId(k));
   }

   public WHProduct getOrCreateWHProduct(String id)
   {
      if (id == null) return null;
      return (WHProduct) modelMap.computeIfAbsent(id, k -> new WHProduct().setId(k));
   }
}
