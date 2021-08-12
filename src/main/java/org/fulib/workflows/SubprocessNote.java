package org.fulib.workflows;
import java.util.Objects;

public class SubprocessNote extends WorkflowNote
{
   public static final String PROPERTY_SUBPROCESS_NAME = "subprocessName";
   public static final String PROPERTY_SUBPROCESS = "subprocess";
   public static final String PROPERTY_KIND = "kind";
   public static final String PROPERTY_EVENT_STORMING_BOARD = "eventStormingBoard";
   private String subprocessName;
   private Workflow subprocess;
   private String kind;
   private EventStormingBoard eventStormingBoard;

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

   public String getKind()
   {
      return this.kind;
   }

   public SubprocessNote setKind(String value)
   {
      if (Objects.equals(value, this.kind))
      {
         return this;
      }

      final String oldValue = this.kind;
      this.kind = value;
      this.firePropertyChange(PROPERTY_KIND, oldValue, value);
      return this;
   }

   public EventStormingBoard getEventStormingBoard()
   {
      return this.eventStormingBoard;
   }

   public SubprocessNote setEventStormingBoard(EventStormingBoard value)
   {
      if (this.eventStormingBoard == value)
      {
         return this;
      }

      final EventStormingBoard oldValue = this.eventStormingBoard;
      if (this.eventStormingBoard != null)
      {
         this.eventStormingBoard = null;
         oldValue.withoutSubprocesses(this);
      }
      this.eventStormingBoard = value;
      if (value != null)
      {
         value.withSubprocesses(this);
      }
      this.firePropertyChange(PROPERTY_EVENT_STORMING_BOARD, oldValue, value);
      return this;
   }

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder(super.toString());
      result.append(' ').append(this.getSubprocessName());
      result.append(' ').append(this.getKind());
      return result.toString();
   }

   @Override
   public void removeYou()
   {
      super.removeYou();
      this.setEventStormingBoard(null);
   }
}
