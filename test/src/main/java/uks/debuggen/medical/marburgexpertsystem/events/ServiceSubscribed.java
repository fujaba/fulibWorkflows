package uks.debuggen.medical.marburgexpertsystem.events;
import java.util.Objects;

public class ServiceSubscribed extends Event
{
   public static final String PROPERTY_SERVICE_URL = "serviceUrl";
   private String serviceUrl;

   public String getServiceUrl()
   {
      return this.serviceUrl;
   }

   public ServiceSubscribed setServiceUrl(String value)
   {
      if (Objects.equals(value, this.serviceUrl))
      {
         return this;
      }

      final String oldValue = this.serviceUrl;
      this.serviceUrl = value;
      this.firePropertyChange(PROPERTY_SERVICE_URL, oldValue, value);
      return this;
   }

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder(super.toString());
      result.append(' ').append(this.getServiceUrl());
      return result.toString();
   }
}
