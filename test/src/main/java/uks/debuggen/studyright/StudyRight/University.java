package uks.debuggen.studyright.StudyRight;
import java.util.Objects;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Collection;

public class University
{
   public static final String PROPERTY_ID = "id";
   public static final String PROPERTY_ROOMS = "rooms";
   public static final String PROPERTY_PERSONS = "persons";
   private String id;
   protected PropertyChangeSupport listeners;
   private List<Room> rooms;
   private List<Person> persons;

   public String getId()
   {
      return this.id;
   }

   public University setId(String value)
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

   public List<Room> getRooms()
   {
      return this.rooms != null ? Collections.unmodifiableList(this.rooms) : Collections.emptyList();
   }

   public University withRooms(Room value)
   {
      if (this.rooms == null)
      {
         this.rooms = new ArrayList<>();
      }
      if (!this.rooms.contains(value))
      {
         this.rooms.add(value);
         value.setUni(this);
         this.firePropertyChange(PROPERTY_ROOMS, null, value);
      }
      return this;
   }

   public University withRooms(Room... value)
   {
      for (final Room item : value)
      {
         this.withRooms(item);
      }
      return this;
   }

   public University withRooms(Collection<? extends Room> value)
   {
      for (final Room item : value)
      {
         this.withRooms(item);
      }
      return this;
   }

   public University withoutRooms(Room value)
   {
      if (this.rooms != null && this.rooms.remove(value))
      {
         value.setUni(null);
         this.firePropertyChange(PROPERTY_ROOMS, value, null);
      }
      return this;
   }

   public University withoutRooms(Room... value)
   {
      for (final Room item : value)
      {
         this.withoutRooms(item);
      }
      return this;
   }

   public University withoutRooms(Collection<? extends Room> value)
   {
      for (final Room item : value)
      {
         this.withoutRooms(item);
      }
      return this;
   }

   public List<Person> getPersons()
   {
      return this.persons != null ? Collections.unmodifiableList(this.persons) : Collections.emptyList();
   }

   public University withPersons(Person value)
   {
      if (this.persons == null)
      {
         this.persons = new ArrayList<>();
      }
      if (!this.persons.contains(value))
      {
         this.persons.add(value);
         value.setUni(this);
         this.firePropertyChange(PROPERTY_PERSONS, null, value);
      }
      return this;
   }

   public University withPersons(Person... value)
   {
      for (final Person item : value)
      {
         this.withPersons(item);
      }
      return this;
   }

   public University withPersons(Collection<? extends Person> value)
   {
      for (final Person item : value)
      {
         this.withPersons(item);
      }
      return this;
   }

   public University withoutPersons(Person value)
   {
      if (this.persons != null && this.persons.remove(value))
      {
         value.setUni(null);
         this.firePropertyChange(PROPERTY_PERSONS, value, null);
      }
      return this;
   }

   public University withoutPersons(Person... value)
   {
      for (final Person item : value)
      {
         this.withoutPersons(item);
      }
      return this;
   }

   public University withoutPersons(Collection<? extends Person> value)
   {
      for (final Person item : value)
      {
         this.withoutPersons(item);
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
      return result.substring(1);
   }

   public void removeYou()
   {
      this.withoutRooms(new ArrayList<>(this.getRooms()));
      this.withoutPersons(new ArrayList<>(this.getPersons()));
   }
}
