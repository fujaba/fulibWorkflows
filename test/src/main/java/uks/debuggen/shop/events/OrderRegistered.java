package uks.debuggen.shop.events;
import java.util.Objects;

public class OrderRegistered extends Event
{
   public static final String PROPERTY_PRODUCT = "product";
   public static final String PROPERTY_CUSTOMER = "customer";
   public static final String PROPERTY_ADDRESS = "address";
   public static final String PROPERTY_EVENT = "event";
   public static final String PROPERTY_TRIGGER = "trigger";
   public static final String PROPERTY_NAME = "name";
   private String product;
   private String customer;
   private String address;
   private String event;
   private String trigger;
   private String name;

   public String getProduct()
   {
      return this.product;
   }

   public OrderRegistered setProduct(String value)
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

   public OrderRegistered setCustomer(String value)
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

   public OrderRegistered setAddress(String value)
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

   public String getEvent()
   {
      return this.event;
   }

   public OrderRegistered setEvent(String value)
   {
      if (Objects.equals(value, this.event))
      {
         return this;
      }

      final String oldValue = this.event;
      this.event = value;
      this.firePropertyChange(PROPERTY_EVENT, oldValue, value);
      return this;
   }

   public String getTrigger()
   {
      return this.trigger;
   }

   public OrderRegistered setTrigger(String value)
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

   public String getName()
   {
      return this.name;
   }

   public OrderRegistered setName(String value)
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

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder(super.toString());
      result.append(' ').append(this.getEvent());
      result.append(' ').append(this.getProduct());
      result.append(' ').append(this.getName());
      result.append(' ').append(this.getAddress());
      result.append(' ').append(this.getTrigger());
      result.append(' ').append(this.getCustomer());
      return result.toString();
   }
}
