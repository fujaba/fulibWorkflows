package uks.fulibgen.shop.events;
import java.util.Objects;

public class OrderPicked extends Event
{
   public static final String PROPERTY_ORDER = "order";
   public static final String PROPERTY_BOX = "box";
   public static final String PROPERTY_CUSTOMER = "customer";
   public static final String PROPERTY_ADDRESS = "address";
   private String order;
   private String box;
   private String customer;
   private String address;

   public String getOrder()
   {
      return this.order;
   }

   public OrderPicked setOrder(String value)
   {
      if (Objects.equals(value, this.order))
      {
         return this;
      }

      final String oldValue = this.order;
      this.order = value;
      this.firePropertyChange(PROPERTY_ORDER, oldValue, value);
      return this;
   }

   public String getBox()
   {
      return this.box;
   }

   public OrderPicked setBox(String value)
   {
      if (Objects.equals(value, this.box))
      {
         return this;
      }

      final String oldValue = this.box;
      this.box = value;
      this.firePropertyChange(PROPERTY_BOX, oldValue, value);
      return this;
   }

   public String getCustomer()
   {
      return this.customer;
   }

   public OrderPicked setCustomer(String value)
   {
      if (Objects.equals(value, this.customer))
      {
         return this;
      }

      final String oldValue = this.customer;
      this.customer = value;
      this.firePropertyChange(PROPERTY_CUSTOMER, oldValue, value);
      return this;
   }

   public String getAddress()
   {
      return this.address;
   }

   public OrderPicked setAddress(String value)
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

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder(super.toString());
      result.append(' ').append(this.getOrder());
      result.append(' ').append(this.getBox());
      result.append(' ').append(this.getCustomer());
      result.append(' ').append(this.getAddress());
      return result.toString();
   }
}
