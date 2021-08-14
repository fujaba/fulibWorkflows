package uks.debuggen.party2.events;
import java.util.Objects;

public class UserBuilt extends DataEvent
{
   public static final String PROPERTY_NAME = "name";
   public static final String PROPERTY_EMAIL = "email";
   public static final String PROPERTY_PASSWORD = "password";
   private String name;
   private String email;
   private String password;

   public String getName()
   {
      return this.name;
   }

   public UserBuilt setName(String value)
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

   public String getEmail()
   {
      return this.email;
   }

   public UserBuilt setEmail(String value)
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

   public String getPassword()
   {
      return this.password;
   }

   public UserBuilt setPassword(String value)
   {
      if (Objects.equals(value, this.password))
      {
         return this;
      }

      final String oldValue = this.password;
      this.password = value;
      this.firePropertyChange(PROPERTY_PASSWORD, oldValue, value);
      return this;
   }

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder(super.toString());
      result.append(' ').append(this.getName());
      result.append(' ').append(this.getEmail());
      result.append(' ').append(this.getPassword());
      return result.toString();
   }
}
