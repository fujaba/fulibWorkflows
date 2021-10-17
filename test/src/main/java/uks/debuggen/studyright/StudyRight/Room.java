package uks.debuggen.studyright.StudyRight;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.beans.PropertyChangeSupport;
import java.util.Collections;
import java.util.Collection;

public class Room
{
   public static final String PROPERTY_ID = "id";
   public static final String PROPERTY_DOORS = "doors";
   public static final String PROPERTY_UNI = "uni";
   public static final String PROPERTY_CREDITS = "credits";
   private String id;
   protected PropertyChangeSupport listeners;
   private List<Room> doors;
   private University uni;
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

   public List<Room> getDoors()
   {
      return this.doors != null ? Collections.unmodifiableList(this.doors) : Collections.emptyList();
   }

   public Room withDoors(Room value)
   {
      if (this.doors == null)
      {
         this.doors = new ArrayList<>();
      }
      if (!this.doors.contains(value))
      {
         this.doors.add(value);
         value.withDoors(this);
         this.firePropertyChange(PROPERTY_DOORS, null, value);
      }
      return this;
   }

   public Room withDoors(Room... value)
   {
      for (final Room item : value)
      {
         this.withDoors(item);
      }
      return this;
   }

   public Room withDoors(Collection<? extends Room> value)
   {
      for (final Room item : value)
      {
         this.withDoors(item);
      }
      return this;
   }

   public Room withoutDoors(Room value)
   {
      if (this.doors != null && this.doors.remove(value))
      {
         value.withoutDoors(this);
         this.firePropertyChange(PROPERTY_DOORS, value, null);
      }
      return this;
   }

   public Room withoutDoors(Room... value)
   {
      for (final Room item : value)
      {
         this.withoutDoors(item);
      }
      return this;
   }

   public Room withoutDoors(Collection<? extends Room> value)
   {
      for (final Room item : value)
      {
         this.withoutDoors(item);
      }
      return this;
   }

   public University getUni()
   {
      return this.uni;
   }

   public Room setUni(University value)
   {
      if (this.uni == value)
      {
         return this;
      }

      final University oldValue = this.uni;
      if (this.uni != null)
      {
         this.uni = null;
         oldValue.withoutRooms(this);
      }
      this.uni = value;
      if (value != null)
      {
         value.withRooms(this);
      }
      this.firePropertyChange(PROPERTY_UNI, oldValue, value);
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
      return result.substring(1);
   }

   public void removeYou()
   {
      this.setUni(null);
      this.withoutDoors(new ArrayList<>(this.getDoors()));
   }
}
