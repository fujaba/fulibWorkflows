package org.fulib.workflows;
import java.util.Objects;

public class SubprocessNote extends WorkflowNote
{
   public static final String PROPERTY_SUBPROCESS_NAME = "subprocessName";
   public static final String PROPERTY_SUBPROCESS = "subprocess";
   private String subprocessName;
   private Workflow subprocess;

   public String getSubprocessName()
   {
      return this.subprocessName;
   }

   public SubprocessNote setSubprocessName(String value)
   {
      if (Objects.equals(value, this.subprocessName))
      {
         return this;
      }

      final String oldValue = this.subprocessName;
      this.subprocessName = value;
      this.firePropertyChange(PROPERTY_SUBPROCESS_NAME, oldValue, value);
      return this;
   }

   public Workflow getSubprocess()
   {
      return this.subprocess;
   }

   public SubprocessNote setSubprocess(Workflow value)
   {
      if (Objects.equals(value, this.subprocess))
      {
         return this;
      }

      final Workflow oldValue = this.subprocess;
      this.subprocess = value;
      this.firePropertyChange(PROPERTY_SUBPROCESS, oldValue, value);
      return this;
   }

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder(super.toString());
      result.append(' ').append(this.getSubprocessName());
      return result.toString();
   }
}
