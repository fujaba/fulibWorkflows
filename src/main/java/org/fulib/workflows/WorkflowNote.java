package org.fulib.workflows;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Collections;
import java.util.Collection;

public class WorkflowNote extends Note
{
   public static final String PROPERTY_TIME = "time";
   public static final String PROPERTY_WORKFLOW = "workflow";
   public static final String PROPERTY_INTERACTION = "interaction";
   private String time;
   private Workflow workflow;
   private Interaction interaction;

   public String getTime()
   {
      return this.time;
   }

   public WorkflowNote setTime(String value)
   {
      if (Objects.equals(value, this.time))
      {
         return this;
      }

      final String oldValue = this.time;
      this.time = value;
      this.firePropertyChange(PROPERTY_TIME, oldValue, value);
      return this;
   }

   public Workflow getWorkflow()
   {
      return this.workflow;
   }

   public WorkflowNote setWorkflow(Workflow value)
   {
      if (this.workflow == value)
      {
         return this;
      }

      final Workflow oldValue = this.workflow;
      if (this.workflow != null)
      {
         this.workflow = null;
         oldValue.withoutNotes(this);
      }
      this.workflow = value;
      if (value != null)
      {
         value.withNotes(this);
      }
      this.firePropertyChange(PROPERTY_WORKFLOW, oldValue, value);
      return this;
   }

   public Interaction getInteraction()
   {
      return this.interaction;
   }

   public WorkflowNote setInteraction(Interaction value)
   {
      if (this.interaction == value)
      {
         return this;
      }

      final Interaction oldValue = this.interaction;
      if (this.interaction != null)
      {
         this.interaction = null;
         oldValue.withoutSteps(this);
      }
      this.interaction = value;
      if (value != null)
      {
         value.withSteps(this);
      }
      this.firePropertyChange(PROPERTY_INTERACTION, oldValue, value);
      return this;
   }

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder(super.toString());
      result.append(' ').append(this.getTime());
      return result.toString();
   }

   public void removeYou()
   {
      this.setInteraction(null);
      this.setWorkflow(null);
   }
}
