package uks.debuggen.medical.marburg.MarburgHealthSystem;
import java.util.Objects;
import java.beans.PropertyChangeSupport;

public class Name
{
   public static final String PROPERTY_ID = "id";
   public static final String PROPERTY_SYMPTOMS = "symptoms";
   public static final String PROPERTY_COUNTER_SYMPTOMS = "counterSymptoms";
   private String id;
   private String symptoms;
   private String counterSymptoms;
   protected PropertyChangeSupport listeners;

   public String getId()
   {
      return this.id;
   }

   public Name setId(String value)
   {
      if (Objects.equals(value, this.id))
      {
         return this;
      }

      final String oldValue = this.id;
      this.id = value;
      this.firePropertyChange(PROPERTY_ID, oldValue, value);
      return this;
   }

   public String getSymptoms()
   {
      return this.symptoms;
   }

   public Name setSymptoms(String value)
   {
      if (Objects.equals(value, this.symptoms))
      {
         return this;
      }

      final String oldValue = this.symptoms;
      this.symptoms = value;
      this.firePropertyChange(PROPERTY_SYMPTOMS, oldValue, value);
      return this;
   }

   public String getCounterSymptoms()
   {
      return this.counterSymptoms;
   }

   public Name setCounterSymptoms(String value)
   {
      if (Objects.equals(value, this.counterSymptoms))
      {
         return this;
      }

      final String oldValue = this.counterSymptoms;
      this.counterSymptoms = value;
      this.firePropertyChange(PROPERTY_COUNTER_SYMPTOMS, oldValue, value);
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
      result.append(' ').append(this.getId());
      result.append(' ').append(this.getSymptoms());
      result.append(' ').append(this.getCounterSymptoms());
      return result.substring(1);
   }
}
