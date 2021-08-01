package uks.debuggen.studyright.StudyRight;
import java.util.Objects;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Collection;

public class Stop
{
   public static final String PROPERTY_ID = "id";
   public static final String PROPERTY_MOTIVATION = "motivation";
   public static final String PROPERTY_ROOM = "room";
   public static final String PROPERTY_PREVIOUS_STOP = "previousStop";
   public static final String PROPERTY_NEXT_STOPS = "nextStops";
   private String id;
   private String motivation;
   protected PropertyChangeSupport listeners;
   private String room;
   private Stop previousStop;
   private List<Stop> nextStops;

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

   public String getMotivation()
   {
      return this.motivation;
   }

   public Stop setMotivation(String value)
   {
      if (Objects.equals(value, this.motivation))
      {
         return this;
      }

      final String oldValue = this.motivation;
      this.motivation = value;
      this.firePropertyChange(PROPERTY_MOTIVATION, oldValue, value);
      return this;
   }

   public String getRoom()
   {
      return this.room;
   }

   public Stop setRoom(String value)
   {
      if (Objects.equals(value, this.room))
      {
         return this;
      }

      final String oldValue = this.room;
      this.room = value;
      this.firePropertyChange(PROPERTY_ROOM, oldValue, value);
      return this;
   }

   public Stop getPreviousStop()
   {
      return this.previousStop;
   }

   public Stop setPreviousStop(Stop value)
   {
      if (this.previousStop == value)
      {
         return this;
      }

      final Stop oldValue = this.previousStop;
      if (this.previousStop != null)
      {
         this.previousStop = null;
         oldValue.withoutNextStops(this);
      }
      this.previousStop = value;
      if (value != null)
      {
         value.withNextStops(this);
      }
      this.firePropertyChange(PROPERTY_PREVIOUS_STOP, oldValue, value);
      return this;
   }

   public List<Stop> getNextStops()
   {
      return this.nextStops != null ? Collections.unmodifiableList(this.nextStops) : Collections.emptyList();
   }

   public Stop withNextStops(Stop value)
   {
      if (this.nextStops == null)
      {
         this.nextStops = new ArrayList<>();
      }
      if (!this.nextStops.contains(value))
      {
         this.nextStops.add(value);
         value.setPreviousStop(this);
         this.firePropertyChange(PROPERTY_NEXT_STOPS, null, value);
      }
      return this;
   }

   public Stop withNextStops(Stop... value)
   {
      for (final Stop item : value)
      {
         this.withNextStops(item);
      }
      return this;
   }

   public Stop withNextStops(Collection<? extends Stop> value)
   {
      for (final Stop item : value)
      {
         this.withNextStops(item);
      }
      return this;
   }

   public Stop withoutNextStops(Stop value)
   {
      if (this.nextStops != null && this.nextStops.remove(value))
      {
         value.setPreviousStop(null);
         this.firePropertyChange(PROPERTY_NEXT_STOPS, value, null);
      }
      return this;
   }

   public Stop withoutNextStops(Stop... value)
   {
      for (final Stop item : value)
      {
         this.withoutNextStops(item);
      }
      return this;
   }

   public Stop withoutNextStops(Collection<? extends Stop> value)
   {
      for (final Stop item : value)
      {
         this.withoutNextStops(item);
      }
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
      result.append(' ').append(this.getMotivation());
      result.append(' ').append(this.getRoom());
      return result.substring(1);
   }

   public void removeYou()
   {
      this.setPreviousStop(null);
      this.withoutNextStops(new ArrayList<>(this.getNextStops()));
   }
}
