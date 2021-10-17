package uks.debuggen.party.PartyApp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Collections;
import java.util.Collection;
import java.beans.PropertyChangeSupport;

public class Guest
{
   public static final String PROPERTY_ID = "id";
   public static final String PROPERTY_ITEMS = "items";
   public static final String PROPERTY_PARTY = "party";
   public static final String PROPERTY_NAME = "name";
   public static final String PROPERTY_EXPENSES = "expenses";
   private String id;
   private List<Item> items;
   private Party party;
   protected PropertyChangeSupport listeners;
   private String name;
   private double expenses;

   public String getId()
   {
      return this.id;
   }

   public Guest setId(String value)
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

   public List<Item> getItems()
   {
      return this.items != null ? Collections.unmodifiableList(this.items) : Collections.emptyList();
   }

   public Guest withItems(Item value)
   {
      if (this.items == null)
      {
         this.items = new ArrayList<>();
      }
      if (!this.items.contains(value))
      {
         this.items.add(value);
         value.setBuyer(this);
         this.firePropertyChange(PROPERTY_ITEMS, null, value);
      }
      return this;
   }

   public Guest withItems(Item... value)
   {
      for (final Item item : value)
      {
         this.withItems(item);
      }
      return this;
   }

   public Guest withItems(Collection<? extends Item> value)
   {
      for (final Item item : value)
      {
         this.withItems(item);
      }
      return this;
   }

   public Guest withoutItems(Item value)
   {
      if (this.items != null && this.items.remove(value))
      {
         value.setBuyer(null);
         this.firePropertyChange(PROPERTY_ITEMS, value, null);
      }
      return this;
   }

   public Guest withoutItems(Item... value)
   {
      for (final Item item : value)
      {
         this.withoutItems(item);
      }
      return this;
   }

   public Guest withoutItems(Collection<? extends Item> value)
   {
      for (final Item item : value)
      {
         this.withoutItems(item);
      }
      return this;
   }

   public Party getParty()
   {
      return this.party;
   }

   public Guest setParty(Party value)
   {
      if (this.party == value)
      {
         return this;
      }

      final Party oldValue = this.party;
      if (this.party != null)
      {
         this.party = null;
         oldValue.withoutGuests(this);
      }
      this.party = value;
      if (value != null)
      {
         value.withGuests(this);
      }
      this.firePropertyChange(PROPERTY_PARTY, oldValue, value);
      return this;
   }

   public String getName()
   {
      return this.name;
   }

   public Guest setName(String value)
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

   public double getExpenses()
   {
      return this.expenses;
   }

   public Guest setExpenses(double value)
   {
      if (value == this.expenses)
      {
         return this;
      }

      final double oldValue = this.expenses;
      this.expenses = value;
      this.firePropertyChange(PROPERTY_EXPENSES, oldValue, value);
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
      return result.substring(1);
   }

   public void removeYou()
   {
      this.withoutItems(new ArrayList<>(this.getItems()));
      this.setParty(null);
   }
}
