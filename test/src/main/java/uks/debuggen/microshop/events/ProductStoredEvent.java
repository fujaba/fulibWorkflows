package uks.debuggen.microshop.events;
import java.util.Objects;

public class ProductStoredEvent extends Event
{
   public static final String PROPERTY_BARCODE = "barcode";
   public static final String PROPERTY_PRODUCT = "product";
   public static final String PROPERTY_AMOUNT = "amount";
   private String barcode;
   private String product;
   private String amount;

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

   public String getProduct()
   {
      return this.product;
   }

   public ProductStoredEvent setProduct(String value)
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

   public String getAmount()
   {
      return this.amount;
   }

   public ProductStoredEvent setAmount(String value)
   {
      if (Objects.equals(value, this.amount))
      {
         return this;
      }

      final String oldValue = this.amount;
      this.amount = value;
      this.firePropertyChange(PROPERTY_AMOUNT, oldValue, value);
      return this;
   }

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder(super.toString());
      result.append(' ').append(this.getBarcode());
      result.append(' ').append(this.getProduct());
      result.append(' ').append(this.getAmount());
      return result.toString();
   }
}
