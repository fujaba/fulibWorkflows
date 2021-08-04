package uks.debuggen.studyright.events;
import java.util.Objects;

public class VisitRoom extends Event
{
   public static final String PROPERTY_COMMAND = "command";
   public static final String PROPERTY_ROOM = "room";
   public static final String PROPERTY_PREVIOUS_STOP = "previousStop";
   private String command;
   private String room;
   private String previousStop;

   public String getCommand()
   {
      return this.command;
   }

   public VisitRoom setCommand(String value)
   {
      if (Objects.equals(value, this.command))
      {
         return this;
      }

      final String oldValue = this.command;
      this.command = value;
      this.firePropertyChange(PROPERTY_COMMAND, oldValue, value);
      return this;
   }

   public String getRoom()
   {
      return this.room;
   }

   public VisitRoom setRoom(String value)
   {
      if (Objects.equals(value, this.room))
      {
         return this;
      }

      final String oldValue = this.room;
      this.room = value;
      this.firePropertyChange(PROPERTY_ROOM, oldValue, value);
      return this;
   }

   public String getPreviousStop()
   {
      return this.previousStop;
   }

   public VisitRoom setPreviousStop(String value)
   {
      if (Objects.equals(value, this.previousStop))
      {
         return this;
      }

      final String oldValue = this.previousStop;
      this.previousStop = value;
      this.firePropertyChange(PROPERTY_PREVIOUS_STOP, oldValue, value);
      return this;
   }

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder(super.toString());
      result.append(' ').append(this.getCommand());
      result.append(' ').append(this.getRoom());
      result.append(' ').append(this.getPreviousStop());
      return result.toString();
   }
}
