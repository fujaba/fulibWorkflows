package uks.debuggen.party2.events;
import java.util.Objects;

public class BuildCommand extends Command
{
   public static final String PROPERTY_ITEM = "item";
   public static final String PROPERTY_PRICE = "price";
   public static final String PROPERTY_BUYER = "buyer";
   private String item;
   private String price;
   private String buyer;

   public String getItem()
   {
      return this.item;
   }

   public BuildCommand setItem(String value)
   {
      if (Objects.equals(value, this.item))
      {
         return this;
      }

      final String oldValue = this.item;
      this.item = value;
      this.firePropertyChange(PROPERTY_ITEM, oldValue, value);
      return this;
   }

   public String getPrice()
   {
      return this.price;
   }

   public BuildCommand setPrice(String value)
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

   public String getBuyer()
   {
      return this.buyer;
   }

   public BuildCommand setBuyer(String value)
   {
      if (Objects.equals(value, this.buyer))
      {
         return this;
      }

      final String oldValue = this.buyer;
      this.buyer = value;
      this.firePropertyChange(PROPERTY_BUYER, oldValue, value);
      return this;
   }

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder(super.toString());
      result.append(' ').append(this.getItem());
      result.append(' ').append(this.getPrice());
      result.append(' ').append(this.getBuyer());
      return result.toString();
   }
}
