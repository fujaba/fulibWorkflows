package uks.debuggen.interconnect.events;
import java.util.Objects;

public class ChargingRequestedEvent extends Event
{
   public static final String PROPERTY_REQUEST_ID = "requestId";
   public static final String PROPERTY_CHARGER_ID = "chargerId";
   public static final String PROPERTY_POWER = "power";
   public static final String PROPERTY_INTERVAL = "interval";
   private String requestId;
   private String chargerId;
   private String power;
   private String interval;

   public String getRequestId()
   {
      return this.requestId;
   }

   public ChargingRequestedEvent setRequestId(String value)
   {
      if (Objects.equals(value, this.requestId))
      {
         return this;
      }

      final String oldValue = this.requestId;
      this.requestId = value;
      this.firePropertyChange(PROPERTY_REQUEST_ID, oldValue, value);
      return this;
   }

   public String getChargerId()
   {
      return this.chargerId;
   }

   public ChargingRequestedEvent setChargerId(String value)
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

   public String getPower()
   {
      return this.power;
   }

   public ChargingRequestedEvent setPower(String value)
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

   public String getInterval()
   {
      return this.interval;
   }

   public ChargingRequestedEvent setInterval(String value)
   {
      if (Objects.equals(value, this.interval))
      {
         return this;
      }

      final String oldValue = this.interval;
      this.interval = value;
      this.firePropertyChange(PROPERTY_INTERVAL, oldValue, value);
      return this;
   }

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder(super.toString());
      result.append(' ').append(this.getRequestId());
      result.append(' ').append(this.getChargerId());
      result.append(' ').append(this.getPower());
      result.append(' ').append(this.getInterval());
      return result.toString();
   }
}
