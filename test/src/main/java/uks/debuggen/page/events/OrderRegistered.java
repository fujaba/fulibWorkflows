package uks.debuggen.page.events;
import java.util.Objects;

public class OrderRegistered extends Event
{
   public static final String PROPERTY_EVENT = "event";
   public static final String PROPERTY_COUNT = "count";
   public static final String PROPERTY_NAME = "name";
   public static final String PROPERTY_ADDRESS = "address";
   private String event;
   private String count;
   private String name;
   private String address;

   public String getEvent()
   {
      return this.event;
   }

   public OrderRegistered setEvent(String value)
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

   public String getCount()
   {
      return this.count;
   }

   public OrderRegistered setCount(String value)
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

   public String getName()
   {
      return this.name;
   }

   public OrderRegistered setName(String value)
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

   public String getAddress()
   {
      return this.address;
   }

   public OrderRegistered setAddress(String value)
   {
      if (Objects.equals(value, this.address))
      {
         return this;
      }

      final String oldValue = this.address;
      this.address = value;
      this.firePropertyChange(PROPERTY_ADDRESS, oldValue, value);
      return this;
   }

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder(super.toString());
      result.append(' ').append(this.getEvent());
      result.append(' ').append(this.getCount());
      result.append(' ').append(this.getName());
      result.append(' ').append(this.getAddress());
      return result.toString();
   }
}
