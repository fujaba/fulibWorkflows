package org.fulib.workflows;
import java.beans.PropertyChangeSupport;

public class UserInteraction
extends Interaction {
   public static final String PROPERTY_USER = "user";
   public static final String PROPERTY_SERVICE = "service";
   public static final String PROPERTY_WORKFLOW = "workflow";
   private UserNote user;
   private ServiceNote service;
   private Workflow workflow;

   public UserNote getUser()
   {
      return this.user;
   }

   public UserInteraction setUser(UserNote value)
   {
      if (this.user == value)
      {
         return this;
      }

      final UserNote oldValue = this.user;
      if (this.user != null)
      {
         this.user = null;
         oldValue.withoutInteractions(this);
      }
      this.user = value;
      if (value != null)
      {
         value.withInteractions(this);
      }
      this.firePropertyChange(PROPERTY_USER, oldValue, value);
      return this;
   }

   public ServiceNote getService()
   {
      return this.service;
   }

   public UserInteraction setService(ServiceNote value)
   {
      if (this.service == value)
      {
         return this;
      }

      final ServiceNote oldValue = this.service;
      if (this.service != null)
      {
         this.service = null;
         oldValue.withoutUserInteractions(this);
      }
      this.service = value;
      if (value != null)
      {
         value.withUserInteractions(this);
      }
      this.firePropertyChange(PROPERTY_SERVICE, oldValue, value);
      return this;
   }

   public Workflow getWorkflow()
   {
      return this.workflow;
   }

   public UserInteraction setWorkflow(Workflow value)
   {
      if (this.workflow == value)
      {
         return this;
      }

      final Workflow oldValue = this.workflow;
      if (this.workflow != null)
      {
         this.workflow = null;
         oldValue.withoutUserInteractions(this);
      }
      this.workflow = value;
      if (value != null)
      {
         value.withUserInteractions(this);
      }
      this.firePropertyChange(PROPERTY_WORKFLOW, oldValue, value);
      return this;
   }

   @Override
   public void removeYou()
   {
      super.removeYou();
      this.setUser(null);
      this.setService(null);
      this.setWorkflow(null);
   }
}