package uks.debuggen.studyright.events;
import java.util.Objects;

public class StopBuilt extends DataEvent
{
   public static final String PROPERTY_MOTIVATION = "motivation";
   public static final String PROPERTY_ROOM = "room";
   public static final String PROPERTY_PREVIOUS_STOP = "previousStop";
   private String motivation;
   private String room;
   private String previousStop;

   public String getMotivation()
   {
      return this.motivation;
   }

   public StopBuilt setMotivation(String value)
   {
      if (Objects.equals(value, this.motivation))
      {
         return this;
      }

      final String oldValue = this.motivation;
      this.motivation = value;
      this.firePropertyChange(PROPERTY_MOTIVATION, oldValue, value);
      return this;
   }

   public String getRoom()
   {
      return this.room;
   }

   public StopBuilt setRoom(String value)
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

   public StopBuilt setPreviousStop(String value)
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
      result.append(' ').append(this.getMotivation());
      result.append(' ').append(this.getRoom());
      result.append(' ').append(this.getPreviousStop());
      return result.toString();
   }
}
