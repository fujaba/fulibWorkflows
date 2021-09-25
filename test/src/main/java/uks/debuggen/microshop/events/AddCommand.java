package uks.debuggen.microshop.events;
import java.util.Objects;

public class AddCommand extends Command
{
   public static final String PROPERTY_PRODUCT = "product";
   public static final String PROPERTY_PRICE = "price";
   private String product;
   private String price;

   public String getProduct()
   {
      return this.product;
   }

   public AddCommand setProduct(String value)
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

   public String getPrice()
   {
      return this.price;
   }

   public AddCommand setPrice(String value)
   {
      if (Objects.equals(value, this.price))
      {
         return this;
      }

      final String oldValue = this.price;
      this.price = value;
      this.firePropertyChange(PROPERTY_PRICE, oldValue, value);
      return this;
   }

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder(super.toString());
      result.append(' ').append(this.getProduct());
      result.append(' ').append(this.getPrice());
      return result.toString();
   }
}
