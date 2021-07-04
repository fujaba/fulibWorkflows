package uks.fulibgen.shop.events;
import java.util.Objects;

public class OrderPicked extends Event
{
   public static final String PROPERTY_BOX = "box";
   public static final String PROPERTY_PICK_TASK = "pickTask";
   public static final String PROPERTY_USER = "user";
   private String box;
   private String pickTask;
   private String user;

   public String getBox()
   {
      return this.box;
   }

   public OrderPicked setBox(String value)
   {
      if (Objects.equals(value, this.box))
      {
         return this;
      }

      final String oldValue = this.box;
      this.box = value;
      this.firePropertyChange(PROPERTY_BOX, oldValue, value);
      return this;
   }

   public String getPickTask()
   {
      return this.pickTask;
   }

   public OrderPicked setPickTask(String value)
   {
      if (Objects.equals(value, this.pickTask))
      {
         return this;
      }

      final String oldValue = this.pickTask;
      this.pickTask = value;
      this.firePropertyChange(PROPERTY_PICK_TASK, oldValue, value);
      return this;
   }

   public String getUser()
   {
      return this.user;
   }

   public OrderPicked setUser(String value)
   {
      if (Objects.equals(value, this.user))
      {
         return this;
      }

      final String oldValue = this.user;
      this.user = value;
      this.firePropertyChange(PROPERTY_USER, oldValue, value);
      return this;
   }

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder(super.toString());
      result.append(' ').append(this.getPickTask());
      result.append(' ').append(this.getBox());
      result.append(' ').append(this.getUser());
      return result.toString();
   }
}
