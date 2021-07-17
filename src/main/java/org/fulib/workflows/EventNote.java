package org.fulib.workflows;
import java.util.Objects;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Collection;

public class EventNote extends WorkflowNote
{
   public static final String PROPERTY_EVENT_TYPE = "eventType";
   public static final String PROPERTY_POLICIES = "policies";
   private String eventType;
   private List<Policy> policies;

   public String getEventType()
   {
      return this.eventType;
   }

   public EventNote setEventType(String value)
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

   public List<Policy> getPolicies()
   {
      return this.policies != null ? Collections.unmodifiableList(this.policies) : Collections.emptyList();
   }

   public EventNote withPolicies(Policy value)
   {
      if (this.policies == null)
      {
         this.policies = new ArrayList<>();
      }
      if (!this.policies.contains(value))
      {
         this.policies.add(value);
         value.setTrigger(this);
         this.firePropertyChange(PROPERTY_POLICIES, null, value);
      }
      return this;
   }

   public EventNote withPolicies(Policy... value)
   {
      for (final Policy item : value)
      {
         this.withPolicies(item);
      }
      return this;
   }

   public EventNote withPolicies(Collection<? extends Policy> value)
   {
      for (final Policy item : value)
      {
         this.withPolicies(item);
      }
      return this;
   }

   public EventNote withoutPolicies(Policy value)
   {
      if (this.policies != null && this.policies.remove(value))
      {
         value.setTrigger(null);
         this.firePropertyChange(PROPERTY_POLICIES, value, null);
      }
      return this;
   }

   public EventNote withoutPolicies(Policy... value)
   {
      for (final Policy item : value)
      {
         this.withoutPolicies(item);
      }
      return this;
   }

   public EventNote withoutPolicies(Collection<? extends Policy> value)
   {
      for (final Policy item : value)
      {
         this.withoutPolicies(item);
      }
      return this;
   }

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder(super.toString());
      result.append(' ').append(this.getEventType());
      return result.toString();
   }

   @Override
   public void removeYou()
   {
      super.removeYou();
      this.withoutPolicies(new ArrayList<>(this.getPolicies()));
   }
}
