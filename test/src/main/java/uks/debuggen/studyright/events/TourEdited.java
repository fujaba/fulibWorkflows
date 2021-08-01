package uks.debuggen.studyright.events;
import java.util.Objects;

public class TourEdited extends DataEvent
{
   public static final String PROPERTY_STOPS = "stops";
   private String stops;

   public String getStops()
   {
      return this.stops;
   }

   public TourEdited setStops(String value)
   {
      if (Objects.equals(value, this.stops))
      {
         return this;
      }

      final String oldValue = this.stops;
      this.stops = value;
      this.firePropertyChange(PROPERTY_STOPS, oldValue, value);
      return this;
   }

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder(super.toString());
      result.append(' ').append(this.getStops());
      return result.toString();
   }
}
