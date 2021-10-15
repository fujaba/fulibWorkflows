package uks.debuggen.pm.studyright;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Collections;
import java.util.Collection;
import java.beans.PropertyChangeSupport;

public class Room
{
   public static final String PROPERTY_ID = "id";
   public static final String PROPERTY_TOPIC = "topic";
   public static final String PROPERTY_NEIGHBORS = "neighbors";
   public static final String PROPERTY_STOPS = "stops";
   public static final String PROPERTY_CREDITS = "credits";
   private String id;
   private String topic;
   private List<Room> neighbors;
   protected PropertyChangeSupport listeners;
   private List<Stop> stops;
   private int credits;

   public String getId()
   {
      return this.id;
   }

   public Room setId(String value)
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

   public String getTopic()
   {
      return this.topic;
   }

   public Room setTopic(String value)
   {
      if (Objects.equals(value, this.topic))
      {
         return this;
      }

      final String oldValue = this.topic;
      this.topic = value;
      this.firePropertyChange(PROPERTY_TOPIC, oldValue, value);
      return this;
   }

   public List<Room> getNeighbors()
   {
      return this.neighbors != null ? Collections.unmodifiableList(this.neighbors) : Collections.emptyList();
   }

   public Room withNeighbors(Room value)
   {
      if (this.neighbors == null)
      {
         this.neighbors = new ArrayList<>();
      }
      if (!this.neighbors.contains(value))
      {
         this.neighbors.add(value);
         value.withNeighbors(this);
         this.firePropertyChange(PROPERTY_NEIGHBORS, null, value);
      }
      return this;
   }

   public Room withNeighbors(Room... value)
   {
      for (final Room item : value)
      {
         this.withNeighbors(item);
      }
      return this;
   }

   public Room withNeighbors(Collection<? extends Room> value)
   {
      for (final Room item : value)
      {
         this.withNeighbors(item);
      }
      return this;
   }

   public Room withoutNeighbors(Room value)
   {
      if (this.neighbors != null && this.neighbors.remove(value))
      {
         value.withoutNeighbors(this);
         this.firePropertyChange(PROPERTY_NEIGHBORS, value, null);
      }
      return this;
   }

   public Room withoutNeighbors(Room... value)
   {
      for (final Room item : value)
      {
         this.withoutNeighbors(item);
      }
      return this;
   }

   public Room withoutNeighbors(Collection<? extends Room> value)
   {
      for (final Room item : value)
      {
         this.withoutNeighbors(item);
      }
      return this;
   }

   public List<Stop> getStops()
   {
      return this.stops != null ? Collections.unmodifiableList(this.stops) : Collections.emptyList();
   }

   public Room withStops(Stop value)
   {
      if (this.stops == null)
      {
         this.stops = new ArrayList<>();
      }
      if (!this.stops.contains(value))
      {
         this.stops.add(value);
         value.setRoom(this);
         this.firePropertyChange(PROPERTY_STOPS, null, value);
      }
      return this;
   }

   public Room withStops(Stop... value)
   {
      for (final Stop item : value)
      {
         this.withStops(item);
      }
      return this;
   }

   public Room withStops(Collection<? extends Stop> value)
   {
      for (final Stop item : value)
      {
         this.withStops(item);
      }
      return this;
   }

   public Room withoutStops(Stop value)
   {
      if (this.stops != null && this.stops.remove(value))
      {
         value.setRoom(null);
         this.firePropertyChange(PROPERTY_STOPS, value, null);
      }
      return this;
   }

   public Room withoutStops(Stop... value)
   {
      for (final Stop item : value)
      {
         this.withoutStops(item);
      }
      return this;
   }

   public Room withoutStops(Collection<? extends Stop> value)
   {
      for (final Stop item : value)
      {
         this.withoutStops(item);
      }
      return this;
   }

   public int getCredits()
   {
      return this.credits;
   }

   public Room setCredits(int value)
   {
      if (value == this.credits)
      {
         return this;
      }

      final int oldValue = this.credits;
      this.credits = value;
      this.firePropertyChange(PROPERTY_CREDITS, oldValue, value);
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
      result.append(' ').append(this.getTopic());
      return result.substring(1);
   }

   public void removeYou()
   {
      this.withoutStops(new ArrayList<>(this.getStops()));
      this.withoutNeighbors(new ArrayList<>(this.getNeighbors()));
   }
}
