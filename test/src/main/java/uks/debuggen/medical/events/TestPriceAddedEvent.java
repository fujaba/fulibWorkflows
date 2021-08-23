package uks.debuggen.medical.events;
import java.util.Objects;

public class TestPriceAddedEvent extends Event
{
   public static final String PROPERTY_TEST = "test";
   public static final String PROPERTY_PRICE = "price";
   private String test;
   private String price;

   public String getTest()
   {
      return this.test;
   }

   public TestPriceAddedEvent setTest(String value)
   {
      if (Objects.equals(value, this.test))
      {
         return this;
      }

      final String oldValue = this.test;
      this.test = value;
      this.firePropertyChange(PROPERTY_TEST, oldValue, value);
      return this;
   }

   public String getPrice()
   {
      return this.price;
   }

   public TestPriceAddedEvent setPrice(String value)
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

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder(super.toString());
      result.append(' ').append(this.getTest());
      result.append(' ').append(this.getPrice());
      return result.toString();
   }
}
