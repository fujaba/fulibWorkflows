package org.fulib.workflows;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Collection;
import java.beans.PropertyChangeSupport;
import java.util.Objects;

public class Interaction
{
   public static final String PROPERTY_STEPS = "steps";
   public static final String PROPERTY_ACTOR_NAME = "actorName";
   private List<WorkflowNote> steps;
   protected PropertyChangeSupport listeners;
   private String actorName;

   public List<WorkflowNote> getSteps()
   {
      return this.steps != null ? Collections.unmodifiableList(this.steps) : Collections.emptyList();
   }

   public Interaction withSteps(WorkflowNote value)
   {
      if (this.steps == null)
      {
         this.steps = new ArrayList<>();
      }
      if (!this.steps.contains(value))
      {
         this.steps.add(value);
         value.setInteraction(this);
         this.firePropertyChange(PROPERTY_STEPS, null, value);
      }
      return this;
   }

   public Interaction withSteps(WorkflowNote... value)
   {
      for (final WorkflowNote item : value)
      {
         this.withSteps(item);
      }
      return this;
   }

   public Interaction withSteps(Collection<? extends WorkflowNote> value)
   {
      for (final WorkflowNote item : value)
      {
         this.withSteps(item);
      }
      return this;
   }

   public Interaction withoutSteps(WorkflowNote value)
   {
      if (this.steps != null && this.steps.remove(value))
      {
         value.setInteraction(null);
         this.firePropertyChange(PROPERTY_STEPS, value, null);
      }
      return this;
   }

   public Interaction withoutSteps(WorkflowNote... value)
   {
      for (final WorkflowNote item : value)
      {
         this.withoutSteps(item);
      }
      return this;
   }

   public Interaction withoutSteps(Collection<? extends WorkflowNote> value)
   {
      for (final WorkflowNote item : value)
      {
         this.withoutSteps(item);
      }
      return this;
   }

   public String getActorName()
   {
      return this.actorName;
   }

   public Interaction setActorName(String value)
   {
      if (Objects.equals(value, this.actorName))
      {
         return this;
      }

      final String oldValue = this.actorName;
      this.actorName = value;
      this.firePropertyChange(PROPERTY_ACTOR_NAME, oldValue, value);
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

   public void removeYou()
   {
      this.withoutSteps(new ArrayList<>(this.getSteps()));
   }

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder();
      result.append(' ').append(this.getActorName());
      return result.substring(1);
   }
}
