package uks.debuggen.shop.events;
import java.util.Objects;

public class StorageData extends Event
{
   public static final String PROPERTY_BOX = "Box";
   public static final String PROPERTY_PRODUCT = "product";
   public static final String PROPERTY_PLACE = "place";
   private String Box;
   private String product;
   private String place;

   public String getBox()
   {
      return this.Box;
   }

   public StorageData setBox(String value)
   {
      if (Objects.equals(value, this.Box))
      {
         return this;
      }

      final String oldValue = this.Box;
      this.Box = value;
      this.firePropertyChange(PROPERTY_BOX, oldValue, value);
      return this;
   }

   public String getProduct()
   {
      return this.product;
   }

   public StorageData setProduct(String value)
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

   public String getPlace()
   {
      return this.place;
   }

   public StorageData setPlace(String value)
   {
      if (Objects.equals(value, this.place))
      {
         return this;
      }

      final String oldValue = this.place;
      this.place = value;
      this.firePropertyChange(PROPERTY_PLACE, oldValue, value);
      return this;
   }

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder(super.toString());
      result.append(' ').append(this.getBox());
      result.append(' ').append(this.getProduct());
      result.append(' ').append(this.getPlace());
      return result.toString();
   }
}
