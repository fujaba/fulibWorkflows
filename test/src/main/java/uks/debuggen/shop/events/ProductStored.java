package uks.debuggen.shop.events;
import java.util.Objects;

public class ProductStored extends Event
{
   public static final String PROPERTY_BOX = "box";
   public static final String PROPERTY_PRODUCT = "product";
   public static final String PROPERTY_PLACE = "place";
   public static final String PROPERTY_USER = "user";
   private String box;
   private String product;
   private String place;
   private String user;

   public String getBox()
   {
      return this.box;
   }

   public ProductStored setBox(String value)
   {
      if (Objects.equals(value, this.box))
      {
         return this;
      }

      final String oldValue = this.box;
      this.box = value;
      this.firePropertyChange(PROPERTY_BOX, oldValue, value);
      return this;
   }

   public String getProduct()
   {
      return this.product;
   }

   public ProductStored setProduct(String value)
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

   public ProductStored setPlace(String value)
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

   public String getUser()
   {
      return this.user;
   }

   public ProductStored setUser(String value)
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
      result.append(' ').append(this.getBox());
      result.append(' ').append(this.getProduct());
      result.append(' ').append(this.getPlace());
      result.append(' ').append(this.getUser());
      return result.toString();
   }
}
