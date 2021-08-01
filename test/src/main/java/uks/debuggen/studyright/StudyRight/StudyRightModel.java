package uks.debuggen.studyright.StudyRight;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.beans.PropertyChangeSupport;

public class StudyRightModel
{
   public static final String PROPERTY_MODEL_MAP = "modelMap";
   private LinkedHashMap<String, Object> modelMap = new LinkedHashMap<>();
   protected PropertyChangeSupport listeners;

   public LinkedHashMap<String, Object> getModelMap()
   {
      return this.modelMap;
   }

   public StudyRightModel setModelMap(LinkedHashMap<String, Object> value)
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

   public Room getOrCreateRoom(String id)
   {
      if (id == null) return null;
      return (Room) modelMap.computeIfAbsent(id, k -> new Room().setId(k));
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

   public University getOrCreateUniversity(String id)
   {
      if (id == null) return null;
      return (University) modelMap.computeIfAbsent(id, k -> new University().setId(k));
   }

   public Stop getOrCreateStop(String id)
   {
      if (id == null) return null;
      return (Stop) modelMap.computeIfAbsent(id, k -> new Stop().setId(k));
   }

   public Tour getOrCreateTour(String id)
   {
      if (id == null) return null;
      return (Tour) modelMap.computeIfAbsent(id, k -> new Tour().setId(k));
   }

   public TourList getOrCreateTourList(String id)
   {
      if (id == null) return null;
      return (TourList) modelMap.computeIfAbsent(id, k -> new TourList().setId(k));
   }
}
