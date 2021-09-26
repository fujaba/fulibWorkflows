package uks.debuggen.microshop.events;
import java.util.Objects;

public class Command extends Event
{
   public static final String PROPERTY_TASK = "task";
   public static final String PROPERTY_SHELF = "shelf";
   private String task;
   private String shelf;

   public String getTask()
   {
      return this.task;
   }

   public Command setTask(String value)
   {
      if (Objects.equals(value, this.task))
      {
         return this;
      }

      final String oldValue = this.task;
      this.task = value;
      this.firePropertyChange(PROPERTY_TASK, oldValue, value);
      return this;
   }

   public String getShelf()
   {
      return this.shelf;
   }

   public Command setShelf(String value)
   {
      if (Objects.equals(value, this.shelf))
      {
         return this;
      }

      final String oldValue = this.shelf;
      this.shelf = value;
      this.firePropertyChange(PROPERTY_SHELF, oldValue, value);
      return this;
   }

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder(super.toString());
      result.append(' ').append(this.getTask());
      result.append(' ').append(this.getShelf());
      return result.toString();
   }
}
