package uks.debuggen.party.PartyApp;
import java.util.Objects;
import java.beans.PropertyChangeSupport;

public class User
{
   public static final String PROPERTY_ID = "id";
   public static final String PROPERTY_NAME = "name";
   public static final String PROPERTY_EMAIL = "email";
   public static final String PROPERTY_PASSWORD = "password";
   private String id;
   private String name;
   private String email;
   private String password;
   protected PropertyChangeSupport listeners;

   public String getId()
   {
      return this.id;
   }

   public User setId(String value)
   {
      if (Objects.equals(value, this.id))
      {
         return this;
      }

      final String oldValue = this.id;
      this.id = value;
      this.firePropertyChange(PROPERTY_ID, oldValue, value);
      return this;
   }

   public String getName()
   {
      return this.name;
   }

   public User setName(String value)
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

   public User setEmail(String value)
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

   public User setPassword(String value)
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

   public boolean firePropertyChange(String propertyName, Object oldValue, Object newValue)
   {
      if (this.listeners != null)
      {
         this.listeners.firePropertyChange(propertyName, oldValue, newValue);
         return true;
      }
      return false;
   }

   public PropertyChangeSupport listeners()
   {
      if (this.listeners == null)
      {
         this.listeners = new PropertyChangeSupport(this);
      }
      return this.listeners;
   }

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder();
      result.append(' ').append(this.getId());
      result.append(' ').append(this.getName());
      result.append(' ').append(this.getEmail());
      result.append(' ').append(this.getPassword());
      return result.substring(1);
   }
}
