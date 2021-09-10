package uks.debuggen.medical.familydoctordegen.events;
import java.util.Objects;

public class TreatmentInitiatedEvent extends Event
{
   public static final String PROPERTY_TREATMENT = "treatment";
   private String treatment;

   public String getTreatment()
   {
      return this.treatment;
   }

   public TreatmentInitiatedEvent setTreatment(String value)
   {
      if (Objects.equals(value, this.treatment))
      {
         return this;
      }

      final String oldValue = this.treatment;
      this.treatment = value;
      this.firePropertyChange(PROPERTY_TREATMENT, oldValue, value);
      return this;
   }

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder(super.toString());
      result.append(' ').append(this.getTreatment());
      return result.toString();
   }
}