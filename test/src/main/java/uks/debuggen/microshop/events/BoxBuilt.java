package uks.debuggen.microshop.events;
import java.util.Objects;

public class BoxBuilt extends DataEvent
{
   public static final String PROPERTY_BARCODE = "barcode";
   public static final String PROPERTY_CONTENT = "content";
   public static final String PROPERTY_LOCATION = "location";
   private String barcode;
   private String content;
   private String location;

   public String getBarcode()
   {
      return this.barcode;
   }

   public BoxBuilt setBarcode(String value)
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

   public String getContent()
   {
      return this.content;
   }

   public BoxBuilt setContent(String value)
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

   public String getLocation()
   {
      return this.location;
   }

   public BoxBuilt setLocation(String value)
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
      result.append(' ').append(this.getContent());
      result.append(' ').append(this.getLocation());
      return result.toString();
   }
}
