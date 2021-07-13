package uks.debuggen.shop.events;
import java.util.Objects;

public class ShopPolicy extends Event
{
   public static final String PROPERTY_ORDER_APPROVED = "OrderApproved";
   public static final String PROPERTY_ORDER = "order";
   private String OrderApproved;
   private String order;

   public String getOrderApproved()
   {
      return this.OrderApproved;
   }

   public ShopPolicy setOrderApproved(String value)
   {
      if (Objects.equals(value, this.OrderApproved))
      {
         return this;
      }

      final String oldValue = this.OrderApproved;
      this.OrderApproved = value;
      this.firePropertyChange(PROPERTY_ORDER_APPROVED, oldValue, value);
      return this;
   }

   public String getOrder()
   {
      return this.order;
   }

   public ShopPolicy setOrder(String value)
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
      result.append(' ').append(this.getOrderApproved());
      result.append(' ').append(this.getOrder());
      return result.toString();
   }
}
