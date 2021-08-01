package org.fulib.workflows;
import java.util.*;
import java.util.concurrent.Future;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Collections;
import java.util.Collection;

public class ServiceNote extends Note
{
   public static final String PROPERTY_PORT = "port";
   public static final String PROPERTY_POLICIES = "policies";
   public static final String PROPERTY_NAME = "name";
   public static final String PROPERTY_WORKFLOWS = "workflows";
   public static final String PROPERTY_HANDLED_EVENT_TYPES = "handledEventTypes";
   public static final String PROPERTY_EVENT_STORMING_BOARD = "eventStormingBoard";
   public static final String PROPERTY_PAGES = "pages";
   public static final String PROPERTY_HANDLED_DATA_TYPES = "handledDataTypes";
   private String port;
   private List<Policy> policies;
   private String name;
   private List<Workflow> workflows;
   private List<EventType> handledEventTypes;
   private EventStormingBoard eventStormingBoard;
   private List<PageNote> pages;
   private LinkedHashMap<String, String> objectMap = new LinkedHashMap<>();
   private List<DataType> handledDataTypes;

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

   public List<EventType> getHandledEventTypes()
   {
      return this.handledEventTypes != null ? Collections.unmodifiableList(this.handledEventTypes) : Collections.emptyList();
   }

   public ServiceNote withHandledEventTypes(EventType value)
   {
      if (this.handledEventTypes == null)
      {
         this.handledEventTypes = new ArrayList<>();
      }
      if (!this.handledEventTypes.contains(value))
      {
         this.handledEventTypes.add(value);
         value.withHandlers(this);
         this.firePropertyChange(PROPERTY_HANDLED_EVENT_TYPES, null, value);
      }
      return this;
   }

   public ServiceNote withHandledEventTypes(EventType... value)
   {
      for (final EventType item : value)
      {
         this.withHandledEventTypes(item);
      }
      return this;
   }

   public ServiceNote withHandledEventTypes(Collection<? extends EventType> value)
   {
      for (final EventType item : value)
      {
         this.withHandledEventTypes(item);
      }
      return this;
   }

   public ServiceNote withoutHandledEventTypes(EventType value)
   {
      if (this.handledEventTypes != null && this.handledEventTypes.remove(value))
      {
         value.withoutHandlers(this);
         this.firePropertyChange(PROPERTY_HANDLED_EVENT_TYPES, value, null);
      }
      return this;
   }

   public ServiceNote withoutHandledEventTypes(EventType... value)
   {
      for (final EventType item : value)
      {
         this.withoutHandledEventTypes(item);
      }
      return this;
   }

   public ServiceNote withoutHandledEventTypes(Collection<? extends EventType> value)
   {
      for (final EventType item : value)
      {
         this.withoutHandledEventTypes(item);
      }
      return this;
   }

   public EventStormingBoard getEventStormingBoard()
   {
      return this.eventStormingBoard;
   }

   public ServiceNote setEventStormingBoard(EventStormingBoard value)
   {
      if (this.eventStormingBoard == value)
      {
         return this;
      }

      final EventStormingBoard oldValue = this.eventStormingBoard;
      if (this.eventStormingBoard != null)
      {
         this.eventStormingBoard = null;
         oldValue.withoutServices(this);
      }
      this.eventStormingBoard = value;
      if (value != null)
      {
         value.withServices(this);
      }
      this.firePropertyChange(PROPERTY_EVENT_STORMING_BOARD, oldValue, value);
      return this;
   }

   public List<PageNote> getPages()
   {
      return this.pages != null ? Collections.unmodifiableList(this.pages) : Collections.emptyList();
   }

   public ServiceNote withPages(PageNote value)
   {
      if (this.pages == null)
      {
         this.pages = new ArrayList<>();
      }
      if (!this.pages.contains(value))
      {
         this.pages.add(value);
         value.setService(this);
         this.firePropertyChange(PROPERTY_PAGES, null, value);
      }
      return this;
   }

   public ServiceNote withPages(PageNote... value)
   {
      for (final PageNote item : value)
      {
         this.withPages(item);
      }
      return this;
   }

   public ServiceNote withPages(Collection<? extends PageNote> value)
   {
      for (final PageNote item : value)
      {
         this.withPages(item);
      }
      return this;
   }

   public ServiceNote withoutPages(PageNote value)
   {
      if (this.pages != null && this.pages.remove(value))
      {
         value.setService(null);
         this.firePropertyChange(PROPERTY_PAGES, value, null);
      }
      return this;
   }

   public ServiceNote withoutPages(PageNote... value)
   {
      for (final PageNote item : value)
      {
         this.withoutPages(item);
      }
      return this;
   }

   public ServiceNote withoutPages(Collection<? extends PageNote> value)
   {
      for (final PageNote item : value)
      {
         this.withoutPages(item);
      }
      return this;
   }

   public List<DataType> getHandledDataTypes()
   {
      return this.handledDataTypes != null ? Collections.unmodifiableList(this.handledDataTypes) : Collections.emptyList();
   }

   public ServiceNote withHandledDataTypes(DataType value)
   {
      if (this.handledDataTypes == null)
      {
         this.handledDataTypes = new ArrayList<>();
      }
      if (!this.handledDataTypes.contains(value))
      {
         this.handledDataTypes.add(value);
         value.withHandlers(this);
         this.firePropertyChange(PROPERTY_HANDLED_DATA_TYPES, null, value);
      }
      return this;
   }

   public ServiceNote withHandledDataTypes(DataType... value)
   {
      for (final DataType item : value)
      {
         this.withHandledDataTypes(item);
      }
      return this;
   }

   public ServiceNote withHandledDataTypes(Collection<? extends DataType> value)
   {
      for (final DataType item : value)
      {
         this.withHandledDataTypes(item);
      }
      return this;
   }

   public ServiceNote withoutHandledDataTypes(DataType value)
   {
      if (this.handledDataTypes != null && this.handledDataTypes.remove(value))
      {
         value.withoutHandlers(this);
         this.firePropertyChange(PROPERTY_HANDLED_DATA_TYPES, value, null);
      }
      return this;
   }

   public ServiceNote withoutHandledDataTypes(DataType... value)
   {
      for (final DataType item : value)
      {
         this.withoutHandledDataTypes(item);
      }
      return this;
   }

   public ServiceNote withoutHandledDataTypes(Collection<? extends DataType> value)
   {
      for (final DataType item : value)
      {
         this.withoutHandledDataTypes(item);
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
      this.withoutPages(new ArrayList<>(this.getPages()));
      this.withoutHandledDataTypes(new ArrayList<>(this.getHandledDataTypes()));
      this.withoutHandledEventTypes(new ArrayList<>(this.getHandledEventTypes()));
      this.withoutWorkflows(new ArrayList<>(this.getWorkflows()));
      this.setEventStormingBoard(null);
   }

   public LinkedHashMap<String, String> getObjectMap()
   {
      return this.objectMap;
   }
}
