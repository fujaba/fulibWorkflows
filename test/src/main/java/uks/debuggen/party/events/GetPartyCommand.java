package uks.debuggen.party.events;
import java.util.Objects;

public class GetPartyCommand extends Command
{
   public static final String PROPERTY_PARTY = "party";
   public static final String PROPERTY_LOCATION = "location";
   private String party;
   private String location;

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

   public String getLocation()
   {
      return this.location;
   }

   public GetPartyCommand setLocation(String value)
   {
      if (Objects.equals(value, this.location))
      {
         return this;
      }

      final String oldValue = this.location;
      this.location = value;
      this.firePropertyChange(PROPERTY_LOCATION, oldValue, value);
      return this;
   }

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder(super.toString());
      result.append(' ').append(this.getParty());
      result.append(' ').append(this.getLocation());
      return result.toString();
   }
}
