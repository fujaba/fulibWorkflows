package org.fulib.workflows;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Collections;
import java.util.Collection;
import java.beans.PropertyChangeSupport;

public class EventType
{
   public static final String PROPERTY_EVENTS = "events";
   public static final String PROPERTY_WORKFLOW = "workflow";
   public static final String PROPERTY_HANDLERS = "handlers";
   public static final String PROPERTY_EVENT_TYPE_NAME = "eventTypeName";
   private List<EventNote> events;
   private Workflow workflow;
   protected PropertyChangeSupport listeners;
   private List<ServiceNote> handlers;
   private String eventTypeName;

   public List<EventNote> getEvents()
   {
      return this.events != null ? Collections.unmodifiableList(this.events) : Collections.emptyList();
   }

   public EventType withEvents(EventNote value)
   {
      if (this.events == null)
      {
         this.events = new ArrayList<>();
      }
      if (!this.events.contains(value))
      {
         this.events.add(value);
         value.setType(this);
         this.firePropertyChange(PROPERTY_EVENTS, null, value);
      }
      return this;
   }

   public EventType withEvents(EventNote... value)
   {
      for (final EventNote item : value)
      {
         this.withEvents(item);
      }
      return this;
   }

   public EventType withEvents(Collection<? extends EventNote> value)
   {
      for (final EventNote item : value)
      {
         this.withEvents(item);
      }
      return this;
   }

   public EventType withoutEvents(EventNote value)
   {
      if (this.events != null && this.events.remove(value))
      {
         value.setType(null);
         this.firePropertyChange(PROPERTY_EVENTS, value, null);
      }
      return this;
   }

   public EventType withoutEvents(EventNote... value)
   {
      for (final EventNote item : value)
      {
         this.withoutEvents(item);
      }
      return this;
   }

   public EventType withoutEvents(Collection<? extends EventNote> value)
   {
      for (final EventNote item : value)
      {
         this.withoutEvents(item);
      }
      return this;
   }

   public Workflow getWorkflow()
   {
      return this.workflow;
   }

   public EventType setWorkflow(Workflow value)
   {
      if (this.workflow == value)
      {
         return this;
      }

      final Workflow oldValue = this.workflow;
      if (this.workflow != null)
      {
         this.workflow = null;
         oldValue.withoutEventTypes(this);
      }
      this.workflow = value;
      if (value != null)
      {
         value.withEventTypes(this);
      }
      this.firePropertyChange(PROPERTY_WORKFLOW, oldValue, value);
      return this;
   }

   public List<ServiceNote> getHandlers()
   {
      return this.handlers != null ? Collections.unmodifiableList(this.handlers) : Collections.emptyList();
   }

   public EventType withHandlers(ServiceNote value)
   {
      if (this.handlers == null)
      {
         this.handlers = new ArrayList<>();
      }
      if (!this.handlers.contains(value))
      {
         this.handlers.add(value);
         value.withHandledEventTypes(this);
         this.firePropertyChange(PROPERTY_HANDLERS, null, value);
      }
      return this;
   }

   public EventType withHandlers(ServiceNote... value)
   {
      for (final ServiceNote item : value)
      {
         this.withHandlers(item);
      }
      return this;
   }

   public EventType withHandlers(Collection<? extends ServiceNote> value)
   {
      for (final ServiceNote item : value)
      {
         this.withHandlers(item);
      }
      return this;
   }

   public EventType withoutHandlers(ServiceNote value)
   {
      if (this.handlers != null && this.handlers.remove(value))
      {
         value.withoutHandledEventTypes(this);
         this.firePropertyChange(PROPERTY_HANDLERS, value, null);
      }
      return this;
   }

   public EventType withoutHandlers(ServiceNote... value)
   {
      for (final ServiceNote item : value)
      {
         this.withoutHandlers(item);
      }
      return this;
   }

   public EventType withoutHandlers(Collection<? extends ServiceNote> value)
   {
      for (final ServiceNote item : value)
      {
         this.withoutHandlers(item);
      }
      return this;
   }

   public String getEventTypeName()
   {
      return this.eventTypeName;
   }

   public EventType setEventTypeName(String value)
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

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder();
      result.append(' ').append(this.getEventTypeName());
      return result.substring(1);
   }

   public void removeYou()
   {
      this.withoutEvents(new ArrayList<>(this.getEvents()));
      this.setWorkflow(null);
      this.withoutHandlers(new ArrayList<>(this.getHandlers()));
   }
}
