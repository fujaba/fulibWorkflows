package uks.debuggen.microshop.events;
import java.util.Objects;

public class ProductBuilt extends DataEvent
{
   public static final String PROPERTY_NAME = "name";
   public static final String PROPERTY_PRICE = "price";
   public static final String PROPERTY_STATE = "state";
   private String name;
   private String price;
   private String state;

   public String getName()
   {
      return this.name;
   }

   public ProductBuilt setName(String value)
   {
      if (Objects.equals(value, this.name))
      {
         return this;
      }

      final String oldValue = this.name;
      this.name = value;
      this.firePropertyChange(PROPERTY_NAME, oldValue, value);
      return this;
   }

   public String getPrice()
   {
      return this.price;
   }

   public ProductBuilt setPrice(String value)
   {
      if (Objects.equals(value, this.price))
      {
         return this;
      }

      final String oldValue = this.price;
      this.price = value;
      this.firePropertyChange(PROPERTY_PRICE, oldValue, value);
      return this;
   }

   public String getState()
   {
      return this.state;
   }

   public ProductBuilt setState(String value)
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

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder(super.toString());
      result.append(' ').append(this.getName());
      result.append(' ').append(this.getState());
      result.append(' ').append(this.getPrice());
      return result.toString();
   }
}
