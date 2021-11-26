package org.fulib.workflows.events;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Collection;
import java.beans.PropertyChangeSupport;

public class Board
{
   public static final String PROPERTY_WORKFLOWS = "workflows";
   private List<Workflow> workflows;
   protected PropertyChangeSupport listeners;

   public List<Workflow> getWorkflows()
   {
      return this.workflows != null ? Collections.unmodifiableList(this.workflows) : Collections.emptyList();
   }

   public Board withWorkflows(Workflow value)
   {
      if (this.workflows == null)
      {
         this.workflows = new ArrayList<>();
      }
      if (this.workflows.add(value))
      {
         this.firePropertyChange(PROPERTY_WORKFLOWS, null, value);
      }
      return this;
   }

   public Board withWorkflows(Workflow... value)
   {
      for (final Workflow item : value)
      {
         this.withWorkflows(item);
      }
      return this;
   }

   public Board withWorkflows(Collection<? extends Workflow> value)
   {
      for (final Workflow item : value)
      {
         this.withWorkflows(item);
      }
      return this;
   }

   public Board withoutWorkflows(Workflow value)
   {
      if (this.workflows != null && this.workflows.removeAll(Collections.singleton(value)))
      {
         this.firePropertyChange(PROPERTY_WORKFLOWS, value, null);
      }
      return this;
   }

   public Board withoutWorkflows(Workflow... value)
   {
      for (final Workflow item : value)
      {
         this.withoutWorkflows(item);
      }
      return this;
   }

   public Board withoutWorkflows(Collection<? extends Workflow> value)
   {
      for (final Workflow item : value)
      {
         this.withoutWorkflows(item);
      }
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
}
