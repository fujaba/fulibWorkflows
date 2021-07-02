package org.fulib.workflows;
import java.util.Objects;

public class UserRegistered extends Event
{
   public static final String PROPERTY_NAME = "name";
   public static final String PROPERTY_ROLE = "role";
   private String name;
   private String role;

   public String getName()
   {
      return this.name;
   }

   public UserRegistered setName(String value)
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

   public String getRole()
   {
      return this.role;
   }

   public UserRegistered setRole(String value)
   {
      if (Objects.equals(value, this.role))
      {
         return this;
      }

      final String oldValue = this.role;
      this.role = value;
      this.firePropertyChange(PROPERTY_ROLE, oldValue, value);
      return this;
   }

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder(super.toString());
      result.append(' ').append(this.getName());
      result.append(' ').append(this.getRole());
      return result.toString();
   }
}
