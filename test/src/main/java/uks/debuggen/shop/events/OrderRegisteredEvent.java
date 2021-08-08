package uks.debuggen.shop.events;
import java.util.Objects;

public class OrderRegisteredEvent extends Event
{
   public static final String PROPERTY_TRIGGER = "trigger";
   public static final String PROPERTY_PRODUCT = "product";
   public static final String PROPERTY_CUSTOMER = "customer";
   public static final String PROPERTY_ADDRESS = "address";
   public static final String PROPERTY_ORDER = "order";
   private String trigger;
   private String product;
   private String customer;
   private String address;
   private String order;

   public String getTrigger()
   {
      return this.trigger;
   }

   public OrderRegisteredEvent setTrigger(String value)
   {
      if (Objects.equals(value, this.trigger))
      {
         return this;
      }

      final String oldValue = this.trigger;
      this.trigger = value;
      this.firePropertyChange(PROPERTY_TRIGGER, oldValue, value);
      return this;
   }

   public String getProduct()
   {
      return this.product;
   }

   public OrderRegisteredEvent setProduct(String value)
   {
      if (Objects.equals(value, this.product))
      {
         return this;
      }

      final String oldValue = this.product;
      this.product = value;
      this.firePropertyChange(PROPERTY_PRODUCT, oldValue, value);
      return this;
   }

   public String getCustomer()
   {
      return this.customer;
   }

   public OrderRegisteredEvent setCustomer(String value)
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

   public OrderRegisteredEvent setAddress(String value)
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

   public String getOrder()
   {
      return this.order;
   }

   public OrderRegisteredEvent setOrder(String value)
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

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder(super.toString());
      result.append(' ').append(this.getOrder());
      result.append(' ').append(this.getProduct());
      result.append(' ').append(this.getCustomer());
      result.append(' ').append(this.getAddress());
      result.append(' ').append(this.getTrigger());
      return result.toString();
   }
}
