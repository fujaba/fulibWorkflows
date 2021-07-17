package org.fulib.workflows;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Collections;
import java.util.Collection;

public class UserNote extends Note
{
   public static final String PROPERTY_NAME = "name";
   public static final String PROPERTY_WORKFLOWS = "workflows";
   public static final String PROPERTY_INTERACTIONS = "interactions";
   private String name;
   private List<Workflow> workflows;
   private List<UserInteraction> interactions;

   public String getName()
   {
      return this.name;
   }

   public UserNote setName(String value)
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

   public UserNote withWorkflows(Workflow value)
   {
      if (this.workflows == null)
      {
         this.workflows = new ArrayList<>();
      }
      if (!this.workflows.contains(value))
      {
         this.workflows.add(value);
         value.withUsers(this);
         this.firePropertyChange(PROPERTY_WORKFLOWS, null, value);
      }
      return this;
   }

   public UserNote withWorkflows(Workflow... value)
   {
      for (final Workflow item : value)
      {
         this.withWorkflows(item);
      }
      return this;
   }

   public UserNote withWorkflows(Collection<? extends Workflow> value)
   {
      for (final Workflow item : value)
      {
         this.withWorkflows(item);
      }
      return this;
   }

   public UserNote withoutWorkflows(Workflow value)
   {
      if (this.workflows != null && this.workflows.remove(value))
      {
         value.withoutUsers(this);
         this.firePropertyChange(PROPERTY_WORKFLOWS, value, null);
      }
      return this;
   }

   public UserNote withoutWorkflows(Workflow... value)
   {
      for (final Workflow item : value)
      {
         this.withoutWorkflows(item);
      }
      return this;
   }

   public UserNote withoutWorkflows(Collection<? extends Workflow> value)
   {
      for (final Workflow item : value)
      {
         this.withoutWorkflows(item);
      }
      return this;
   }

   public List<UserInteraction> getInteractions()
   {
      return this.interactions != null ? Collections.unmodifiableList(this.interactions) : Collections.emptyList();
   }

   public UserNote withInteractions(UserInteraction value)
   {
      if (this.interactions == null)
      {
         this.interactions = new ArrayList<>();
      }
      if (!this.interactions.contains(value))
      {
         this.interactions.add(value);
         value.setUser(this);
         this.firePropertyChange(PROPERTY_INTERACTIONS, null, value);
      }
      return this;
   }

   public UserNote withInteractions(UserInteraction... value)
   {
      for (final UserInteraction item : value)
      {
         this.withInteractions(item);
      }
      return this;
   }

   public UserNote withInteractions(Collection<? extends UserInteraction> value)
   {
      for (final UserInteraction item : value)
      {
         this.withInteractions(item);
      }
      return this;
   }

   public UserNote withoutInteractions(UserInteraction value)
   {
      if (this.interactions != null && this.interactions.remove(value))
      {
         value.setUser(null);
         this.firePropertyChange(PROPERTY_INTERACTIONS, value, null);
      }
      return this;
   }

   public UserNote withoutInteractions(UserInteraction... value)
   {
      for (final UserInteraction item : value)
      {
         this.withoutInteractions(item);
      }
      return this;
   }

   public UserNote withoutInteractions(Collection<? extends UserInteraction> value)
   {
      for (final UserInteraction item : value)
      {
         this.withoutInteractions(item);
      }
      return this;
   }

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder(super.toString());
      result.append(' ').append(this.getName());
      return result.toString();
   }

   public void removeYou()
   {
      this.withoutInteractions(new ArrayList<>(this.getInteractions()));
      this.withoutWorkflows(new ArrayList<>(this.getWorkflows()));
   }
}
