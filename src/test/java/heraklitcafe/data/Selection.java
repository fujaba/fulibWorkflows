package heraklitcafe.data;

import java.beans.PropertyChangeSupport;
import java.util.*;

public class Selection
{
   public static final String PROPERTY_NAME = "name";
   public static final String PROPERTY_PLACE = "place";
   public static final String PROPERTY_ORDERS = "orders";
   public static final String PROPERTY_ITEMS = "items";
   private String name;
   private Place place;
   private List<Order> orders;
   protected PropertyChangeSupport listeners;
   private List<OrderItem> items;

   public String getName()
   {
      return this.name;
   }

   public Selection setName(String value)
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

   public Place getPlace()
   {
      return this.place;
   }

   public Selection setPlace(Place value)
   {
      if (this.place == value)
      {
         return this;
      }

      final Place oldValue = this.place;
      if (this.place != null)
      {
         this.place = null;
         oldValue.withoutSelections(this);
      }
      this.place = value;
      if (value != null)
      {
         value.withSelections(this);
      }
      this.firePropertyChange(PROPERTY_PLACE, oldValue, value);
      return this;
   }

   public List<Order> getOrders()
   {
      return this.orders != null ? Collections.unmodifiableList(this.orders) : Collections.emptyList();
   }

   public Selection withOrders(Order value)
   {
      if (this.orders == null)
      {
         this.orders = new ArrayList<>();
      }
      if (!this.orders.contains(value))
      {
         this.orders.add(value);
         value.setSelection(this);
         this.firePropertyChange(PROPERTY_ORDERS, null, value);
      }
      return this;
   }

   public Selection withOrders(Order... value)
   {
      for (final Order item : value)
      {
         this.withOrders(item);
      }
      return this;
   }

   public Selection withOrders(Collection<? extends Order> value)
   {
      for (final Order item : value)
      {
         this.withOrders(item);
      }
      return this;
   }

   public Selection withoutOrders(Order value)
   {
      if (this.orders != null && this.orders.remove(value))
      {
         value.setSelection(null);
         this.firePropertyChange(PROPERTY_ORDERS, value, null);
      }
      return this;
   }

   public Selection withoutOrders(Order... value)
   {
      for (final Order item : value)
      {
         this.withoutOrders(item);
      }
      return this;
   }

   public Selection withoutOrders(Collection<? extends Order> value)
   {
      for (final Order item : value)
      {
         this.withoutOrders(item);
      }
      return this;
   }

   public List<OrderItem> getItems()
   {
      return this.items != null ? Collections.unmodifiableList(this.items) : Collections.emptyList();
   }

   public Selection withItems(OrderItem value)
   {
      if (this.items == null)
      {
         this.items = new ArrayList<>();
      }
      if (!this.items.contains(value))
      {
         this.items.add(value);
         value.withSelections(this);
         this.firePropertyChange(PROPERTY_ITEMS, null, value);
      }
      return this;
   }

   public Selection withItems(OrderItem... value)
   {
      for (final OrderItem item : value)
      {
         this.withItems(item);
      }
      return this;
   }

   public Selection withItems(Collection<? extends OrderItem> value)
   {
      for (final OrderItem item : value)
      {
         this.withItems(item);
      }
      return this;
   }

   public Selection withoutItems(OrderItem value)
   {
      if (this.items != null && this.items.remove(value))
      {
         value.withoutSelections(this);
         this.firePropertyChange(PROPERTY_ITEMS, value, null);
      }
      return this;
   }

   public Selection withoutItems(OrderItem... value)
   {
      for (final OrderItem item : value)
      {
         this.withoutItems(item);
      }
      return this;
   }

   public Selection withoutItems(Collection<? extends OrderItem> value)
   {
      for (final OrderItem item : value)
      {
         this.withoutItems(item);
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
