package uks.debuggen.page.events;
import java.util.Objects;

public class ProductSelected extends Event
{
   public static final String PROPERTY_EVENT = "event";
   private String event;

   public String getEvent()
   {
      return this.event;
   }

   public ProductSelected setEvent(String value)
   {
      if (Objects.equals(value, this.event))
      {
         return this;
      }

      final String oldValue = this.event;
      this.event = value;
      this.firePropertyChange(PROPERTY_EVENT, oldValue, value);
      return this;
   }

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder(super.toString());
      result.append(' ').append(this.getEvent());
      return result.toString();
   }
}