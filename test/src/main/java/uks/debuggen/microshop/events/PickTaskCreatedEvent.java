package uks.debuggen.microshop.events;
import java.util.Objects;

public class PickTaskCreatedEvent extends Event
{
   public static final String PROPERTY_CODE = "code";
   public static final String PROPERTY_ORDER = "order";
   private String code;
   private String order;

   public String getCode()
   {
      return this.code;
   }

   public PickTaskCreatedEvent setCode(String value)
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

   public String getOrder()
   {
      return this.order;
   }

   public PickTaskCreatedEvent setOrder(String value)
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
      result.append(' ').append(this.getCode());
      result.append(' ').append(this.getOrder());
      return result.toString();
   }
}
