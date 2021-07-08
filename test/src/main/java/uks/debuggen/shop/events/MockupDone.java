package uks.debuggen.shop.events;
import java.util.Objects;

public class MockupDone extends Event
{
   public static final String PROPERTY_ORDER_REGISTERED = "OrderRegistered";
   public static final String PROPERTY_L1 = "l1";
   public static final String PROPERTY_PRODUCT = "product";
   public static final String PROPERTY_CUSTOMER = "customer";
   public static final String PROPERTY_ADDRESS = "address";
   public static final String PROPERTY_OK = "ok";
   private String OrderRegistered;
   private String l1;
   private String product;
   private String customer;
   private String address;
   private String ok;

   public String getOrderRegistered()
   {
      return this.OrderRegistered;
   }

   public MockupDone setOrderRegistered(String value)
   {
      if (Objects.equals(value, this.OrderRegistered))
      {
         return this;
      }

      final String oldValue = this.OrderRegistered;
      this.OrderRegistered = value;
      this.firePropertyChange(PROPERTY_ORDER_REGISTERED, oldValue, value);
      return this;
   }

   public String getL1()
   {
      return this.l1;
   }

   public MockupDone setL1(String value)
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

   public MockupDone setProduct(String value)
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

   public MockupDone setCustomer(String value)
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

   public MockupDone setAddress(String value)
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

   public MockupDone setOk(String value)
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
      result.append(' ').append(this.getOrderRegistered());
      result.append(' ').append(this.getL1());
      result.append(' ').append(this.getProduct());
      result.append(' ').append(this.getCustomer());
      result.append(' ').append(this.getAddress());
      result.append(' ').append(this.getOk());
      return result.toString();
   }
}
