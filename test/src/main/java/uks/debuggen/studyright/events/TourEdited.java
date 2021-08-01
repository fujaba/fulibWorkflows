package uks.debuggen.studyright.events;
import java.util.Objects;

public class TourEdited extends DataEvent
{
   public static final String PROPERTY_STOPS = "stops";
   public static final String PROPERTY_TOUR_LIST = "tourList";
   private String stops;
   private String tourList;

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

   public String getTourList()
   {
      return this.tourList;
   }

   public TourEdited setTourList(String value)
   {
      if (Objects.equals(value, this.tourList))
      {
         return this;
      }

      final String oldValue = this.tourList;
      this.tourList = value;
      this.firePropertyChange(PROPERTY_TOUR_LIST, oldValue, value);
      return this;
   }

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder(super.toString());
      result.append(' ').append(this.getStops());
      result.append(' ').append(this.getTourList());
      return result.toString();
   }
}
