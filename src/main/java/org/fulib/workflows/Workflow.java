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
   public static final String PROPERTY_SERVICES = "services";
   public static final String PROPERTY_POLICIES = "policies";
   public static final String PROPERTY_USER_INTERACTIONS = "userInteractions";
   public static final String PROPERTY_EVENT_STORMING_BOARD = "eventStormingBoard";
   private String name;
   private List<WorkflowNote> notes;
   private List<ServiceNote> services;
   private List<Policy> policies;
   private List<UserInteraction> userInteractions;
   private EventStormingBoard eventStormingBoard;
   public String currentTime = "12:00";

   public String getCurrentTime()
   {
      return currentTime;
   }

   public void setCurrentTime(String currentTime)
   {
      this.currentTime = currentTime;
   }

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

   public EventStormingBoard getEventStormingBoard()
   {
      return this.eventStormingBoard;
   }

   public Workflow setEventStormingBoard(EventStormingBoard value)
   {
      if (this.eventStormingBoard == value)
      {
         return this;
      }

      final EventStormingBoard oldValue = this.eventStormingBoard;
      if (this.eventStormingBoard != null)
      {
         this.eventStormingBoard = null;
         oldValue.withoutWorkflows(this);
      }
      this.eventStormingBoard = value;
      if (value != null)
      {
         value.withWorkflows(this);
      }
      this.firePropertyChange(PROPERTY_EVENT_STORMING_BOARD, oldValue, value);
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
      this.withoutNotes(new ArrayList<>(this.getNotes()));
      this.withoutServices(new ArrayList<>(this.getServices()));
      this.setEventStormingBoard(null);
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

   public String addToTime(String delta)
   {
      String[] deltaSplit = delta.split("\\:");
      String deltaSecs = deltaSplit[2];
      String deltaMins = deltaSplit[1];
      String[] currentSplit = currentTime.split("\\:");
      String hours = addOneTimePart(currentSplit[0], deltaSplit[0]);
      String mins = addOneTimePart(currentSplit[1], deltaSplit[1]);
      String secs = "00";
      if (currentSplit.length >= 3) {
         secs = currentSplit[2];
      }
      if (deltaSecs.equals("00")) {
         secs = deltaSecs;
      }
      else {
         secs = addOneTimePart(secs, deltaSplit[2]);
      }

      if (deltaMins.equals("00")) {
         mins = "00";
      }

      if (secs.equals("00")) {
         currentTime = String.format("%s:%s", hours, mins);
      } else {
         currentTime = String.format("%s:%s:%s", hours, mins, secs);
      }

      return currentTime;
   }

   private String addOneTimePart(String s1, String s2)
   {
      int t1 = Integer.parseInt(s1);
      int t2 = Integer.parseInt(s2);
      int sum = t1 + t2;
      String result = String.format("%02d", sum);
      return result;
   }
}
