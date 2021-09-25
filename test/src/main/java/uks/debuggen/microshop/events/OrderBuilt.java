package uks.debuggen.microshop.events;
import java.util.Objects;

public class OrderBuilt extends DataEvent
{
   public static final String PROPERTY_CODE = "code";
   public static final String PROPERTY_PRODUCT = "product";
   public static final String PROPERTY_CUSTOMER = "customer";
   public static final String PROPERTY_ADDRESS = "address";
   public static final String PROPERTY_STATE = "state";
   private String code;
   private String product;
   private String customer;
   private String address;
   private String state;

   public String getCode()
   {
      return this.code;
   }

   public OrderBuilt setCode(String value)
   {
      if (Objects.equals(value, this.code))
      {
         return this;
      }

      final String oldValue = this.code;
      this.code = value;
      this.firePropertyChange(PROPERTY_CODE, oldValue, value);
      return this;
   }

   public String getProduct()
   {
      return this.product;
   }

   public OrderBuilt setProduct(String value)
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

   public OrderBuilt setCustomer(String value)
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

   public OrderBuilt setAddress(String value)
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

   public String getState()
   {
      return this.state;
   }

   public OrderBuilt setState(String value)
   {
      if (Objects.equals(value, this.state))
      {
         return this;
      }

      final String oldValue = this.state;
      this.state = value;
      this.firePropertyChange(PROPERTY_STATE, oldValue, value);
      return this;
   }

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder(super.toString());
      result.append(' ').append(this.getCode());
      result.append(' ').append(this.getProduct());
      result.append(' ').append(this.getCustomer());
      result.append(' ').append(this.getAddress());
      result.append(' ').append(this.getState());
      return result.toString();
   }
}
