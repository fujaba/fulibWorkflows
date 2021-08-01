package uks.debuggen.studyright.events;
import java.util.Objects;

public class RoomSelected extends Event
{
   public static final String PROPERTY_EVENT = "event";
   public static final String PROPERTY_ROOM = "room";
   public static final String PROPERTY_PREVIOUS_STOP = "previousStop";
   private String event;
   private String room;
   private String previousStop;

   public String getEvent()
   {
      return this.event;
   }

   public RoomSelected setEvent(String value)
   {
      if (Objects.equals(value, this.event))
      {
         return this;
      }

      final String oldValue = this.event;
      this.event = value;
      this.firePropertyChange(PROPERTY_EVENT, oldValue, value);
      return this;
   }

   public String getRoom()
   {
      return this.room;
   }

   public RoomSelected setRoom(String value)
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

   public RoomSelected setPreviousStop(String value)
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
      result.append(' ').append(this.getEvent());
      result.append(' ').append(this.getRoom());
      result.append(' ').append(this.getPreviousStop());
      return result.toString();
   }
}
