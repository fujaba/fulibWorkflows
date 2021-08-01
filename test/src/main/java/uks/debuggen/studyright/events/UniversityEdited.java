package uks.debuggen.studyright.events;
import java.util.Objects;

public class UniversityEdited extends DataEvent
{
   public static final String PROPERTY_ROOMS = "rooms";
   private String rooms;

   public String getRooms()
   {
      return this.rooms;
   }

   public UniversityEdited setRooms(String value)
   {
      if (Objects.equals(value, this.rooms))
      {
         return this;
      }

      final String oldValue = this.rooms;
      this.rooms = value;
      this.firePropertyChange(PROPERTY_ROOMS, oldValue, value);
      return this;
   }

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder(super.toString());
      result.append(' ').append(this.getRooms());
      return result.toString();
   }
}
