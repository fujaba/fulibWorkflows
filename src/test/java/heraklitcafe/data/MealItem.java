package heraklitcafe.data;

import java.beans.PropertyChangeSupport;
import java.util.Objects;

public class MealItem
{
   public static final String PROPERTY_NAME = "name";
   public static final String PROPERTY_PLACE = "place";
   public static final String PROPERTY_ORDER_ITEM = "orderItem";
   public static final String PROPERTY_ORDER = "order";
   private String name;
   private Place place;
   private OrderItem orderItem;
   protected PropertyChangeSupport listeners;
   private Order order;

   public String getName()
   {
      return this.name;
   }

   public MealItem setName(String value)
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

   public MealItem setPlace(Place value)
   {
      if (this.place == value)
      {
         return this;
      }

      final Place oldValue = this.place;
      if (this.place != null)
      {
         this.place = null;
         oldValue.withoutMealItems(this);
      }
      this.place = value;
      if (value != null)
      {
         value.withMealItems(this);
      }
      this.firePropertyChange(PROPERTY_PLACE, oldValue, value);
      return this;
   }

   public OrderItem getOrderItem()
   {
      return this.orderItem;
   }

   public MealItem setOrderItem(OrderItem value)
   {
      if (this.orderItem == value)
      {
         return this;
      }

      final OrderItem oldValue = this.orderItem;
      if (this.orderItem != null)
      {
         this.orderItem = null;
         oldValue.withoutMealItems(this);
      }
      this.orderItem = value;
      if (value != null)
      {
         value.withMealItems(this);
      }
      this.firePropertyChange(PROPERTY_ORDER_ITEM, oldValue, value);
      return this;
   }

   public Order getOrder()
   {
      return this.order;
   }

   public MealItem setOrder(Order value)
   {
      if (this.order == value)
      {
         return this;
      }

      final Order oldValue = this.order;
      if (this.order != null)
      {
         this.order = null;
         oldValue.withoutMealItems(this);
      }
      this.order = value;
      if (value != null)
      {
         value.withMealItems(this);
      }
      this.firePropertyChange(PROPERTY_ORDER, oldValue, value);
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
      this.setOrderItem(null);
      this.setOrder(null);
   }
}
