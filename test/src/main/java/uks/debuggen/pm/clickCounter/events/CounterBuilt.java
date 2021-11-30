package uks.debuggen.pm.clickCounter.events;
import java.util.Objects;

public class CounterBuilt extends DataEvent
{
   public static final String PROPERTY_COUNT = "count";
   private String count;

   public String getCount()
   {
      return this.count;
   }

   public CounterBuilt setCount(String value)
   {
      if (Objects.equals(value, this.count))
      {
         return this;
      }

      final String oldValue = this.count;
      this.count = value;
      this.firePropertyChange(PROPERTY_COUNT, oldValue, value);
      return this;
   }

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder(super.toString());
      result.append(' ').append(this.getCount());
      return result.toString();
   }
}
