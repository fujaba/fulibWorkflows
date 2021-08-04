package uks.debuggen.studyright.events;
import java.util.Objects;

public class TourFailed extends Event
{
   public static final String PROPERTY_STOP = "stop";
   public static final String PROPERTY_ROOM = "room";
   public static final String PROPERTY_CREDITS = "credits";
   private String stop;
   private String room;
   private String credits;

   public String getStop()
   {
      return this.stop;
   }

   public TourFailed setStop(String value)
   {
      if (Objects.equals(value, this.stop))
      {
         return this;
      }

      final String oldValue = this.stop;
      this.stop = value;
      this.firePropertyChange(PROPERTY_STOP, oldValue, value);
      return this;
   }

   public String getRoom()
   {
      return this.room;
   }

   public TourFailed setRoom(String value)
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

   public String getCredits()
   {
      return this.credits;
   }

   public TourFailed setCredits(String value)
   {
      if (Objects.equals(value, this.credits))
      {
         return this;
      }

      final String oldValue = this.credits;
      this.credits = value;
      this.firePropertyChange(PROPERTY_CREDITS, oldValue, value);
      return this;
   }

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder(super.toString());
      result.append(' ').append(this.getStop());
      result.append(' ').append(this.getRoom());
      result.append(' ').append(this.getCredits());
      return result.toString();
   }
}
