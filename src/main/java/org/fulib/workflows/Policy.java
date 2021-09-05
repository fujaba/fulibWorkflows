package org.fulib.workflows;
import java.util.Objects;

public class Policy extends Interaction
{
   public static final String PROPERTY_SERVICE = "service";
   public static final String PROPERTY_TRIGGER = "trigger";
   public static final String PROPERTY_WORKFLOW = "workflow";
   public static final String PROPERTY_EXTERNAL_SYSTEM = "externalSystem";
   private ServiceNote service;
   private EventNote trigger;
   private Workflow workflow;
   private ExternalSystemNote externalSystem;

   public ServiceNote getService()
   {
      return this.service;
   }

   public Policy setService(ServiceNote value)
   {
      if (this.service == value)
      {
         return this;
      }

      final ServiceNote oldValue = this.service;
      if (this.service != null)
      {
         this.service = null;
         oldValue.withoutPolicies(this);
      }
      this.service = value;
      if (value != null)
      {
         value.withPolicies(this);
      }
      this.firePropertyChange(PROPERTY_SERVICE, oldValue, value);
      return this;
   }

   public EventNote getTrigger()
   {
      return this.trigger;
   }

   public Policy setTrigger(EventNote value)
   {
      if (this.trigger == value)
      {
         return this;
      }

      final EventNote oldValue = this.trigger;
      if (this.trigger != null)
      {
         this.trigger = null;
         oldValue.withoutPolicies(this);
      }
      this.trigger = value;
      if (value != null)
      {
         value.withPolicies(this);
      }
      this.firePropertyChange(PROPERTY_TRIGGER, oldValue, value);
      return this;
   }

   public Workflow getWorkflow()
   {
      return this.workflow;
   }

   public Policy setWorkflow(Workflow value)
   {
      if (this.workflow == value)
      {
         return this;
      }

      final Workflow oldValue = this.workflow;
      if (this.workflow != null)
      {
         this.workflow = null;
         oldValue.withoutPolicies(this);
      }
      this.workflow = value;
      if (value != null)
      {
         value.withPolicies(this);
      }
      this.firePropertyChange(PROPERTY_WORKFLOW, oldValue, value);
      return this;
   }

   public ExternalSystemNote getExternalSystem()
   {
      return this.externalSystem;
   }

   public Policy setExternalSystem(ExternalSystemNote value)
   {
      if (this.externalSystem == value)
      {
         return this;
      }

      final ExternalSystemNote oldValue = this.externalSystem;
      if (this.externalSystem != null)
      {
         this.externalSystem = null;
         oldValue.withoutPolicies(this);
      }
      this.externalSystem = value;
      if (value != null)
      {
         value.withPolicies(this);
      }
      this.firePropertyChange(PROPERTY_EXTERNAL_SYSTEM, oldValue, value);
      return this;
   }

   @Override
   public void removeYou()
   {
      super.removeYou();
      this.setService(null);
      this.setTrigger(null);
      this.setExternalSystem(null);
      this.setWorkflow(null);
   }
}
