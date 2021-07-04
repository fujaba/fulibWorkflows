package uks.debuggen.shop.Storage;
import java.util.Objects;
import java.beans.PropertyChangeSupport;

public class Box
{
   public static final String PROPERTY_ID = "id";
   public static final String PROPERTY_PRODUCT = "product";
   public static final String PROPERTY_PLACE = "place";
   private String id;
   protected PropertyChangeSupport listeners;
   private String product;
   private String place;

   public String getId()
   {
      return this.id;
   }

   public Box setId(String value)
   {
      if (Objects.equals(value, this.id))
      {
         return this;
      }

      final String oldValue = this.id;
      this.id = value;
      this.firePropertyChange(PROPERTY_ID, oldValue, value);
      return this;
   }

   public String getProduct()
   {
      return this.product;
   }

   public Box setProduct(String value)
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

   public Box setPlace(String value)
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

   public boolean firePropertyChange(String propertyName, Object oldValue, Object newValue)
   {
      if (this.listeners != null)
      {
         this.listeners.firePropertyChange(propertyName, oldValue, newValue);
         return true;
      }
      return false;
   }

   public PropertyChangeSupport listeners()
   {
      if (this.listeners == null)
      {
         this.listeners = new PropertyChangeSupport(this);
      }
      return this.listeners;
   }

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder();
      result.append(' ').append(this.getId());
      result.append(' ').append(this.getProduct());
      result.append(' ').append(this.getPlace());
      return result.substring(1);
   }
}
