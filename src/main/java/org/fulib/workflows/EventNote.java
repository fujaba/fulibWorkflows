package org.fulib.workflows;
import java.util.Objects;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Collection;

public class EventNote extends WorkflowNote
{
   public static final String PROPERTY_POLICIES = "policies";
   public static final String PROPERTY_EVENT_TYPE_NAME = "eventTypeName";
   public static final String PROPERTY_TYPE = "type";
   private List<Policy> policies;
   private String eventTypeName;
   private EventType type;

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

   public String getEventTypeName()
   {
      return this.eventTypeName;
   }

   public EventNote setEventTypeName(String value)
   {
      if (Objects.equals(value, this.eventTypeName))
      {
         return this;
      }

      final String oldValue = this.eventTypeName;
      this.eventTypeName = value;
      this.firePropertyChange(PROPERTY_EVENT_TYPE_NAME, oldValue, value);
      return this;
   }

   public EventType getType()
   {
      return this.type;
   }

   public EventNote setType(EventType value)
   {
      if (this.type == value)
      {
         return this;
      }

      final EventType oldValue = this.type;
      if (this.type != null)
      {
         this.type = null;
         oldValue.withoutEvents(this);
      }
      this.type = value;
      if (value != null)
      {
         value.withEvents(this);
      }
      this.firePropertyChange(PROPERTY_TYPE, oldValue, value);
      return this;
   }

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder(super.toString());
      result.append(' ').append(this.getEventTypeName());
      return result.toString();
   }

   @Override
   public void removeYou()
   {
      super.removeYou();
      this.withoutPolicies(new ArrayList<>(this.getPolicies()));
      this.setType(null);
   }
}
