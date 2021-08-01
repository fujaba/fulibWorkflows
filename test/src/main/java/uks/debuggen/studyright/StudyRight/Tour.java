package uks.debuggen.studyright.StudyRight;
import java.util.Objects;
import java.beans.PropertyChangeSupport;

public class Tour
{
   public static final String PROPERTY_ID = "id";
   public static final String PROPERTY_STOPS = "stops";
   public static final String PROPERTY_TOUR_LIST = "tourList";
   private String id;
   private String stops;
   private TourList tourList;
   protected PropertyChangeSupport listeners;

   public String getId()
   {
      return this.id;
   }

   public Tour setId(String value)
   {
      if (Objects.equals(value, this.id))
      {
         return this;
      }

      final String oldValue = this.id;
      this.id = value;
      this.firePropertyChange(PROPERTY_ID, oldValue, value);
      return this;
   }

   public String getStops()
   {
      return this.stops;
   }

   public Tour setStops(String value)
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

   public TourList getTourList()
   {
      return this.tourList;
   }

   public Tour setTourList(TourList value)
   {
      if (this.tourList == value)
      {
         return this;
      }

      final TourList oldValue = this.tourList;
      if (this.tourList != null)
      {
         this.tourList = null;
         oldValue.withoutAlternatives(this);
      }
      this.tourList = value;
      if (value != null)
      {
         value.withAlternatives(this);
      }
      this.firePropertyChange(PROPERTY_TOUR_LIST, oldValue, value);
      return this;
   }

   public boolean firePropertyChange(String propertyName, Object oldValue, Object newValue)
   {
      if (this.listeners != null)
      {
         this.listeners.firePropertyChange(propertyName, oldValue, newValue);
         return true;
      }
      return false;
   }

   public PropertyChangeSupport listeners()
   {
      if (this.listeners == null)
      {
         this.listeners = new PropertyChangeSupport(this);
      }
      return this.listeners;
   }

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder();
      result.append(' ').append(this.getId());
      result.append(' ').append(this.getStops());
      return result.substring(1);
   }

   public void removeYou()
   {
      this.setTourList(null);
   }
}
