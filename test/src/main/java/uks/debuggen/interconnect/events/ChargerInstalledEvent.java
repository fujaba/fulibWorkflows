package uks.debuggen.interconnect.events;
import java.util.Objects;

public class ChargerInstalledEvent extends Event
{
   public static final String PROPERTY_CHARGER_ID = "chargerId";
   public static final String PROPERTY_ADDRESS = "address";
   public static final String PROPERTY_POWER = "power";
   private String chargerId;
   private String address;
   private String power;

   public String getChargerId()
   {
      return this.chargerId;
   }

   public ChargerInstalledEvent setChargerId(String value)
   {
      if (Objects.equals(value, this.chargerId))
      {
         return this;
      }

      final String oldValue = this.chargerId;
      this.chargerId = value;
      this.firePropertyChange(PROPERTY_CHARGER_ID, oldValue, value);
      return this;
   }

   public String getAddress()
   {
      return this.address;
   }

   public ChargerInstalledEvent setAddress(String value)
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

   public String getPower()
   {
      return this.power;
   }

   public ChargerInstalledEvent setPower(String value)
   {
      if (Objects.equals(value, this.power))
      {
         return this;
      }

      final String oldValue = this.power;
      this.power = value;
      this.firePropertyChange(PROPERTY_POWER, oldValue, value);
      return this;
   }

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder(super.toString());
      result.append(' ').append(this.getChargerId());
      result.append(' ').append(this.getAddress());
      result.append(' ').append(this.getPower());
      return result.toString();
   }
}
