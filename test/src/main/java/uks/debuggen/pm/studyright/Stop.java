package uks.debuggen.pm.studyright;
import java.util.Objects;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Collection;

public class Stop
{
   public static final String PROPERTY_ID = "id";
   public static final String PROPERTY_STUDENT = "student";
   public static final String PROPERTY_ROOM = "room";
   public static final String PROPERTY_PREV = "prev";
   public static final String PROPERTY_NEXT = "next";
   public static final String PROPERTY_MOTIVATION = "motivation";
   private String id;
   protected PropertyChangeSupport listeners;
   private Student student;
   private Room room;
   private Stop prev;
   private List<Stop> next;
   private int motivation;

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

   public Student getStudent()
   {
      return this.student;
   }

   public Stop setStudent(Student value)
   {
      if (this.student == value)
      {
         return this;
      }

      final Student oldValue = this.student;
      if (this.student != null)
      {
         this.student = null;
         oldValue.withoutStops(this);
      }
      this.student = value;
      if (value != null)
      {
         value.withStops(this);
      }
      this.firePropertyChange(PROPERTY_STUDENT, oldValue, value);
      return this;
   }

   public Room getRoom()
   {
      return this.room;
   }

   public Stop setRoom(Room value)
   {
      if (this.room == value)
      {
         return this;
      }

      final Room oldValue = this.room;
      if (this.room != null)
      {
         this.room = null;
         oldValue.withoutStops(this);
      }
      this.room = value;
      if (value != null)
      {
         value.withStops(this);
      }
      this.firePropertyChange(PROPERTY_ROOM, oldValue, value);
      return this;
   }

   public Stop getPrev()
   {
      return this.prev;
   }

   public Stop setPrev(Stop value)
   {
      if (this.prev == value)
      {
         return this;
      }

      final Stop oldValue = this.prev;
      if (this.prev != null)
      {
         this.prev = null;
         oldValue.withoutNext(this);
      }
      this.prev = value;
      if (value != null)
      {
         value.withNext(this);
      }
      this.firePropertyChange(PROPERTY_PREV, oldValue, value);
      return this;
   }

   public List<Stop> getNext()
   {
      return this.next != null ? Collections.unmodifiableList(this.next) : Collections.emptyList();
   }

   public Stop withNext(Stop value)
   {
      if (this.next == null)
      {
         this.next = new ArrayList<>();
      }
      if (!this.next.contains(value))
      {
         this.next.add(value);
         value.setPrev(this);
         this.firePropertyChange(PROPERTY_NEXT, null, value);
      }
      return this;
   }

   public Stop withNext(Stop... value)
   {
      for (final Stop item : value)
      {
         this.withNext(item);
      }
      return this;
   }

   public Stop withNext(Collection<? extends Stop> value)
   {
      for (final Stop item : value)
      {
         this.withNext(item);
      }
      return this;
   }

   public Stop withoutNext(Stop value)
   {
      if (this.next != null && this.next.remove(value))
      {
         value.setPrev(null);
         this.firePropertyChange(PROPERTY_NEXT, value, null);
      }
      return this;
   }

   public Stop withoutNext(Stop... value)
   {
      for (final Stop item : value)
      {
         this.withoutNext(item);
      }
      return this;
   }

   public Stop withoutNext(Collection<? extends Stop> value)
   {
      for (final Stop item : value)
      {
         this.withoutNext(item);
      }
      return this;
   }

   public int getMotivation()
   {
      return this.motivation;
   }

   public Stop setMotivation(int value)
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
      return result.substring(1);
   }

   public void removeYou()
   {
      this.setStudent(null);
      this.setRoom(null);
      this.setPrev(null);
      this.withoutNext(new ArrayList<>(this.getNext()));
   }
}
