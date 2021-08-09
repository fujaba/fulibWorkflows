package uks.debuggen.party.events;
import java.util.Objects;

public class GetEmailCommand extends Command
{
   public static final String PROPERTY_EMAIL = "email";
   private String email;

   public String getEmail()
   {
      return this.email;
   }

   public GetEmailCommand setEmail(String value)
   {
      if (Objects.equals(value, this.email))
      {
         return this;
      }

      final String oldValue = this.email;
      this.email = value;
      this.firePropertyChange(PROPERTY_EMAIL, oldValue, value);
      return this;
   }

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder(super.toString());
      result.append(' ').append(this.getEmail());
      return result.toString();
   }
}
