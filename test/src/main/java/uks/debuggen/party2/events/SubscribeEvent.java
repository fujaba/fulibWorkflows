package uks.debuggen.party2.events;
import java.util.Objects;

public class SubscribeEvent extends Event
{
   public static final String PROPERTY_URL = "url";
   private String url;

   public String getUrl()
   {
      return this.url;
   }

   public SubscribeEvent setUrl(String value)
   {
      if (Objects.equals(value, this.url))
      {
         return this;
      }

      final String oldValue = this.url;
      this.url = value;
      this.firePropertyChange(PROPERTY_URL, oldValue, value);
      return this;
   }

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder(super.toString());
      result.append(' ').append(this.getUrl());
      return result.toString();
   }
}
