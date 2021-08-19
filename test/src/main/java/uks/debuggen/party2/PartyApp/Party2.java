package uks.debuggen.party2.PartyApp;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Collection;
import java.beans.PropertyChangeSupport;
import java.util.Objects;

public class Party2
{
   public static final String PROPERTY_ITEMS = "items";
   public static final String PROPERTY_GUESTS = "guests";
   public static final String PROPERTY_ID = "id";
   public static final String PROPERTY_NAME = "name";
   public static final String PROPERTY_ADDRESS = "address";
   public static final String PROPERTY_REGION = "region";
   public static final String PROPERTY_DATE = "date";
   private List<Item> items;
   private List<Guest> guests;
   protected PropertyChangeSupport listeners;
   private String id;
   private String name;
   private String address;
   private Region region;
   private String date;

   public List<Item> getItems()
   {
      return this.items != null ? Collections.unmodifiableList(this.items) : Collections.emptyList();
   }

   public Party2 withItems(Item value)
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

   public Party2 withItems(Item... value)
   {
      for (final Item item : value)
      {
         this.withItems(item);
      }
      return this;
   }

   public Party2 withItems(Collection<? extends Item> value)
   {
      for (final Item item : value)
      {
         this.withItems(item);
      }
      return this;
   }

   public Party2 withoutItems(Item value)
   {
      if (this.items != null && this.items.remove(value))
      {
         value.setParty(null);
         this.firePropertyChange(PROPERTY_ITEMS, value, null);
      }
      return this;
   }

   public Party2 withoutItems(Item... value)
   {
      for (final Item item : value)
      {
         this.withoutItems(item);
      }
      return this;
   }

   public Party2 withoutItems(Collection<? extends Item> value)
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

   public Party2 withGuests(Guest value)
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

   public Party2 withGuests(Guest... value)
   {
      for (final Guest item : value)
      {
         this.withGuests(item);
      }
      return this;
   }

   public Party2 withGuests(Collection<? extends Guest> value)
   {
      for (final Guest item : value)
      {
         this.withGuests(item);
      }
      return this;
   }

   public Party2 withoutGuests(Guest value)
   {
      if (this.guests != null && this.guests.remove(value))
      {
         value.setParty(null);
         this.firePropertyChange(PROPERTY_GUESTS, value, null);
      }
      return this;
   }

   public Party2 withoutGuests(Guest... value)
   {
      for (final Guest item : value)
      {
         this.withoutGuests(item);
      }
      return this;
   }

   public Party2 withoutGuests(Collection<? extends Guest> value)
   {
      for (final Guest item : value)
      {
         this.withoutGuests(item);
      }
      return this;
   }

   public String getId()
   {
      return this.id;
   }

   public Party2 setId(String value)
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

   public Party2 setName(String value)
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

   public String getAddress()
   {
      return this.address;
   }

   public Party2 setAddress(String value)
   {
      if (Objects.equals(value, this.address))
      {
         return this;
      }

      final String oldValue = this.address;
      this.address = value;
      this.firePropertyChange(PROPERTY_ADDRESS, oldValue, value);
      return this;
   }

   public Region getRegion()
   {
      return this.region;
   }

   public Party2 setRegion(Region value)
   {
      if (this.region == value)
      {
         return this;
      }

      final Region oldValue = this.region;
      if (this.region != null)
      {
         this.region = null;
         oldValue.withoutParties(this);
      }
      this.region = value;
      if (value != null)
      {
         value.withParties(this);
      }
      this.firePropertyChange(PROPERTY_REGION, oldValue, value);
      return this;
   }

   public String getDate()
   {
      return this.date;
   }

   public Party2 setDate(String value)
   {
      if (Objects.equals(value, this.date))
      {
         return this;
      }

      final String oldValue = this.date;
      this.date = value;
      this.firePropertyChange(PROPERTY_DATE, oldValue, value);
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

   public void removeYou()
   {
      this.setRegion(null);
      this.withoutItems(new ArrayList<>(this.getItems()));
      this.withoutGuests(new ArrayList<>(this.getGuests()));
   }

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder();
      result.append(' ').append(this.getId());
      result.append(' ').append(this.getName());
      result.append(' ').append(this.getDate());
      result.append(' ').append(this.getAddress());
      return result.substring(1);
   }
}
