package org.fulib.workflows;
import java.util.Objects;

public class DataEvent extends Event
{
   public static final String PROPERTY_INCREMENT = "increment";
   private String increment;

   public String getIncrement()
   {
      return this.increment;
   }

   public DataEvent setIncrement(String value)
   {
      if (Objects.equals(value, this.increment))
      {
         return this;
      }

      final String oldValue = this.increment;
      this.increment = value;
      this.firePropertyChange(PROPERTY_INCREMENT, oldValue, value);
      return this;
   }

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder(super.toString());
      result.append(' ').append(this.getIncrement());
      return result.toString();
   }
}
