package uks.debuggen.medical.marburg.events;
import java.util.Objects;

public class LoadDiseasesCommand extends Command
{
   public static final String PROPERTY_NAMES = "names";
   private String names;

   public String getNames()
   {
      return this.names;
   }

   public LoadDiseasesCommand setNames(String value)
   {
      if (Objects.equals(value, this.names))
      {
         return this;
      }

      final String oldValue = this.names;
      this.names = value;
      this.firePropertyChange(PROPERTY_NAMES, oldValue, value);
      return this;
   }

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder(super.toString());
      result.append(' ').append(this.getNames());
      return result.toString();
   }
}
