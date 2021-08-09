package uks.debuggen.party.events;
import java.util.Objects;

public class GetPartyCommand extends Command
{
   public static final String PROPERTY_PARTY = "party";
   private String party;

   public String getParty()
   {
      return this.party;
   }

   public GetPartyCommand setParty(String value)
   {
      if (Objects.equals(value, this.party))
      {
         return this;
      }

      final String oldValue = this.party;
      this.party = value;
      this.firePropertyChange(PROPERTY_PARTY, oldValue, value);
      return this;
   }

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder(super.toString());
      result.append(' ').append(this.getParty());
      return result.toString();
   }
}
