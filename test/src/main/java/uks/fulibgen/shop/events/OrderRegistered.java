package uks.fulibgen.shop.events;
import java.util.Objects;

public class OrderRegistered extends Event
{
   public static final String PROPERTY_PRODUCT = "product";
   public static final String PROPERTY_CUSTOMER = "customer";
   public static final String PROPERTY_ADDRESS = "address";
   public static final String PROPERTY_USER = "user";
   private String product;
   private String customer;
   private String address;
   private String user;

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

   public String getUser()
   {
      return this.user;
   }

   public OrderRegistered setUser(String value)
   {
      if (Objects.equals(value, this.user))
      {
         return this;
      }

      final String oldValue = this.user;
      this.user = value;
      this.firePropertyChange(PROPERTY_USER, oldValue, value);
      return this;
   }

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder(super.toString());
      result.append(' ').append(this.getProduct());
      result.append(' ').append(this.getCustomer());
      result.append(' ').append(this.getAddress());
      result.append(' ').append(this.getUser());
      return result.toString();
   }
}
