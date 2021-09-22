package uks.debuggen.microshop.events;
import java.util.Objects;

public class ProductStoredEvent extends Event
{
   public static final String PROPERTY_BARCODE = "barcode";
   public static final String PROPERTY_TYPE = "type";
   private String barcode;
   private String type;

   public String getBarcode()
   {
      return this.barcode;
   }

   public ProductStoredEvent setBarcode(String value)
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

   public String getType()
   {
      return this.type;
   }

   public ProductStoredEvent setType(String value)
   {
      if (Objects.equals(value, this.type))
      {
         return this;
      }

      final String oldValue = this.type;
      this.type = value;
      this.firePropertyChange(PROPERTY_TYPE, oldValue, value);
      return this;
   }

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder(super.toString());
      result.append(' ').append(this.getBarcode());
      result.append(' ').append(this.getType());
      return result.toString();
   }
}
