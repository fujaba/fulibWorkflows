package uks.debuggen.pm.Routing;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Collections;
import java.util.Collection;
import java.beans.PropertyChangeSupport;

public class Stop
{
   public static final String PROPERTY_ID = "id";
   public static final String PROPERTY_ROUTE_STARTS = "routeStarts";
   public static final String PROPERTY_ROUTE_ENDS = "routeEnds";
   public static final String PROPERTY_DEPARTURE = "departure";
   public static final String PROPERTY_ARRIVAL = "arrival";
   private String id;
   private List<Route> routeStarts;
   private List<Route> routeEnds;
   private Leg departure;
   private Leg arrival;
   protected PropertyChangeSupport listeners;

   public String getId()
   {
      return this.id;
   }

   public Stop setId(String value)
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

   public List<Route> getRouteStarts()
   {
      return this.routeStarts != null ? Collections.unmodifiableList(this.routeStarts) : Collections.emptyList();
   }

   public Stop withRouteStarts(Route value)
   {
      if (this.routeStarts == null)
      {
         this.routeStarts = new ArrayList<>();
      }
      if (!this.routeStarts.contains(value))
      {
         this.routeStarts.add(value);
         value.setStart(this);
         this.firePropertyChange(PROPERTY_ROUTE_STARTS, null, value);
      }
      return this;
   }

   public Stop withRouteStarts(Route... value)
   {
      for (final Route item : value)
      {
         this.withRouteStarts(item);
      }
      return this;
   }

   public Stop withRouteStarts(Collection<? extends Route> value)
   {
      for (final Route item : value)
      {
         this.withRouteStarts(item);
      }
      return this;
   }

   public Stop withoutRouteStarts(Route value)
   {
      if (this.routeStarts != null && this.routeStarts.remove(value))
      {
         value.setStart(null);
         this.firePropertyChange(PROPERTY_ROUTE_STARTS, value, null);
      }
      return this;
   }

   public Stop withoutRouteStarts(Route... value)
   {
      for (final Route item : value)
      {
         this.withoutRouteStarts(item);
      }
      return this;
   }

   public Stop withoutRouteStarts(Collection<? extends Route> value)
   {
      for (final Route item : value)
      {
         this.withoutRouteStarts(item);
      }
      return this;
   }

   public List<Route> getRouteEnds()
   {
      return this.routeEnds != null ? Collections.unmodifiableList(this.routeEnds) : Collections.emptyList();
   }

   public Stop withRouteEnds(Route value)
   {
      if (this.routeEnds == null)
      {
         this.routeEnds = new ArrayList<>();
      }
      if (!this.routeEnds.contains(value))
      {
         this.routeEnds.add(value);
         value.setEnd(this);
         this.firePropertyChange(PROPERTY_ROUTE_ENDS, null, value);
      }
      return this;
   }

   public Stop withRouteEnds(Route... value)
   {
      for (final Route item : value)
      {
         this.withRouteEnds(item);
      }
      return this;
   }

   public Stop withRouteEnds(Collection<? extends Route> value)
   {
      for (final Route item : value)
      {
         this.withRouteEnds(item);
      }
      return this;
   }

   public Stop withoutRouteEnds(Route value)
   {
      if (this.routeEnds != null && this.routeEnds.remove(value))
      {
         value.setEnd(null);
         this.firePropertyChange(PROPERTY_ROUTE_ENDS, value, null);
      }
      return this;
   }

   public Stop withoutRouteEnds(Route... value)
   {
      for (final Route item : value)
      {
         this.withoutRouteEnds(item);
      }
      return this;
   }

   public Stop withoutRouteEnds(Collection<? extends Route> value)
   {
      for (final Route item : value)
      {
         this.withoutRouteEnds(item);
      }
      return this;
   }

   public Leg getDeparture()
   {
      return this.departure;
   }

   public Stop setDeparture(Leg value)
   {
      if (this.departure == value)
      {
         return this;
      }

      final Leg oldValue = this.departure;
      if (this.departure != null)
      {
         this.departure = null;
         oldValue.setFrom(null);
      }
      this.departure = value;
      if (value != null)
      {
         value.setFrom(this);
      }
      this.firePropertyChange(PROPERTY_DEPARTURE, oldValue, value);
      return this;
   }

   public Leg getArrival()
   {
      return this.arrival;
   }

   public Stop setArrival(Leg value)
   {
      if (this.arrival == value)
      {
         return this;
      }

      final Leg oldValue = this.arrival;
      if (this.arrival != null)
      {
         this.arrival = null;
         oldValue.setTo(null);
      }
      this.arrival = value;
      if (value != null)
      {
         value.setTo(this);
      }
      this.firePropertyChange(PROPERTY_ARRIVAL, oldValue, value);
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
      return result.substring(1);
   }

   public void removeYou()
   {
      this.withoutRouteStarts(new ArrayList<>(this.getRouteStarts()));
      this.withoutRouteEnds(new ArrayList<>(this.getRouteEnds()));
      this.setDeparture(null);
      this.setArrival(null);
   }
}
