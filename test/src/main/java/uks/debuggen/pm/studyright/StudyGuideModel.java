package uks.debuggen.pm.studyright;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.beans.PropertyChangeSupport;

public class StudyGuideModel
{
   public static final String PROPERTY_MODEL_MAP = "modelMap";
   private LinkedHashMap<String, Object> modelMap = new LinkedHashMap<>();
   protected PropertyChangeSupport listeners;

   public LinkedHashMap<String, Object> getModelMap()
   {
      return this.modelMap;
   }

   public StudyGuideModel setModelMap(LinkedHashMap<String, Object> value)
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

   public Student getOrCreateStudent(String id)
   {
      if (id == null) return null;
      return (Student) modelMap.computeIfAbsent(id, k -> new Student().setId(k));
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

   public Stop getOrCreateStop(String id)
   {
      if (id == null) return null;
      return (Stop) modelMap.computeIfAbsent(id, k -> new Stop().setId(k));
   }

   public Room getOrCreateRoom(String id)
   {
      if (id == null) return null;
      return (Room) modelMap.computeIfAbsent(id, k -> new Room().setId(k));
   }
}
