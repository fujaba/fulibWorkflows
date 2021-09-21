package uks.debuggen.microshop.events;
import java.util.Objects;

public class SubmitOrderCommand extends Command
{
   public static final String PROPERTY_PRODUCT = "product";
   public static final String PROPERTY_NAME = "name";
   public static final String PROPERTY_ADDRESS = "address";
   public static final String PROPERTY_TRIGGER = "trigger";
   public static final String PROPERTY_CUSTOMER = "customer";
   private String product;
   private String name;
   private String address;
   private String trigger;
   private String customer;

   public String getProduct()
   {
      return this.product;
   }

   public SubmitOrderCommand setProduct(String value)
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

   public String getName()
   {
      return this.name;
   }

   public SubmitOrderCommand setName(String value)
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

   public String getAddress()
   {
      return this.address;
   }

   public SubmitOrderCommand setAddress(String value)
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

   public String getTrigger()
   {
      return this.trigger;
   }

   public SubmitOrderCommand setTrigger(String value)
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

   public String getCustomer()
   {
      return this.customer;
   }

   public SubmitOrderCommand setCustomer(String value)
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

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder(super.toString());
      result.append(' ').append(this.getProduct());
      result.append(' ').append(this.getName());
      result.append(' ').append(this.getAddress());
      result.append(' ').append(this.getTrigger());
      result.append(' ').append(this.getCustomer());
      return result.toString();
   }
}
