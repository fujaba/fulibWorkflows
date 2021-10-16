package uks.debuggen.pm.studyright;
import java.util.Objects;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Collection;

public class Student
{
   public static final String PROPERTY_ID = "id";
   public static final String PROPERTY_STOPS = "stops";
   public static final String PROPERTY_ROUTE = "route";
   public static final String PROPERTY_MOTIVATION = "motivation";
   private String id;
   protected PropertyChangeSupport listeners;
   private List<Stop> stops;
   private String route;
   private int motivation;

   public String getId()
   {
      return this.id;
   }

   public Student setId(String value)
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

   public List<Stop> getStops()
   {
      return this.stops != null ? Collections.unmodifiableList(this.stops) : Collections.emptyList();
   }

   public Student withStops(Stop value)
   {
      if (this.stops == null)
      {
         this.stops = new ArrayList<>();
      }
      if (!this.stops.contains(value))
      {
         this.stops.add(value);
         value.setStudent(this);
         this.firePropertyChange(PROPERTY_STOPS, null, value);
      }
      return this;
   }

   public Student withStops(Stop... value)
   {
      for (final Stop item : value)
      {
         this.withStops(item);
      }
      return this;
   }

   public Student withStops(Collection<? extends Stop> value)
   {
      for (final Stop item : value)
      {
         this.withStops(item);
      }
      return this;
   }

   public Student withoutStops(Stop value)
   {
      if (this.stops != null && this.stops.remove(value))
      {
         value.setStudent(null);
         this.firePropertyChange(PROPERTY_STOPS, value, null);
      }
      return this;
   }

   public Student withoutStops(Stop... value)
   {
      for (final Stop item : value)
      {
         this.withoutStops(item);
      }
      return this;
   }

   public Student withoutStops(Collection<? extends Stop> value)
   {
      for (final Stop item : value)
      {
         this.withoutStops(item);
      }
      return this;
   }

   public String getRoute()
   {
      return this.route;
   }

   public Student setRoute(String value)
   {
      if (Objects.equals(value, this.route))
      {
         return this;
      }

      final String oldValue = this.route;
      this.route = value;
      this.firePropertyChange(PROPERTY_ROUTE, oldValue, value);
      return this;
   }

   public int getMotivation()
   {
      return this.motivation;
   }

   public Student setMotivation(int value)
   {
      if (value == this.motivation)
      {
         return this;
      }

      final int oldValue = this.motivation;
      this.motivation = value;
      this.firePropertyChange(PROPERTY_MOTIVATION, oldValue, value);
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
      result.append(' ').append(this.getRoute());
      return result.substring(1);
   }

   public void removeYou()
   {
      this.withoutStops(new ArrayList<>(this.getStops()));
   }
}
