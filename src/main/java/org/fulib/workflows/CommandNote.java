package org.fulib.workflows;
import java.util.Objects;

public class CommandNote extends WorkflowNote
{
   public static final String PROPERTY_EVENT_TYPE = "eventType";
   private String eventType;

   public String getEventType()
   {
      return this.eventType;
   }

   public CommandNote setEventType(String value)
   {
      if (Objects.equals(value, this.eventType))
      {
         return this;
      }

      final String oldValue = this.eventType;
      this.eventType = value;
      this.firePropertyChange(PROPERTY_EVENT_TYPE, oldValue, value);
      return this;
   }

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder(super.toString());
      result.append(' ').append(this.getEventType());
      return result.toString();
   }
}
