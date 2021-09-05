package uks.debuggen.medical.familydoctordegen.DocMedical;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.beans.PropertyChangeSupport;

public class DocMedicalModel
{
   public static final String PROPERTY_MODEL_MAP = "modelMap";
   private LinkedHashMap<String, Object> modelMap = new LinkedHashMap<>();
   protected PropertyChangeSupport listeners;

   public LinkedHashMap<String, Object> getModelMap()
   {
      return this.modelMap;
   }

   public DocMedicalModel setModelMap(LinkedHashMap<String, Object> value)
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

   public Patient getOrCreatePatient(String id)
   {
      if (id == null) return null;
      return (Patient) modelMap.computeIfAbsent(id, k -> new Patient().setId(k));
   }

   public Consultation getOrCreateConsultation(String id)
   {
      if (id == null) return null;
      return (Consultation) modelMap.computeIfAbsent(id, k -> new Consultation().setId(k));
   }

   public Symptom getOrCreateSymptom(String id)
   {
      if (id == null) return null;
      return (Symptom) modelMap.computeIfAbsent(id, k -> new Symptom().setId(k));
   }

   public Test getOrCreateTest(String id)
   {
      if (id == null) return null;
      return (Test) modelMap.computeIfAbsent(id, k -> new Test().setId(k));
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

   public Disease getOrCreateDisease(String id)
   {
      if (id == null) return null;
      return (Disease) modelMap.computeIfAbsent(id, k -> new Disease().setId(k));
   }
}
