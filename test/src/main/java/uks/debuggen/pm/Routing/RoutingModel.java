package uks.debuggen.pm.routing;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.beans.PropertyChangeSupport;

public class RoutingModel
{
   public static final String PROPERTY_MODEL_MAP = "modelMap";
   private LinkedHashMap<String, Object> modelMap = new LinkedHashMap<>();
   protected PropertyChangeSupport listeners;

   public LinkedHashMap<String, Object> getModelMap()
   {
      return this.modelMap;
   }

   public RoutingModel setModelMap(LinkedHashMap<String, Object> value)
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

   public Route getOrCreateRoute(String id)
   {
      if (id == null) return null;
      return (Route) modelMap.computeIfAbsent(id, k -> new Route().setId(k));
   }

   public Stop getOrCreateStop(String id)
   {
      if (id == null) return null;
      return (Stop) modelMap.computeIfAbsent(id, k -> new Stop().setId(k));
   }

   public Leg getOrCreateLeg(String id)
   {
      if (id == null) return null;
      return (Leg) modelMap.computeIfAbsent(id, k -> new Leg().setId(k));
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
