package uks.debuggen.shop.events;
import java.util.Objects;

public class OrderRegisteredCommand extends Command
{
   public static final String PROPERTY_PRODUCT = "product";
   public static final String PROPERTY_NAME = "name";
   public static final String PROPERTY_ADDRESS = "address";
   private String product;
   private String name;
   private String address;

   public String getProduct()
   {
      return this.product;
   }

   public OrderRegisteredCommand setProduct(String value)
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

   public OrderRegisteredCommand setName(String value)
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

   public OrderRegisteredCommand setAddress(String value)
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
      result.append(' ').append(this.getProduct());
      result.append(' ').append(this.getName());
      result.append(' ').append(this.getAddress());
      return result.toString();
   }
}
