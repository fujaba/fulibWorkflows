package uks.fulibgen.shop.events;
import java.util.Objects;

public class CustomerBuilt extends DataEvent
{
   public static final String PROPERTY_ORDERS = "orders";
   private String orders;

   public String getOrders()
   {
      return this.orders;
   }

   public CustomerBuilt setOrders(String value)
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

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder(super.toString());
      result.append(' ').append(this.getOrders());
      return result.toString();
   }
}
