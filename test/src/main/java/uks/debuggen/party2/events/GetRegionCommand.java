package uks.debuggen.party2.events;
import java.util.Objects;

public class GetRegionCommand extends Command
{
   public static final String PROPERTY_REGION = "region";
   private String region;

   public String getRegion()
   {
      return this.region;
   }

   public GetRegionCommand setRegion(String value)
   {
      if (Objects.equals(value, this.region))
      {
         return this;
      }

      final String oldValue = this.region;
      this.region = value;
      this.firePropertyChange(PROPERTY_REGION, oldValue, value);
      return this;
   }

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder(super.toString());
      result.append(' ').append(this.getRegion());
      return result.toString();
   }
}
