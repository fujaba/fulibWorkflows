package org.fulib.workflows;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Collection;
import java.beans.PropertyChangeSupport;
import java.util.Objects;

public class EventStormingBoard
{
   public static final String PROPERTY_WORKFLOWS = "workflows";
   public static final String PROPERTY_SERVICES = "services";
   public static final String PROPERTY_NAME = "name";
   public static final String PROPERTY_EVENT_TYPES = "eventTypes";
   public static final String PROPERTY_USERS = "users";
   public static final String PROPERTY_DATA_TYPES = "dataTypes";
   private List<Workflow> workflows;
   protected PropertyChangeSupport listeners;
   private List<ServiceNote> services;
   private String name;
   private List<EventType> eventTypes;
   private List<UserNote> users;
   private List<DataType> dataTypes;

   public EventStormingBoard() {
      this.name = "some event storming";
   }

   public List<Workflow> getWorkflows()
   {
      return this.workflows != null ? Collections.unmodifiableList(this.workflows) : Collections.emptyList();
   }

   public EventStormingBoard withWorkflows(Workflow value)
   {
      if (this.workflows == null)
      {
         this.workflows = new ArrayList<>();
      }
      if (!this.workflows.contains(value))
      {
         this.workflows.add(value);
         value.setEventStormingBoard(this);
         this.firePropertyChange(PROPERTY_WORKFLOWS, null, value);
      }
      return this;
   }

   public EventStormingBoard withWorkflows(Workflow... value)
   {
      for (final Workflow item : value)
      {
         this.withWorkflows(item);
      }
      return this;
   }

   public EventStormingBoard withWorkflows(Collection<? extends Workflow> value)
   {
      for (final Workflow item : value)
      {
         this.withWorkflows(item);
      }
      return this;
   }

   public EventStormingBoard withoutWorkflows(Workflow value)
   {
      if (this.workflows != null && this.workflows.remove(value))
      {
         value.setEventStormingBoard(null);
         this.firePropertyChange(PROPERTY_WORKFLOWS, value, null);
      }
      return this;
   }

   public EventStormingBoard withoutWorkflows(Workflow... value)
   {
      for (final Workflow item : value)
      {
         this.withoutWorkflows(item);
      }
      return this;
   }

   public EventStormingBoard withoutWorkflows(Collection<? extends Workflow> value)
   {
      for (final Workflow item : value)
      {
         this.withoutWorkflows(item);
      }
      return this;
   }

   public List<ServiceNote> getServices()
   {
      return this.services != null ? Collections.unmodifiableList(this.services) : Collections.emptyList();
   }

   public EventStormingBoard withServices(ServiceNote value)
   {
      if (this.services == null)
      {
         this.services = new ArrayList<>();
      }
      if (!this.services.contains(value))
      {
         this.services.add(value);
         value.setEventStormingBoard(this);
         this.firePropertyChange(PROPERTY_SERVICES, null, value);
      }
      return this;
   }

   public EventStormingBoard withServices(ServiceNote... value)
   {
      for (final ServiceNote item : value)
      {
         this.withServices(item);
      }
      return this;
   }

   public EventStormingBoard withServices(Collection<? extends ServiceNote> value)
   {
      for (final ServiceNote item : value)
      {
         this.withServices(item);
      }
      return this;
   }

   public EventStormingBoard withoutServices(ServiceNote value)
   {
      if (this.services != null && this.services.remove(value))
      {
         value.setEventStormingBoard(null);
         this.firePropertyChange(PROPERTY_SERVICES, value, null);
      }
      return this;
   }

   public EventStormingBoard withoutServices(ServiceNote... value)
   {
      for (final ServiceNote item : value)
      {
         this.withoutServices(item);
      }
      return this;
   }

   public EventStormingBoard withoutServices(Collection<? extends ServiceNote> value)
   {
      for (final ServiceNote item : value)
      {
         this.withoutServices(item);
      }
      return this;
   }

   public String getName()
   {
      return this.name;
   }

   public EventStormingBoard setName(String value)
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

   public List<EventType> getEventTypes()
   {
      return this.eventTypes != null ? Collections.unmodifiableList(this.eventTypes) : Collections.emptyList();
   }

   public EventStormingBoard withEventTypes(EventType value)
   {
      if (this.eventTypes == null)
      {
         this.eventTypes = new ArrayList<>();
      }
      if (!this.eventTypes.contains(value))
      {
         this.eventTypes.add(value);
         value.setEventStormingBoard(this);
         this.firePropertyChange(PROPERTY_EVENT_TYPES, null, value);
      }
      return this;
   }

   public EventStormingBoard withEventTypes(EventType... value)
   {
      for (final EventType item : value)
      {
         this.withEventTypes(item);
      }
      return this;
   }

   public EventStormingBoard withEventTypes(Collection<? extends EventType> value)
   {
      for (final EventType item : value)
      {
         this.withEventTypes(item);
      }
      return this;
   }

   public EventStormingBoard withoutEventTypes(EventType value)
   {
      if (this.eventTypes != null && this.eventTypes.remove(value))
      {
         value.setEventStormingBoard(null);
         this.firePropertyChange(PROPERTY_EVENT_TYPES, value, null);
      }
      return this;
   }

   public EventStormingBoard withoutEventTypes(EventType... value)
   {
      for (final EventType item : value)
      {
         this.withoutEventTypes(item);
      }
      return this;
   }

