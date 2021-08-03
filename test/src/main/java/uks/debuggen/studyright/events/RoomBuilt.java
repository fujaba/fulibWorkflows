package uks.debuggen.studyright.events;
import java.util.Objects;

public class RoomBuilt extends DataEvent
{
   public static final String PROPERTY_CREDITS = "credits";
   public static final String PROPERTY_UNI = "uni";
   public static final String PROPERTY_DOORS = "doors";
   private String credits;
   private String uni;
   private String doors;

   public String getCredits()
   {
      return this.credits;
   }

   public RoomBuilt setCredits(String value)
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

   public String getUni()
   {
      return this.uni;
   }

   public RoomBuilt setUni(String value)
   {
      if (Objects.equals(value, this.uni))
      {
         return this;
      }

      final String oldValue = this.uni;
      this.uni = value;
      this.firePropertyChange(PROPERTY_UNI, oldValue, value);
      return this;
   }

   public String getDoors()
   {
      return this.doors;
   }

   public RoomBuilt setDoors(String value)
   {
      if (Objects.equals(value, this.doors))
      {
         return this;
      }

      final String oldValue = this.doors;
      this.doors = value;
      this.firePropertyChange(PROPERTY_DOORS, oldValue, value);
      return this;
   }

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder(super.toString());
      result.append(' ').append(this.getCredits());
      result.append(' ').append(this.getUni());
      result.append(' ').append(this.getDoors());
      return result.toString();
   }
}
