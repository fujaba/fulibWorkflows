package uks.debuggen.shop.Shop;
import java.util.Objects;
import java.beans.PropertyChangeSupport;

public class customer
{
   public static final String PROPERTY_ID = "id";
   public static final String PROPERTY_ORDERS = "orders";
   private String id;
   private String orders;
   protected PropertyChangeSupport listeners;

   public String getId()
   {
      return this.id;
   }

   public customer setId(String value)
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

   public String getOrders()
   {
      return this.orders;
   }

   public customer setOrders(String value)
   {
      if (Objects.equals(value, this.orders))
      {
         return this;
      }

      final String oldValue = this.orders;
      this.orders = value;
      this.firePropertyChange(PROPERTY_ORDERS, oldValue, value);
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
      result.append(' ').append(this.getOrders());
      return result.substring(1);
   }
}
