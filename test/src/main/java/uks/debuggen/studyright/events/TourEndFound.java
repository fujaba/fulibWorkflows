package uks.debuggen.studyright.events;
import java.util.Objects;

public class TourEndFound extends Event
{
   public static final String PROPERTY_EVENT = "event";
   public static final String PROPERTY_STOP = "stop";
   public static final String PROPERTY_TOUR = "tour";
   public static final String PROPERTY_TOUR_LIST = "tourList";
   private String event;
   private String stop;
   private String tour;
   private String tourList;

   public String getEvent()
   {
      return this.event;
   }

   public TourEndFound setEvent(String value)
   {
      if (Objects.equals(value, this.event))
      {
         return this;
      }

      final String oldValue = this.event;
      this.event = value;
      this.firePropertyChange(PROPERTY_EVENT, oldValue, value);
      return this;
   }

   public String getStop()
   {
      return this.stop;
   }

   public TourEndFound setStop(String value)
   {
      if (Objects.equals(value, this.stop))
      {
         return this;
      }

      final String oldValue = this.stop;
      this.stop = value;
      this.firePropertyChange(PROPERTY_STOP, oldValue, value);
      return this;
   }

   public String getTour()
   {
      return this.tour;
   }

   public TourEndFound setTour(String value)
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

   public String getTourList()
   {
      return this.tourList;
   }

   public TourEndFound setTourList(String value)
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
      result.append(' ').append(this.getEvent());
      result.append(' ').append(this.getStop());
      result.append(' ').append(this.getTour());
      result.append(' ').append(this.getTourList());
      return result.toString();
   }
}
