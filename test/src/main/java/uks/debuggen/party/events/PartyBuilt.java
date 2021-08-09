package uks.debuggen.party.events;
import java.util.Objects;

public class PartyBuilt extends DataEvent
{
   public static final String PROPERTY_NAME = "name";
   public static final String PROPERTY_LOCATION = "location";
   private String name;
   private String location;

   public String getName()
   {
      return this.name;
   }

   public PartyBuilt setName(String value)
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

   public String getLocation()
   {
      return this.location;
   }

   public PartyBuilt setLocation(String value)
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
      result.append(' ').append(this.getName());
      result.append(' ').append(this.getLocation());
      return result.toString();
   }
}
