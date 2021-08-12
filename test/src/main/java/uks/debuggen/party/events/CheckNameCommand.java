package uks.debuggen.party.events;
import java.util.Objects;

public class CheckNameCommand extends Command
{
   public static final String PROPERTY_NAME = "name";
   private String name;

   public String getName()
   {
      return this.name;
   }

   public CheckNameCommand setName(String value)
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

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder(super.toString());
      result.append(' ').append(this.getName());
      return result.toString();
   }
}
