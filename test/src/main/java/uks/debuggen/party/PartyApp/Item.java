package uks.debuggen.party.PartyApp;
import java.util.Objects;
import java.beans.PropertyChangeSupport;

public class Item
{
   public static final String PROPERTY_ID = "id";
   public static final String PROPERTY_NAME = "name";
   public static final String PROPERTY_BUYER = "buyer";
   public static final String PROPERTY_PARTY = "party";
   public static final String PROPERTY_PRICE = "price";
   private String id;
   private String name;
   private Guest buyer;
   private Party party;
   protected PropertyChangeSupport listeners;
   private double price;

   public String getId()
   {
      return this.id;
   }

   public Item setId(String value)
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

   public Item setName(String value)
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

   public Guest getBuyer()
   {
      return this.buyer;
   }

   public Item setBuyer(Guest value)
   {
      if (this.buyer == value)
      {
         return this;
      }

      final Guest oldValue = this.buyer;
      if (this.buyer != null)
      {
         this.buyer = null;
         oldValue.withoutItems(this);
      }
      this.buyer = value;
      if (value != null)
      {
         value.withItems(this);
      }
      this.firePropertyChange(PROPERTY_BUYER, oldValue, value);
      return this;
   }

   public Party getParty()
   {
      return this.party;
   }

   public Item setParty(Party value)
   {
      if (this.party == value)
      {
         return this;
      }

      final Party oldValue = this.party;
      if (this.party != null)
      {
         this.party = null;
         oldValue.withoutItems(this);
      }
      this.party = value;
      if (value != null)
      {
         value.withItems(this);
      }
      this.firePropertyChange(PROPERTY_PARTY, oldValue, value);
      return this;
   }

   public double getPrice()
   {
      return this.price;
   }

   public Item setPrice(double value)
   {
      if (value == this.price)
      {
         return this;
      }

      final double oldValue = this.price;
      this.price = value;
      this.firePropertyChange(PROPERTY_PRICE, oldValue, value);
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
      this.setBuyer(null);
      this.setParty(null);
   }
}
