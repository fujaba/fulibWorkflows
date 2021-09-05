package uks.debuggen.medical.familydoctordegen.events;
import java.util.Objects;

public class DiagnosisDoneEvent extends Event
{
   public static final String PROPERTY_DISEASE = "disease";
   private String disease;

   public String getDisease()
   {
      return this.disease;
   }

   public DiagnosisDoneEvent setDisease(String value)
   {
      if (Objects.equals(value, this.disease))
      {
         return this;
      }

      final String oldValue = this.disease;
      this.disease = value;
      this.firePropertyChange(PROPERTY_DISEASE, oldValue, value);
      return this;
   }

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder(super.toString());
      result.append(' ').append(this.getDisease());
      return result.toString();
   }
}
