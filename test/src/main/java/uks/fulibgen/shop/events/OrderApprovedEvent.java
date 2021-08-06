package uks.fulibgen.shop.events;
import java.util.Objects;

public class OrderApprovedEvent extends Event
{
   public static final String PROPERTY_ORDER = "order";
   private String order;

   public String getOrder()
   {
      return this.order;
   }

   public OrderApprovedEvent setOrder(String value)
   {
      if (Objects.equals(value, this.order))
      {
         return this;
      }

      final String oldValue = this.order;
      this.order = value;
      this.firePropertyChange(PROPERTY_ORDER, oldValue, value);
      return this;
   }

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder(super.toString());
      result.append(' ').append(this.getOrder());
      return result.toString();
   }
}
