package org.fulib.workflows;
import java.util.Objects;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Collection;

public class ExternalSystemNote extends WorkflowNote
{
   public static final String PROPERTY_TIME_INTERVAL = "timeInterval";
   public static final String PROPERTY_POLICIES = "policies";
   public static final String PROPERTY_SYSTEM_NAME = "systemName";
   private String timeInterval;
   private List<Policy> policies;
   private String systemName;

   public String getTimeInterval()
   {
      return this.timeInterval;
   }

   public ExternalSystemNote setTimeInterval(String value)
   {
      if (Objects.equals(value, this.timeInterval))
      {
         return this;
      }

      final String oldValue = this.timeInterval;
      this.timeInterval = value;
      this.firePropertyChange(PROPERTY_TIME_INTERVAL, oldValue, value);
      return this;
   }

   public List<Policy> getPolicies()
   {
      return this.policies != null ? Collections.unmodifiableList(this.policies) : Collections.emptyList();
   }

   public ExternalSystemNote withPolicies(Policy value)
   {
      if (this.policies == null)
      {
         this.policies = new ArrayList<>();
      }
      if (!this.policies.contains(value))
      {
         this.policies.add(value);
         value.setExternalSystem(this);
         this.firePropertyChange(PROPERTY_POLICIES, null, value);
      }
      return this;
   }

   public ExternalSystemNote withPolicies(Policy... value)
   {
      for (final Policy item : value)
      {
         this.withPolicies(item);
      }
      return this;
   }

   public ExternalSystemNote withPolicies(Collection<? extends Policy> value)
   {
      for (final Policy item : value)
      {
         this.withPolicies(item);
      }
      return this;
   }

   public ExternalSystemNote withoutPolicies(Policy value)
   {
      if (this.policies != null && this.policies.remove(value))
      {
         value.setExternalSystem(null);
         this.firePropertyChange(PROPERTY_POLICIES, value, null);
      }
      return this;
   }

   public ExternalSystemNote withoutPolicies(Policy... value)
   {
      for (final Policy item : value)
      {
         this.withoutPolicies(item);
      }
      return this;
   }

   public ExternalSystemNote withoutPolicies(Collection<? extends Policy> value)
   {
      for (final Policy item : value)
      {
         this.withoutPolicies(item);
      }
      return this;
   }

   public String getSystemName()
   {
      return this.systemName;
   }

   public ExternalSystemNote setSystemName(String value)
   {
      if (Objects.equals(value, this.systemName))
      {
         return this;
      }

      final String oldValue = this.systemName;
      this.systemName = value;
      this.firePropertyChange(PROPERTY_SYSTEM_NAME, oldValue, value);
      return this;
   }

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder(super.toString());
      result.append(' ').append(this.getSystemName());
      result.append(' ').append(this.getTimeInterval());
      return result.toString();
   }

   @Override
   public void removeYou()
   {
      super.removeYou();
      this.withoutPolicies(new ArrayList<>(this.getPolicies()));
   }
}
