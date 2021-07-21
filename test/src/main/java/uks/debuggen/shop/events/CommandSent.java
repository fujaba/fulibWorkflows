package uks.debuggen.shop.events;
import java.util.Objects;

public class CommandSent extends Event
{
   public static final String PROPERTY_TYPE = "type";
   public static final String PROPERTY_L1 = "l1";
   public static final String PROPERTY_PRODUCT = "product";
   public static final String PROPERTY_CUSTOMER = "customer";
   public static final String PROPERTY_ADDRESS = "address";
   public static final String PROPERTY_OK = "ok";
   private String type;
   private String l1;
   private String product;
   private String customer;
   private String address;
   private String ok;

   public String getType()
   {
      return this.type;
   }

   public CommandSent setType(String value)
   {
      if (Objects.equals(value, this.type))
      {
         return this;
      }

      final String oldValue = this.type;
      this.type = value;
      this.firePropertyChange(PROPERTY_TYPE, oldValue, value);
      return this;
   }

   public String getL1()
   {
      return this.l1;
   }

   public CommandSent setL1(String value)
   {
      if (Objects.equals(value, this.l1))
      {
         return this;
      }

      final String oldValue = this.l1;
      this.l1 = value;
      this.firePropertyChange(PROPERTY_L1, oldValue, value);
      return this;
   }

   public String getProduct()
   {
      return this.product;
   }

   public CommandSent setProduct(String value)
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

   public CommandSent setCustomer(String value)
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

   public CommandSent setAddress(String value)
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

   public String getOk()
   {
      return this.ok;
   }

   public CommandSent setOk(String value)
   {
      if (Objects.equals(value, this.ok))
      {
         return this;
      }

      final String oldValue = this.ok;
      this.ok = value;
      this.firePropertyChange(PROPERTY_OK, oldValue, value);
      return this;
   }

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder(super.toString());
      result.append(' ').append(this.getType());
      result.append(' ').append(this.getL1());
      result.append(' ').append(this.getProduct());
      result.append(' ').append(this.getCustomer());
      result.append(' ').append(this.getAddress());
      result.append(' ').append(this.getOk());
      return result.toString();
   }
}
