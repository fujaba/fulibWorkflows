package org.fulib.workflows;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Collections;
import java.util.Collection;

public class UserNote extends Note
{
   public static final String PROPERTY_NAME = "name";
   public static final String PROPERTY_INTERACTIONS = "interactions";
   public static final String PROPERTY_EVENT_STORMING_BOARD = "eventStormingBoard";
   private String name;
   private List<UserInteraction> interactions;
   private EventStormingBoard eventStormingBoard;

   public String getName()
   {
      return this.name;
   }

   public UserNote setName(String value)
   {
      if (Objects.equals(value, this.name))
      {
         return this;
      }

      final String oldValue = this.name;
      this.name = value;
      this.firePropertyChange(PROPERTY_NAME, oldValue, value);
      return this;
   }

   public List<UserInteraction> getInteractions()
   {
      return this.interactions != null ? Collections.unmodifiableList(this.interactions) : Collections.emptyList();
   }

   public UserNote withInteractions(UserInteraction value)
   {
      if (this.interactions == null)
      {
         this.interactions = new ArrayList<>();
      }
      if (!this.interactions.contains(value))
      {
         this.interactions.add(value);
         value.setUser(this);
         this.firePropertyChange(PROPERTY_INTERACTIONS, null, value);
      }
      return this;
   }

   public UserNote withInteractions(UserInteraction... value)
   {
      for (final UserInteraction item : value)
      {
         this.withInteractions(item);
      }
      return this;
   }

   public UserNote withInteractions(Collection<? extends UserInteraction> value)
   {
      for (final UserInteraction item : value)
      {
         this.withInteractions(item);
      }
      return this;
   }

   public UserNote withoutInteractions(UserInteraction value)
   {
      if (this.interactions != null && this.interactions.remove(value))
      {
         value.setUser(null);
         this.firePropertyChange(PROPERTY_INTERACTIONS, value, null);
      }
      return this;
   }

   public UserNote withoutInteractions(UserInteraction... value)
   {
      for (final UserInteraction item : value)
      {
         this.withoutInteractions(item);
      }
      return this;
   }

   public UserNote withoutInteractions(Collection<? extends UserInteraction> value)
   {
      for (final UserInteraction item : value)
      {
         this.withoutInteractions(item);
      }
      return this;
   }

   public EventStormingBoard getEventStormingBoard()
   {
      return this.eventStormingBoard;
   }

   public UserNote setEventStormingBoard(EventStormingBoard value)
   {
      if (this.eventStormingBoard == value)
      {
         return this;
      }

      final EventStormingBoard oldValue = this.eventStormingBoard;
      if (this.eventStormingBoard != null)
      {
         this.eventStormingBoard = null;
         oldValue.withoutUsers(this);
      }
      this.eventStormingBoard = value;
      if (value != null)
      {
         value.withUsers(this);
      }
      this.firePropertyChange(PROPERTY_EVENT_STORMING_BOARD, oldValue, value);
      return this;
   }

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder(super.toString());
      result.append(' ').append(this.getName());
      return result.toString();
   }

   public void removeYou()
   {
      this.withoutInteractions(new ArrayList<>(this.getInteractions()));
      this.setEventStormingBoard(null);
   }
}
