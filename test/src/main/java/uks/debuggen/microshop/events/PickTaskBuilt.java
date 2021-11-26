package uks.debuggen.microshop.events;
import java.util.Objects;

public class PickTaskBuilt extends DataEvent
{
   public static final String PROPERTY_CODE = "code";
   public static final String PROPERTY_PRODUCT = "product";
   public static final String PROPERTY_SHELF = "shelf";
   public static final String PROPERTY_CUSTOMER = "customer";
   public static final String PROPERTY_ADDRESS = "address";
   public static final String PROPERTY_STATE = "state";
   public static final String PROPERTY_FROM = "from";
   public static final String PROPERTY_PALETTE = "palette";
   private String code;
   private String product;
   private String shelf;
   private String customer;
   private String address;
   private String state;
   private String from;
   private String palette;

   public String getCode()
   {
      return this.code;
   }

   public PickTaskBuilt setCode(String value)
   {
      if (Objects.equals(value, this.code))
      {
         return this;
      }

      final String oldValue = this.code;
      this.code = value;
      this.firePropertyChange(PROPERTY_CODE, oldValue, value);
      return this;
   }

   public String getProduct()
   {
      return this.product;
   }

   public PickTaskBuilt setProduct(String value)
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

   public String getShelf()
   {
      return this.shelf;
   }

   public PickTaskBuilt setShelf(String value)
   {
      if (Objects.equals(value, this.shelf))
      {
         return this;
      }

      final String oldValue = this.shelf;
      this.shelf = value;
      this.firePropertyChange(PROPERTY_SHELF, oldValue, value);
      return this;
   }

   public String getCustomer()
   {
      return this.customer;
   }

   public PickTaskBuilt setCustomer(String value)
   {
      if (Objects.equals(value, this.customer))
      {
         return this;
      }

      final String oldValue = this.customer;
      this.customer = value;
      this.firePropertyChange(PROPERTY_CUSTOMER, oldValue, value);
      return this;
   }

   public String getAddress()
   {
      return this.address;
   }

   public PickTaskBuilt setAddress(String value)
   {
      if (Objects.equals(value, this.address))
      {
         return this;
      }

      final String oldValue = this.address;
      this.address = value;
      this.firePropertyChange(PROPERTY_ADDRESS, oldValue, value);
      return this;
   }

   public String getState()
   {
      return this.state;
   }

   public PickTaskBuilt setState(String value)
   {
      if (Objects.equals(value, this.state))
      {
         return this;
      }

      final String oldValue = this.state;
      this.state = value;
      this.firePropertyChange(PROPERTY_STATE, oldValue, value);
      return this;
   }

   public String getFrom()
   {
      return this.from;
   }

   public PickTaskBuilt setFrom(String value)
   {
      if (Objects.equals(value, this.from))
      {
         return this;
      }

      final String oldValue = this.from;
      this.from = value;
      this.firePropertyChange(PROPERTY_FROM, oldValue, value);
      return this;
   }

   public String getPalette()
   {
      return this.palette;
   }

   public PickTaskBuilt setPalette(String value)
   {
      if (Objects.equals(value, this.palette))
      {
         return this;
      }

      final String oldValue = this.palette;
      this.palette = value;
      this.firePropertyChange(PROPERTY_PALETTE, oldValue, value);
      return this;
   }

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder(super.toString());
      result.append(' ').append(this.getCode());
      result.append(' ').append(this.getProduct());
      result.append(' ').append(this.getShelf());
      result.append(' ').append(this.getCustomer());
      result.append(' ').append(this.getAddress());
      result.append(' ').append(this.getState());
      result.append(' ').append(this.getFrom());
      result.append(' ').append(this.getPalette());
      return result.toString();
   }
}
