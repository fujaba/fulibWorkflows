package uks.debuggen.studyright.events;
import java.util.Objects;

public class FindTours extends Event
{
   public static final String PROPERTY_COMMAND = "command";
   private String command;

   public String getCommand()
   {
      return this.command;
   }

   public FindTours setCommand(String value)
   {
      if (Objects.equals(value, this.command))
      {
         return this;
      }

      final String oldValue = this.command;
      this.command = value;
      this.firePropertyChange(PROPERTY_COMMAND, oldValue, value);
      return this;
   }

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder(super.toString());
      result.append(' ').append(this.getCommand());
      return result.toString();
   }
}
