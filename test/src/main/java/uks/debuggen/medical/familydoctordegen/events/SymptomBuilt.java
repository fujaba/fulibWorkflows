package uks.debuggen.medical.familydoctordegen.events;
import java.util.Objects;

public class SymptomBuilt extends DataEvent
{
   public static final String PROPERTY_NAME = "name";
   public static final String PROPERTY_CONSULTATIONS = "consultations";
   private String name;
   private String consultations;

   public String getName()
   {
      return this.name;
   }

   public SymptomBuilt setName(String value)
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

   public String getConsultations()
   {
      return this.consultations;
   }

   public SymptomBuilt setConsultations(String value)
   {
      if (Objects.equals(value, this.consultations))
      {
         return this;
      }

      final String oldValue = this.consultations;
      this.consultations = value;
      this.firePropertyChange(PROPERTY_CONSULTATIONS, oldValue, value);
      return this;
   }

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder(super.toString());
      result.append(' ').append(this.getName());
      result.append(' ').append(this.getConsultations());
      return result.toString();
   }
}
