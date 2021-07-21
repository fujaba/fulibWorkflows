package org.fulib.workflows;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Collections;
import java.util.Collection;

public class Workflow extends Note
{
   public static final String PROPERTY_NAME = "name";
   public static final String PROPERTY_NOTES = "notes";
   public static final String PROPERTY_USERS = "users";
   public static final String PROPERTY_SERVICES = "services";
   public static final String PROPERTY_POLICIES = "policies";
   public static final String PROPERTY_USER_INTERACTIONS = "userInteractions";
   public static final String PROPERTY_EVENT_TYPES = "eventTypes";
   private String name;
   private List<WorkflowNote> notes;
   private List<UserNote> users;
   private List<ServiceNote> services;
   private List<Policy> policies;
   private List<UserInteraction> userInteractions;
   private List<EventType> eventTypes;

   public String getName()
   {
      return this.name;
   }

   public Workflow setName(String value)
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

   public List<WorkflowNote> getNotes()
   {
      return this.notes != null ? Collections.unmodifiableList(this.notes) : Collections.emptyList();
   }

   public Workflow withNotes(WorkflowNote value)
   {
      if (this.notes == null)
      {
         this.notes = new ArrayList<>();
      }
      if (!this.notes.contains(value))
      {
         this.notes.add(value);
         value.setWorkflow(this);
         this.firePropertyChange(PROPERTY_NOTES, null, value);
      }
      return this;
   }

   public Workflow withNotes(WorkflowNote... value)
   {
      for (final WorkflowNote item : value)
      {
         this.withNotes(item);
      }
      return this;
   }

   public Workflow withNotes(Collection<? extends WorkflowNote> value)
   {
      for (final WorkflowNote item : value)
      {
         this.withNotes(item);
      }
      return this;
   }

   public Workflow withoutNotes(WorkflowNote value)
   {
      if (this.notes != null && this.notes.remove(value))
      {
         value.setWorkflow(null);
         this.firePropertyChange(PROPERTY_NOTES, value, null);
      }
      return this;
   }

   public Workflow withoutNotes(WorkflowNote... value)
   {
      for (final WorkflowNote item : value)
      {
         this.withoutNotes(item);
      }
      return this;
   }

   public Workflow withoutNotes(Collection<? extends WorkflowNote> value)
   {
      for (final WorkflowNote item : value)
      {
         this.withoutNotes(item);
      }
      return this;
   }

   public List<UserNote> getUsers()
   {
      return this.users != null ? Collections.unmodifiableList(this.users) : Collections.emptyList();
   }

   public Workflow withUsers(UserNote value)
   {
      if (this.users == null)
      {
         this.users = new ArrayList<>();
      }
      if (!this.users.contains(value))
      {
         this.users.add(value);
         value.withWorkflows(this);
         this.firePropertyChange(PROPERTY_USERS, null, value);
      }
      return this;
   }

   public Workflow withUsers(UserNote... value)
   {
      for (final UserNote item : value)
      {
         this.withUsers(item);
      }
      return this;
   }

   public Workflow withUsers(Collection<? extends UserNote> value)
   {
      for (final UserNote item : value)
      {
         this.withUsers(item);
      }
      return this;
   }

   public Workflow withoutUsers(UserNote value)
   {
      if (this.users != null && this.users.remove(value))
      {
         value.withoutWorkflows(this);
         this.firePropertyChange(PROPERTY_USERS, value, null);
      }
      return this;
   }

   public Workflow withoutUsers(UserNote... value)
   {
      for (final UserNote item : value)
      {
         this.withoutUsers(item);
      }
      return this;
   }

   public Workflow withoutUsers(Collection<? extends UserNote> value)
   {
      for (final UserNote item : value)
      {
         this.withoutUsers(item);
      }
      return this;
   }

   public List<ServiceNote> getServices()
   {
      return this.services != null ? Collections.unmodifiableList(this.services) : Collections.emptyList();
   }

   public Workflow withServices(ServiceNote value)
   {
      if (this.services == null)
      {
         this.services = new ArrayList<>();
      }
      if (!this.services.contains(value))
      {
         this.services.add(value);
         value.withWorkflows(this);
         this.firePropertyChange(PROPERTY_SERVICES, null, value);
      }
      return this;
   }

   public Workflow withServices(ServiceNote... value)
   {
      for (final ServiceNote item : value)
      {
         this.withServices(item);
      }
      return this;
   }

   public Workflow withServices(Collection<? extends ServiceNote> value)
   {
      for (final ServiceNote item : value)
      {
         this.withServices(item);
      }
      return this;
   }

   public Workflow withoutServices(ServiceNote value)
   {
      if (this.services != null && this.services.remove(value))
      {
         value.withoutWorkflows(this);
         this.firePropertyChange(PROPERTY_SERVICES, value, null);
      }
      return this;
   }

   public Workflow withoutServices(ServiceNote... value)
   {
      for (final ServiceNote item : value)
      {
         this.withoutServices(item);
      }
      return this;
   }

   public Workflow withoutServices(Collection<? extends ServiceNote> value)
   {
      for (final ServiceNote item : value)
      {
         this.withoutServices(item);
      }
      return this;
   }

   public List<Policy> getPolicies()
   {
      return this.policies != null ? Collections.unmodifiableList(this.policies) : Collections.emptyList();
   }

