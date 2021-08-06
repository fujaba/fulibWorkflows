package uks.debuggen.studyright.events;
import java.util.Objects;

public class TourFoundEvent extends Event
{
   public static final String PROPERTY_TOUR = "tour";
   private String tour;

   public String getTour()
   {
      return this.tour;
   }

   public TourFoundEvent setTour(String value)
   {
      if (Objects.equals(value, this.tour))
      {
         return this;
      }

      final String oldValue = this.tour;
      this.tour = value;
      this.firePropertyChange(PROPERTY_TOUR, oldValue, value);
      return this;
   }

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder(super.toString());
      result.append(' ').append(this.getTour());
      return result.toString();
   }
}
