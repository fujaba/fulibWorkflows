package uks.debuggen.studyright.StudyRight;
import java.util.Objects;
import java.beans.PropertyChangeSupport;

public class Room
{
   public static final String PROPERTY_ID = "id";
   public static final String PROPERTY_CREDITS = "credits";
   public static final String PROPERTY_DOORS = "doors";
   private String id;
   private String credits;
   private String doors;
   protected PropertyChangeSupport listeners;

   public String getId()
   {
      return this.id;
   }

   public Room setId(String value)
   {
      if (Objects.equals(value, this.id))
      {
         return this;
      }

      final String oldValue = this.id;
      this.id = value;
      this.firePropertyChange(PROPERTY_ID, oldValue, value);
      return this;
   }

   public String getCredits()
   {
      return this.credits;
   }

   public Room setCredits(String value)
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

   public String getDoors()
   {
      return this.doors;
   }

   public Room setDoors(String value)
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

   public boolean firePropertyChange(String propertyName, Object oldValue, Object newValue)
   {
      if (this.listeners != null)
      {
         this.listeners.firePropertyChange(propertyName, oldValue, newValue);
         return true;
      }
      return false;
   }

   public PropertyChangeSupport listeners()
   {
      if (this.listeners == null)
      {
         this.listeners = new PropertyChangeSupport(this);
      }
      return this.listeners;
   }

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder();
      result.append(' ').append(this.getId());
      result.append(' ').append(this.getCredits());
      result.append(' ').append(this.getDoors());
      return result.substring(1);
   }
}
