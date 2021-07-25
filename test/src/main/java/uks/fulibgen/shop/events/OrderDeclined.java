package uks.fulibgen.shop.events;
import java.util.Objects;

public class OrderDeclined extends Event
{
   public static final String PROPERTY_EVENT = "event";
   public static final String PROPERTY_ORDER = "order";
   private String event;
   private String order;

   public String getEvent()
   {
      return this.event;
   }

   public OrderDeclined setEvent(String value)
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

   public String getOrder()
   {
      return this.order;
   }

   public OrderDeclined setOrder(String value)
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
      result.append(' ').append(this.getEvent());
      result.append(' ').append(this.getOrder());
      return result.toString();
   }
}
