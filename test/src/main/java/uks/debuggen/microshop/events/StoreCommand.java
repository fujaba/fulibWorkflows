package uks.debuggen.microshop.events;
import java.util.Objects;

public class StoreCommand extends Command
{
   public static final String PROPERTY_BARCODE = "barcode";
   public static final String PROPERTY_TYPE = "type";
   public static final String PROPERTY_LOCATION = "location";
   private String barcode;
   private String type;
   private String location;

   public String getBarcode()
   {
      return this.barcode;
   }

   public StoreCommand setBarcode(String value)
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

   public StoreCommand setType(String value)
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

   public String getLocation()
   {
      return this.location;
   }

   public StoreCommand setLocation(String value)
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

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder(super.toString());
      result.append(' ').append(this.getBarcode());
      result.append(' ').append(this.getType());
      result.append(' ').append(this.getLocation());
      return result.toString();
   }
}
