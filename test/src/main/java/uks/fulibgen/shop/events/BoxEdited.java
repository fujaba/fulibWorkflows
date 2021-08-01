package uks.fulibgen.shop.events;
import java.util.Objects;

public class BoxEdited extends DataEvent
{
   public static final String PROPERTY_PRODUCT = "product";
   public static final String PROPERTY_PLACE = "place";
   private String product;
   private String place;

   public String getProduct()
   {
      return this.product;
   }

   public BoxEdited setProduct(String value)
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

   public BoxEdited setPlace(String value)
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
      result.append(' ').append(this.getProduct());
      result.append(' ').append(this.getPlace());
      return result.toString();
   }
}
