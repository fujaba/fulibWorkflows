package uks.debuggen.studyright.events;
import java.util.Objects;

public class TourListEdited extends DataEvent
{
   public static final String PROPERTY_ALTERNATIVES = "alternatives";
   private String alternatives;

   public String getAlternatives()
   {
      return this.alternatives;
   }

   public TourListEdited setAlternatives(String value)
   {
      if (Objects.equals(value, this.alternatives))
      {
         return this;
      }

      final String oldValue = this.alternatives;
      this.alternatives = value;
      this.firePropertyChange(PROPERTY_ALTERNATIVES, oldValue, value);
      return this;
   }

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder(super.toString());
      result.append(' ').append(this.getAlternatives());
      return result.toString();
   }
}
