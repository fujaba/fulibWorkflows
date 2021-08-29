package uks.debuggen.medical.marburg.events;
import java.util.Objects;

public class NameBuilt extends DataEvent
{
   public static final String PROPERTY_SYMPTOMS = "symptoms";
   public static final String PROPERTY_COUNTER_SYMPTOMS = "counterSymptoms";
   private String symptoms;
   private String counterSymptoms;

   public String getSymptoms()
   {
      return this.symptoms;
   }

   public NameBuilt setSymptoms(String value)
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

   public NameBuilt setCounterSymptoms(String value)
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

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder(super.toString());
      result.append(' ').append(this.getSymptoms());
      result.append(' ').append(this.getCounterSymptoms());
      return result.toString();
   }
}
