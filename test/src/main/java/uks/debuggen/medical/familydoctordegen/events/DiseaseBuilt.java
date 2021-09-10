package uks.debuggen.medical.familydoctordegen.events;
import java.util.Objects;

public class DiseaseBuilt extends DataEvent
{
   public static final String PROPERTY_NAME = "name";
   public static final String PROPERTY_SYMPTOMS = "symptoms";
   public static final String PROPERTY_COUNTER_SYMPTOMS = "counterSymptoms";
   public static final String PROPERTY_MIGRATED_TO = "migratedTo";
   private String name;
   private String symptoms;
   private String counterSymptoms;
   private String migratedTo;

   public String getName()
   {
      return this.name;
   }

   public DiseaseBuilt setName(String value)
   {
      if (Objects.equals(value, this.name))
      {
         return this;
      }

      final String oldValue = this.name;
      this.name = value;
      this.firePropertyChange(PROPERTY_NAME, oldValue, value);
      return this;
   }

   public String getSymptoms()
   {
      return this.symptoms;
   }

   public DiseaseBuilt setSymptoms(String value)
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

   public DiseaseBuilt setCounterSymptoms(String value)
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

   public String getMigratedTo()
   {
      return this.migratedTo;
   }

   public DiseaseBuilt setMigratedTo(String value)
   {
      if (Objects.equals(value, this.migratedTo))
      {
         return this;
      }

      final String oldValue = this.migratedTo;
      this.migratedTo = value;
      this.firePropertyChange(PROPERTY_MIGRATED_TO, oldValue, value);
      return this;
   }

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder(super.toString());
      result.append(' ').append(this.getName());
      result.append(' ').append(this.getSymptoms());
      result.append(' ').append(this.getCounterSymptoms());
      result.append(' ').append(this.getMigratedTo());
      return result.toString();
   }
}
