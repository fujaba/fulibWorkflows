package org.fulib.workflows;
import java.util.Objects;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Collection;

public class ServiceNote extends Note
{
   public static final String PROPERTY_PORT = "port";
   public static final String PROPERTY_POLICIES = "policies";
   public static final String PROPERTY_USER_INTERACTIONS = "userInteractions";
   public static final String PROPERTY_NAME = "name";
   public static final String PROPERTY_WORKFLOWS = "workflows";
   private String port;
   private List<Policy> policies;
   private List<UserInteraction> userInteractions;
   private String name;
   private List<Workflow> workflows;

   public String getPort()
   {
      return this.port;
   }

   public ServiceNote setPort(String value)
   {
      if (Objects.equals(value, this.port))
      {
         return this;
      }

      final String oldValue = this.port;
      this.port = value;
      this.firePropertyChange(PROPERTY_PORT, oldValue, value);
      return this;
   }

   public List<Policy> getPolicies()
   {
      return this.policies != null ? Collections.unmodifiableList(this.policies) : Collections.emptyList();
   }

   public ServiceNote withPolicies(Policy value)
   {
      if (this.policies == null)
      {
         this.policies = new ArrayList<>();
      }
      if (!this.policies.contains(value))
      {
         this.policies.add(value);
         value.setService(this);
         this.firePropertyChange(PROPERTY_POLICIES, null, value);
      }
      return this;
   }

   public ServiceNote withPolicies(Policy... value)
   {
      for (final Policy item : value)
      {
         this.withPolicies(item);
      }
      return this;
   }

   public ServiceNote withPolicies(Collection<? extends Policy> value)
   {
      for (final Policy item : value)
      {
         this.withPolicies(item);
      }
      return this;
   }

   public ServiceNote withoutPolicies(Policy value)
   {
      if (this.policies != null && this.policies.remove(value))
      {
         value.setService(null);
         this.firePropertyChange(PROPERTY_POLICIES, value, null);
      }
      return this;
   }

   public ServiceNote withoutPolicies(Policy... value)
   {
      for (final Policy item : value)
      {
         this.withoutPolicies(item);
      }
      return this;
   }

   public ServiceNote withoutPolicies(Collection<? extends Policy> value)
   {
      for (final Policy item : value)
      {
         this.withoutPolicies(item);
      }
      return this;
   }

   public List<UserInteraction> getUserInteractions()
   {
      return this.userInteractions != null ? Collections.unmodifiableList(this.userInteractions) : Collections.emptyList();
   }

   public ServiceNote withUserInteractions(UserInteraction value)
   {
      if (this.userInteractions == null)
      {
         this.userInteractions = new ArrayList<>();
      }
      if (!this.userInteractions.contains(value))
      {
         this.userInteractions.add(value);
         value.setService(this);
         this.firePropertyChange(PROPERTY_USER_INTERACTIONS, null, value);
      }
      return this;
   }

   public ServiceNote withUserInteractions(UserInteraction... value)
   {
      for (final UserInteraction item : value)
      {
         this.withUserInteractions(item);
      }
      return this;
   }

   public ServiceNote withUserInteractions(Collection<? extends UserInteraction> value)
   {
      for (final UserInteraction item : value)
      {
         this.withUserInteractions(item);
      }
      return this;
   }

   public ServiceNote withoutUserInteractions(UserInteraction value)
   {
      if (this.userInteractions != null && this.userInteractions.remove(value))
      {
         value.setService(null);
         this.firePropertyChange(PROPERTY_USER_INTERACTIONS, value, null);
      }
      return this;
   }

   public ServiceNote withoutUserInteractions(UserInteraction... value)
   {
      for (final UserInteraction item : value)
      {
         this.withoutUserInteractions(item);
      }
      return this;
   }

   public ServiceNote withoutUserInteractions(Collection<? extends UserInteraction> value)
   {
      for (final UserInteraction item : value)
      {
         this.withoutUserInteractions(item);
      }
      return this;
   }

   public String getName()
   {
      return this.name;
   }

   public ServiceNote setName(String value)
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

   public List<Workflow> getWorkflows()
   {
      return this.workflows != null ? Collections.unmodifiableList(this.workflows) : Collections.emptyList();
   }

   public ServiceNote withWorkflows(Workflow value)
   {
      if (this.workflows == null)
      {
         this.workflows = new ArrayList<>();
      }
      if (!this.workflows.contains(value))
      {
         this.workflows.add(value);
         value.withServices(this);
         this.firePropertyChange(PROPERTY_WORKFLOWS, null, value);
      }
      return this;
   }

   public ServiceNote withWorkflows(Workflow... value)
   {
      for (final Workflow item : value)
      {
         this.withWorkflows(item);
      }
      return this;
   }

   public ServiceNote withWorkflows(Collection<? extends Workflow> value)
   {
      for (final Workflow item : value)
      {
         this.withWorkflows(item);
      }
      return this;
   }

   public ServiceNote withoutWorkflows(Workflow value)
   {
      if (this.workflows != null && this.workflows.remove(value))
      {
         value.withoutServices(this);
         this.firePropertyChange(PROPERTY_WORKFLOWS, value, null);
      }
      return this;
   }

   public ServiceNote withoutWorkflows(Workflow... value)
   {
      for (final Workflow item : value)
      {
         this.withoutWorkflows(item);
      }
      return this;
   }

   public ServiceNote withoutWorkflows(Collection<? extends Workflow> value)
   {
      for (final Workflow item : value)
      {
         this.withoutWorkflows(item);
      }
      return this;
   }

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder(super.toString());
      result.append(' ').append(this.getName());
      result.append(' ').append(this.getPort());
      return result.toString();
   }

   public void removeYou()
   {
      this.withoutPolicies(new ArrayList<>(this.getPolicies()));
      this.withoutUserInteractions(new ArrayList<>(this.getUserInteractions()));
      this.withoutWorkflows(new ArrayList<>(this.getWorkflows()));
   }
}
