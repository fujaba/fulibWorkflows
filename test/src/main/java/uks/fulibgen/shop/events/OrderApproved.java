package uks.fulibgen.shop.events;
import java.util.Objects;

public class OrderApproved extends Event
{
   public static final String PROPERTY_ORDER = "order";
   public static final String PROPERTY_EVENT = "event";
   private String order;
   private String event;

   public String getOrder()
   {
      return this.order;
   }

   public OrderApproved setOrder(String value)
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

   public String getEvent()
   {
      return this.event;
   }

   public OrderApproved setEvent(String value)
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
      result.append(' ').append(this.getOrder());
      return result.toString();
   }
}
