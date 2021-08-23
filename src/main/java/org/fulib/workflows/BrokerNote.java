package org.fulib.workflows;
import java.util.Objects;

public class BrokerNote extends WorkflowNote
{
   public static final String PROPERTY_BROKER_NAME = "brokerName";
   private String brokerName;

   public String getBrokerName()
   {
      return this.brokerName;
   }

   public BrokerNote setBrokerName(String value)
   {
      if (Objects.equals(value, this.brokerName))
      {
         return this;
      }

      final String oldValue = this.brokerName;
      this.brokerName = value;
      this.firePropertyChange(PROPERTY_BROKER_NAME, oldValue, value);
      return this;
   }

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder(super.toString());
      result.append(' ').append(this.getBrokerName());
      return result.toString();
   }
}
