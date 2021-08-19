package uks.debuggen.party2.events;
import java.util.Objects;

public class Party2Built extends DataEvent
{
   public static final String PROPERTY_NAME = "name";
   public static final String PROPERTY_REGION = "region";
   public static final String PROPERTY_ADDRESS = "address";
   public static final String PROPERTY_DATE = "date";
   private String name;
   private String region;
   private String address;
   private String date;

   public String getName()
   {
      return this.name;
   }

   public Party2Built setName(String value)
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

   public String getRegion()
   {
      return this.region;
   }

   public Party2Built setRegion(String value)
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

   public String getAddress()
   {
      return this.address;
   }

   public Party2Built setAddress(String value)
   {
      if (Objects.equals(value, this.address))
      {
         return this;
      }

      final String oldValue = this.address;
      this.address = value;
      this.firePropertyChange(PROPERTY_ADDRESS, oldValue, value);
      return this;
   }

   public String getDate()
   {
      return this.date;
   }

   public Party2Built setDate(String value)
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
      result.append(' ').append(this.getName());
      result.append(' ').append(this.getRegion());
      result.append(' ').append(this.getDate());
      result.append(' ').append(this.getAddress());
      return result.toString();
   }
}