   public Workflow withPolicies(Policy value)
   {
      if (this.policies == null)
      {
         this.policies = new ArrayList<>();
      }
      if (!this.policies.contains(value))
      {
         this.policies.add(value);
         value.setWorkflow(this);
         this.firePropertyChange(PROPERTY_POLICIES, null, value);
      }
      return this;
   }

   public Workflow withPolicies(Policy... value)
   {
      for (final Policy item : value)
      {
         this.withPolicies(item);
      }
      return this;
   }

   public Workflow withPolicies(Collection<? extends Policy> value)
   {
      for (final Policy item : value)
      {
         this.withPolicies(item);
      }
      return this;
   }

   public Workflow withoutPolicies(Policy value)
   {
      if (this.policies != null && this.policies.remove(value))
      {
         value.setWorkflow(null);
         this.firePropertyChange(PROPERTY_POLICIES, value, null);
      }
      return this;
   }

   public Workflow withoutPolicies(Policy... value)
   {
      for (final Policy item : value)
      {
         this.withoutPolicies(item);
      }
      return this;
   }

   public Workflow withoutPolicies(Collection<? extends Policy> value)
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

   public Workflow withUserInteractions(UserInteraction value)
   {
      if (this.userInteractions == null)
      {
         this.userInteractions = new ArrayList<>();
      }
      if (!this.userInteractions.contains(value))
      {
         this.userInteractions.add(value);
         value.setWorkflow(this);
         this.firePropertyChange(PROPERTY_USER_INTERACTIONS, null, value);
      }
      return this;
   }

   public Workflow withUserInteractions(UserInteraction... value)
   {
      for (final UserInteraction item : value)
      {
         this.withUserInteractions(item);
      }
      return this;
   }

   public Workflow withUserInteractions(Collection<? extends UserInteraction> value)
   {
      for (final UserInteraction item : value)
      {
         this.withUserInteractions(item);
      }
      return this;
   }

   public Workflow withoutUserInteractions(UserInteraction value)
   {
      if (this.userInteractions != null && this.userInteractions.remove(value))
      {
         value.setWorkflow(null);
         this.firePropertyChange(PROPERTY_USER_INTERACTIONS, value, null);
      }
      return this;
   }

   public Workflow withoutUserInteractions(UserInteraction... value)
   {
      for (final UserInteraction item : value)
      {
         this.withoutUserInteractions(item);
      }
      return this;
   }

   public Workflow withoutUserInteractions(Collection<? extends UserInteraction> value)
   {
      for (final UserInteraction item : value)
      {
         this.withoutUserInteractions(item);
      }
      return this;
   }

   public List<EventType> getEventTypes()
   {
      return this.eventTypes != null ? Collections.unmodifiableList(this.eventTypes) : Collections.emptyList();
   }

   public Workflow withEventTypes(EventType value)
   {
      if (this.eventTypes == null)
      {
         this.eventTypes = new ArrayList<>();
      }
      if (!this.eventTypes.contains(value))
      {
         this.eventTypes.add(value);
         value.setWorkflow(this);
         this.firePropertyChange(PROPERTY_EVENT_TYPES, null, value);
      }
      return this;
   }

   public Workflow withEventTypes(EventType... value)
   {
      for (final EventType item : value)
      {
         this.withEventTypes(item);
      }
      return this;
   }

   public Workflow withEventTypes(Collection<? extends EventType> value)
   {
      for (final EventType item : value)
      {
         this.withEventTypes(item);
      }
      return this;
   }

   public Workflow withoutEventTypes(EventType value)
   {
      if (this.eventTypes != null && this.eventTypes.remove(value))
      {
         value.setWorkflow(null);
         this.firePropertyChange(PROPERTY_EVENT_TYPES, value, null);
      }
      return this;
   }

   public Workflow withoutEventTypes(EventType... value)
   {
      for (final EventType item : value)
      {
         this.withoutEventTypes(item);
      }
      return this;
   }

   public Workflow withoutEventTypes(Collection<? extends EventType> value)
   {
      for (final EventType item : value)
      {
         this.withoutEventTypes(item);
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
      this.withoutPolicies(new ArrayList<>(this.getPolicies()));
      this.withoutUserInteractions(new ArrayList<>(this.getUserInteractions()));
      this.withoutEventTypes(new ArrayList<>(this.getEventTypes()));
      this.withoutNotes(new ArrayList<>(this.getNotes()));
      this.withoutServices(new ArrayList<>(this.getServices()));
      this.withoutUsers(new ArrayList<>(this.getUsers()));
   }

   public WorkflowNote getFromNotes(String triggerTime)
   {
      for (WorkflowNote note : this.getNotes()) {
         if (triggerTime.equals(note.getTime())) {
            return note;
         }
      }
      return null;
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

   public ServiceNote getFromServices(String serviceName)
   {
      for (ServiceNote service : this.getServices()) {
         if (service.getName().equals(serviceName)) {
            return service;
         }
      }
      return null;
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
      eventType.setWorkflow(this);
      return eventType;
   }

   public UserNote getOrCreateFromUsers(String actorName)
   {
      UserNote user = getFromUsers(actorName);
      if (user == null) {
         user = new UserNote();
         user.setName(actorName);
         user.withWorkflows(this);
      }
      return user;
   }
}
