package heraklitcafe.data;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Collections;
import java.util.Collection;
import java.beans.PropertyChangeSupport;

public class Meal
{
   public static final String PROPERTY_NAME = "name";
   public static final String PROPERTY_ITEMS = "items";
   public static final String PROPERTY_PLACE = "place";
   public static final String PROPERTY_ORDERS = "orders";
   private String name;
   private List<OrderItem> items;
   protected PropertyChangeSupport listeners;
   private Place place;
   private List<Order> orders;

   public String getName()
   {
      return this.name;
   }

   public Meal setName(String value)
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

   public List<OrderItem> getItems()
   {
      return this.items != null ? Collections.unmodifiableList(this.items) : Collections.emptyList();
   }

   public Meal withItems(OrderItem value)
   {
      if (this.items == null)
      {
         this.items = new ArrayList<>();
      }
      if (!this.items.contains(value))
      {
         this.items.add(value);
         value.withMeals(this);
         this.firePropertyChange(PROPERTY_ITEMS, null, value);
      }
      return this;
   }

   public Meal withItems(OrderItem... value)
   {
      for (final OrderItem item : value)
      {
         this.withItems(item);
      }
      return this;
   }

   public Meal withItems(Collection<? extends OrderItem> value)
   {
      for (final OrderItem item : value)
      {
         this.withItems(item);
      }
      return this;
   }

   public Meal withoutItems(OrderItem value)
   {
      if (this.items != null && this.items.remove(value))
      {
         value.withoutMeals(this);
         this.firePropertyChange(PROPERTY_ITEMS, value, null);
      }
      return this;
   }

   public Meal withoutItems(OrderItem... value)
   {
      for (final OrderItem item : value)
      {
         this.withoutItems(item);
      }
      return this;
   }

   public Meal withoutItems(Collection<? extends OrderItem> value)
   {
      for (final OrderItem item : value)
      {
         this.withoutItems(item);
      }
      return this;
   }

   public Place getPlace()
   {
      return this.place;
   }

   public Meal setPlace(Place value)
   {
      if (this.place == value)
      {
         return this;
      }

      final Place oldValue = this.place;
      if (this.place != null)
      {
         this.place = null;
         oldValue.withoutMeals(this);
      }
      this.place = value;
      if (value != null)
      {
         value.withMeals(this);
      }
      this.firePropertyChange(PROPERTY_PLACE, oldValue, value);
      return this;
   }

   public List<Order> getOrders()
   {
      return this.orders != null ? Collections.unmodifiableList(this.orders) : Collections.emptyList();
   }

   public Meal withOrders(Order value)
   {
      if (this.orders == null)
      {
         this.orders = new ArrayList<>();
      }
      if (!this.orders.contains(value))
      {
         this.orders.add(value);
         value.setMeal(this);
         this.firePropertyChange(PROPERTY_ORDERS, null, value);
      }
      return this;
   }

   public Meal withOrders(Order... value)
   {
      for (final Order item : value)
      {
         this.withOrders(item);
      }
      return this;
   }

   public Meal withOrders(Collection<? extends Order> value)
   {
      for (final Order item : value)
      {
         this.withOrders(item);
      }
      return this;
   }

   public Meal withoutOrders(Order value)
   {
      if (this.orders != null && this.orders.remove(value))
      {
         value.setMeal(null);
         this.firePropertyChange(PROPERTY_ORDERS, value, null);
      }
      return this;
   }

   public Meal withoutOrders(Order... value)
   {
      for (final Order item : value)
      {
         this.withoutOrders(item);
      }
      return this;
   }

   public Meal withoutOrders(Collection<? extends Order> value)
   {
      for (final Order item : value)
      {
         this.withoutOrders(item);
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
      result.append(' ').append(this.getName());
      return result.substring(1);
   }

   public void removeYou()
   {
      this.setPlace(null);
      this.withoutItems(new ArrayList<>(this.getItems()));
      this.withoutOrders(new ArrayList<>(this.getOrders()));
   }
}
