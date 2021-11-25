package uks.debuggen.microshop.events;
import java.util.Objects;

public class WHProductBuilt extends DataEvent
{
   public static final String PROPERTY_NAME = "name";
   public static final String PROPERTY_AMOUNT = "amount";
   private String name;
   private String amount;

   public String getName()
   {
      return this.name;
   }

   public WHProductBuilt setName(String value)
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

   public String getAmount()
   {
      return this.amount;
   }

   public WHProductBuilt setAmount(String value)
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
      result.append(' ').append(this.getName());
      result.append(' ').append(this.getAmount());
      return result.toString();
   }
}
