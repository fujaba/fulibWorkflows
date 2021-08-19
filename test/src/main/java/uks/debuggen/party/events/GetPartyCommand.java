package uks.debuggen.party.events;
import java.util.Objects;

public class GetPartyCommand extends Command
{
   public static final String PROPERTY_PARTY = "party";
   public static final String PROPERTY_LOCATION = "location";
   public static final String PROPERTY_DATE = "date";
   private String party;
   private String location;
   private String date;

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

   public String getDate()
   {
      return this.date;
   }

   public GetPartyCommand setDate(String value)
   {
      if (Objects.equals(value, this.date))
      {
         return this;
      }

      final String oldValue = this.date;
      this.date = value;
      this.firePropertyChange(PROPERTY_DATE, oldValue, value);
      return this;
   }

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder(super.toString());
      result.append(' ').append(this.getParty());
      result.append(' ').append(this.getDate());
      result.append(' ').append(this.getLocation());
      return result.toString();
   }
}
