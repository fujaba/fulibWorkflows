package uks.debuggen.pm.party;
import java.util.Objects;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Collection;

public class PartyApp
{
   public static final String PROPERTY_ID = "id";
   public static final String PROPERTY_PARTIES = "parties";
   public static final String PROPERTY_GUESTS = "guests";
   private String id;
   protected PropertyChangeSupport listeners;
   private List<Party> parties;
   private List<Guest> guests;

   public String getId()
   {
      return this.id;
   }

   public PartyApp setId(String value)
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

   public List<Party> getParties()
   {
      return this.parties != null ? Collections.unmodifiableList(this.parties) : Collections.emptyList();
   }

   public PartyApp withParties(Party value)
   {
      if (this.parties == null)
      {
         this.parties = new ArrayList<>();
      }
      if (!this.parties.contains(value))
      {
         this.parties.add(value);
         value.setRoot(this);
         this.firePropertyChange(PROPERTY_PARTIES, null, value);
      }
      return this;
   }

   public PartyApp withParties(Party... value)
   {
      for (final Party item : value)
      {
         this.withParties(item);
      }
      return this;
   }

   public PartyApp withParties(Collection<? extends Party> value)
   {
      for (final Party item : value)
      {
         this.withParties(item);
      }
      return this;
   }

   public PartyApp withoutParties(Party value)
   {
      if (this.parties != null && this.parties.remove(value))
      {
         value.setRoot(null);
         this.firePropertyChange(PROPERTY_PARTIES, value, null);
      }
      return this;
   }

   public PartyApp withoutParties(Party... value)
   {
      for (final Party item : value)
      {
         this.withoutParties(item);
      }
      return this;
   }

   public PartyApp withoutParties(Collection<? extends Party> value)
   {
      for (final Party item : value)
      {
         this.withoutParties(item);
      }
      return this;
   }

   public List<Guest> getGuests()
   {
      return this.guests != null ? Collections.unmodifiableList(this.guests) : Collections.emptyList();
   }

   public PartyApp withGuests(Guest value)
   {
      if (this.guests == null)
      {
         this.guests = new ArrayList<>();
      }
      if (!this.guests.contains(value))
      {
         this.guests.add(value);
         value.setApp(this);
         this.firePropertyChange(PROPERTY_GUESTS, null, value);
      }
      return this;
   }

   public PartyApp withGuests(Guest... value)
   {
      for (final Guest item : value)
      {
         this.withGuests(item);
      }
      return this;
   }

   public PartyApp withGuests(Collection<? extends Guest> value)
   {
      for (final Guest item : value)
      {
         this.withGuests(item);
      }
      return this;
   }

   public PartyApp withoutGuests(Guest value)
   {
      if (this.guests != null && this.guests.remove(value))
      {
         value.setApp(null);
         this.firePropertyChange(PROPERTY_GUESTS, value, null);
      }
      return this;
   }

   public PartyApp withoutGuests(Guest... value)
   {
      for (final Guest item : value)
      {
         this.withoutGuests(item);
      }
      return this;
   }

   public PartyApp withoutGuests(Collection<? extends Guest> value)
   {
      for (final Guest item : value)
      {
         this.withoutGuests(item);
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
      this.withoutGuests(new ArrayList<>(this.getGuests()));
      this.withoutParties(new ArrayList<>(this.getParties()));
   }
}
