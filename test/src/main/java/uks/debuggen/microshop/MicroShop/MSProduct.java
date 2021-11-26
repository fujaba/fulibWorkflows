package uks.debuggen.microshop.MicroShop;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Collections;
import java.util.Collection;
import java.beans.PropertyChangeSupport;

public class MSProduct
{
   public static final String PROPERTY_ID = "id";
   public static final String PROPERTY_NAME = "name";
   public static final String PROPERTY_STATE = "state";
   public static final String PROPERTY_PRICE = "price";
   public static final String PROPERTY_ORDERS = "orders";
   public static final String PROPERTY_AMOUNT = "amount";
   private String id;
   private String name;
   private String state;
   private String price;
   private List<Order> orders;
   protected PropertyChangeSupport listeners;
   private int amount;

   public String getId()
   {
      return this.id;
   }

   public MSProduct setId(String value)
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

   public MSProduct setName(String value)
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

   public String getState()
   {
      return this.state;
   }

   public MSProduct setState(String value)
   {
      if (Objects.equals(value, this.state))
      {
         return this;
      }

      final String oldValue = this.state;
      this.state = value;
      this.firePropertyChange(PROPERTY_STATE, oldValue, value);
      return this;
   }

   public String getPrice()
   {
      return this.price;
   }

   public MSProduct setPrice(String value)
   {
      if (Objects.equals(value, this.price))
      {
         return this;
      }

      final String oldValue = this.price;
      this.price = value;
      this.firePropertyChange(PROPERTY_PRICE, oldValue, value);
      return this;
   }

   public List<Order> getOrders()
   {
      return this.orders != null ? Collections.unmodifiableList(this.orders) : Collections.emptyList();
   }

   public MSProduct withOrders(Order value)
   {
      if (this.orders == null)
      {
         this.orders = new ArrayList<>();
      }
      if (!this.orders.contains(value))
      {
         this.orders.add(value);
         value.setProduct(this);
         this.firePropertyChange(PROPERTY_ORDERS, null, value);
      }
      return this;
   }

   public MSProduct withOrders(Order... value)
   {
      for (final Order item : value)
      {
         this.withOrders(item);
      }
      return this;
   }

   public MSProduct withOrders(Collection<? extends Order> value)
   {
      for (final Order item : value)
      {
         this.withOrders(item);
      }
      return this;
   }

   public MSProduct withoutOrders(Order value)
   {
      if (this.orders != null && this.orders.remove(value))
      {
         value.setProduct(null);
         this.firePropertyChange(PROPERTY_ORDERS, value, null);
      }
      return this;
   }

   public MSProduct withoutOrders(Order... value)
   {
      for (final Order item : value)
      {
         this.withoutOrders(item);
      }
      return this;
   }

   public MSProduct withoutOrders(Collection<? extends Order> value)
   {
      for (final Order item : value)
      {
         this.withoutOrders(item);
      }
      return this;
   }

   public int getAmount()
   {
      return this.amount;
   }

   public MSProduct setAmount(int value)
   {
      if (value == this.amount)
      {
         return this;
      }

      final int oldValue = this.amount;
      this.amount = value;
      this.firePropertyChange(PROPERTY_AMOUNT, oldValue, value);
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
      result.append(' ').append(this.getState());
      result.append(' ').append(this.getPrice());
      return result.substring(1);
   }

   public void removeYou()
   {
      this.withoutOrders(new ArrayList<>(this.getOrders()));
   }
}