   public EventStormingBoard withoutEventTypes(Collection<? extends EventType> value)
   {
      for (final EventType item : value)
      {
         this.withoutEventTypes(item);
      }
      return this;
   }

   public List<UserNote> getUsers()
   {
      return this.users != null ? Collections.unmodifiableList(this.users) : Collections.emptyList();
   }

   public EventStormingBoard withUsers(UserNote value)
   {
      if (this.users == null)
      {
         this.users = new ArrayList<>();
      }
      if (!this.users.contains(value))
      {
         this.users.add(value);
         value.setEventStormingBoard(this);
         this.firePropertyChange(PROPERTY_USERS, null, value);
      }
      return this;
   }

   public EventStormingBoard withUsers(UserNote... value)
   {
      for (final UserNote item : value)
      {
         this.withUsers(item);
      }
      return this;
   }

   public EventStormingBoard withUsers(Collection<? extends UserNote> value)
   {
      for (final UserNote item : value)
      {
         this.withUsers(item);
      }
      return this;
   }

   public EventStormingBoard withoutUsers(UserNote value)
   {
      if (this.users != null && this.users.remove(value))
      {
         value.setEventStormingBoard(null);
         this.firePropertyChange(PROPERTY_USERS, value, null);
      }
      return this;
   }

   public EventStormingBoard withoutUsers(UserNote... value)
   {
      for (final UserNote item : value)
      {
         this.withoutUsers(item);
      }
      return this;
   }

   public EventStormingBoard withoutUsers(Collection<? extends UserNote> value)
   {
      for (final UserNote item : value)
      {
         this.withoutUsers(item);
      }
      return this;
   }

   public List<DataType> getDataTypes()
   {
      return this.dataTypes != null ? Collections.unmodifiableList(this.dataTypes) : Collections.emptyList();
   }

   public EventStormingBoard withDataTypes(DataType value)
   {
      if (this.dataTypes == null)
      {
         this.dataTypes = new ArrayList<>();
      }
      if (!this.dataTypes.contains(value))
      {
         this.dataTypes.add(value);
         value.setEventStormingBoard(this);
         this.firePropertyChange(PROPERTY_DATA_TYPES, null, value);
      }
      return this;
   }

   public EventStormingBoard withDataTypes(DataType... value)
   {
      for (final DataType item : value)
      {
         this.withDataTypes(item);
      }
      return this;
   }

   public EventStormingBoard withDataTypes(Collection<? extends DataType> value)
   {
      for (final DataType item : value)
      {
         this.withDataTypes(item);
      }
      return this;
   }

   public EventStormingBoard withoutDataTypes(DataType value)
   {
      if (this.dataTypes != null && this.dataTypes.remove(value))
      {
         value.setEventStormingBoard(null);
         this.firePropertyChange(PROPERTY_DATA_TYPES, value, null);
      }
      return this;
   }

   public EventStormingBoard withoutDataTypes(DataType... value)
   {
      for (final DataType item : value)
      {
         this.withoutDataTypes(item);
      }
      return this;
   }

   public EventStormingBoard withoutDataTypes(Collection<? extends DataType> value)
   {
      for (final DataType item : value)
      {
         this.withoutDataTypes(item);
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

   public void removeYou()
   {
      this.withoutDataTypes(new ArrayList<>(this.getDataTypes()));
      this.withoutEventTypes(new ArrayList<>(this.getEventTypes()));
      this.withoutServices(new ArrayList<>(this.getServices()));
      this.withoutUsers(new ArrayList<>(this.getUsers()));
      this.withoutWorkflows(new ArrayList<>(this.getWorkflows()));
   }

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder();
      result.append(' ').append(this.getName());
      return result.substring(1);
   }

   public ServiceNote getFromServices(String serviceName)
   {
      for (ServiceNote service : getServices()) {
         if (service.getName().equals(serviceName)) {
            return service;
         }
      }
      return null;
   }

   public ServiceNote getOrCreateFromServices(String serviceName)
   {
      ServiceNote service = getFromServices(serviceName);
      if (service == null) {
         service = new ServiceNote();
         service.setName(serviceName)
               .setEventStormingBoard(this);
         service.setPort("" + (42000 + this.getServices().size()));
      }
      return service;
   }

   public EventType getOrCreateEventType(String eventTypeName)
   {
      for (EventType eventType : this.getEventTypes()) {
         if (eventType.getEventTypeName().equals(eventTypeName)) {
            return eventType;
         }
      }
      EventType eventType = new EventType();
      eventType.setEventTypeName(eventTypeName);
      eventType.setEventStormingBoard(this);
      return eventType;
   }

   public DataType getOrCreateDataType(String dataType)
   {
      for (DataType type : this.getDataTypes()) {
         if (type.getDataTypeName().equals(dataType)) {
            return type;
         }
      }
      DataType type = new DataType();
      type.setDataTypeName(dataType);
      type.setEventStormingBoard(this);
      return type;
   }

   public UserNote getFromUsers(String user)
   {
      for (UserNote userNote : this.getUsers()) {
         if (userNote.getName().equals(user)) {
            return userNote;
         }
      }
      return null;
   }

   public UserNote getOrCreateFromUsers(String actorName)
   {
      UserNote user = getFromUsers(actorName);
      if (user == null) {
         user = new UserNote();
         user.setName(actorName);
         user.setEventStormingBoard(this);
      }
      return user;
   }

}
