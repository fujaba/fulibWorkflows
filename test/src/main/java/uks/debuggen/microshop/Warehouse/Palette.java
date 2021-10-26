package uks.debuggen.microshop.Warehouse;
import java.util.Objects;
import java.beans.PropertyChangeSupport;

public class Palette
{
   public static final String PROPERTY_ID = "id";
   public static final String PROPERTY_BARCODE = "barcode";
   public static final String PROPERTY_PRODUCT = "product";
   public static final String PROPERTY_AMOUNT = "amount";
   public static final String PROPERTY_LOCATION = "location";
   public static final String PROPERTY_CONTENT = "content";
   private String id;
   private String barcode;
   private String product;
   private int amount;
   private String location;
   private String content;
   protected PropertyChangeSupport listeners;

   public String getId()
   {
      return this.id;
   }

   public Palette setId(String value)
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

   public String getBarcode()
   {
      return this.barcode;
   }

   public Palette setBarcode(String value)
   {
      if (Objects.equals(value, this.barcode))
      {
         return this;
      }

      final String oldValue = this.barcode;
      this.barcode = value;
      this.firePropertyChange(PROPERTY_BARCODE, oldValue, value);
      return this;
   }

   public String getProduct()
   {
      return this.product;
   }

   public Palette setProduct(String value)
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

   public int getAmount()
   {
      return this.amount;
   }

   public Palette setAmount(int value)
   {
      if (value == this.amount)
      {
         return this;
      }

      final int oldValue = this.amount;
      this.amount = value;
      this.firePropertyChange(PROPERTY_AMOUNT, oldValue, value);
      return this;
   }

   public String getLocation()
   {
      return this.location;
   }

   public Palette setLocation(String value)
   {
      if (Objects.equals(value, this.location))
      {
         return this;
      }

      final String oldValue = this.location;
      this.location = value;
      this.firePropertyChange(PROPERTY_LOCATION, oldValue, value);
      return this;
   }

   public String getContent()
   {
      return this.content;
   }

   public Palette setContent(String value)
   {
      if (Objects.equals(value, this.content))
      {
         return this;
      }

      final String oldValue = this.content;
      this.content = value;
      this.firePropertyChange(PROPERTY_CONTENT, oldValue, value);
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
      result.append(' ').append(this.getBarcode());
      result.append(' ').append(this.getProduct());
      result.append(' ').append(this.getLocation());
      result.append(' ').append(this.getContent());
      return result.substring(1);
   }
}
