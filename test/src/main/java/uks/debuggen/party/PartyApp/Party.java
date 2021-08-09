package uks.debuggen.party.PartyApp;
import java.util.Objects;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Collection;

public class Party
{
   public static final String PROPERTY_ID = "id";
   public static final String PROPERTY_NAME = "name";
   public static final String PROPERTY_LOCATION = "location";
   public static final String PROPERTY_ITEMS = "items";
   public static final String PROPERTY_GUESTS = "guests";
   private String id;
   private String name;
   private String location;
   protected PropertyChangeSupport listeners;
   private List<Item> items;
   private List<Guest> guests;

   public String getId()
   {
      return this.id;
   }

   public Party setId(String value)
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

   public String getName()
   {
      return this.name;
   }

   public Party setName(String value)
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

   public String getLocation()
   {
      return this.location;
   }

   public Party setLocation(String value)
   {
      if (Objects.equals(value, this.location))
      {
         return this;
      }

      final String oldValue = this.location;
      this.location = value;
      this.firePropertyChange(PROPERTY_LOCATION, oldValue, value);
      return this;
   }

   public List<Item> getItems()
   {
      return this.items != null ? Collections.unmodifiableList(this.items) : Collections.emptyList();
   }

   public Party withItems(Item value)
   {
      if (this.items == null)
      {
         this.items = new ArrayList<>();
      }
      if (!this.items.contains(value))
      {
         this.items.add(value);
         value.setParty(this);
         this.firePropertyChange(PROPERTY_ITEMS, null, value);
      }
      return this;
   }

   public Party withItems(Item... value)
   {
      for (final Item item : value)
      {
         this.withItems(item);
      }
      return this;
   }

   public Party withItems(Collection<? extends Item> value)
   {
      for (final Item item : value)
      {
         this.withItems(item);
      }
      return this;
   }

   public Party withoutItems(Item value)
   {
      if (this.items != null && this.items.remove(value))
      {
         value.setParty(null);
         this.firePropertyChange(PROPERTY_ITEMS, value, null);
      }
      return this;
   }

   public Party withoutItems(Item... value)
   {
      for (final Item item : value)
      {
         this.withoutItems(item);
      }
      return this;
   }

   public Party withoutItems(Collection<? extends Item> value)
   {
      for (final Item item : value)
      {
         this.withoutItems(item);
      }
      return this;
   }

   public List<Guest> getGuests()
   {
      return this.guests != null ? Collections.unmodifiableList(this.guests) : Collections.emptyList();
   }

   public Party withGuests(Guest value)
   {
      if (this.guests == null)
      {
         this.guests = new ArrayList<>();
      }
      if (!this.guests.contains(value))
      {
         this.guests.add(value);
         value.setParty(this);
         this.firePropertyChange(PROPERTY_GUESTS, null, value);
      }
      return this;
   }

   public Party withGuests(Guest... value)
   {
      for (final Guest item : value)
      {
         this.withGuests(item);
      }
      return this;
   }

   public Party withGuests(Collection<? extends Guest> value)
   {
      for (final Guest item : value)
      {
         this.withGuests(item);
      }
      return this;
   }

   public Party withoutGuests(Guest value)
   {
      if (this.guests != null && this.guests.remove(value))
      {
         value.setParty(null);
         this.firePropertyChange(PROPERTY_GUESTS, value, null);
      }
      return this;
   }

   public Party withoutGuests(Guest... value)
   {
      for (final Guest item : value)
      {
         this.withoutGuests(item);
      }
      return this;
   }

   public Party withoutGuests(Collection<? extends Guest> value)
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
      result.append(' ').append(this.getName());
      result.append(' ').append(this.getLocation());
      return result.substring(1);
   }

   public void removeYou()
   {
      this.withoutItems(new ArrayList<>(this.getItems()));
      this.withoutGuests(new ArrayList<>(this.getGuests()));
   }
}
