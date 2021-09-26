package uks.debuggen.microshop.events;
import java.util.Objects;

public class ProductOfferedEvent extends Event
{
   public static final String PROPERTY_NAME = "name";
   public static final String PROPERTY_PRICE = "price";
   private String name;
   private String price;

   public String getName()
   {
      return this.name;
   }

   public ProductOfferedEvent setName(String value)
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

   public String getPrice()
   {
      return this.price;
   }

   public ProductOfferedEvent setPrice(String value)
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
      result.append(' ').append(this.getName());
      result.append(' ').append(this.getPrice());
      return result.toString();
   }
}