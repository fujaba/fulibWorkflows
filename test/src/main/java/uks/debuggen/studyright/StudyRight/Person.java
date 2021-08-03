package uks.debuggen.studyright.StudyRight;
import java.util.Objects;
import java.beans.PropertyChangeSupport;

public class Person
{
   public static final String PROPERTY_ID = "id";
   public static final String PROPERTY_NAME = "name";
   public static final String PROPERTY_BIRTH_YEAR = "birthYear";
   public static final String PROPERTY_UNI = "uni";
   private String id;
   private String name;
   private int birthYear;
   protected PropertyChangeSupport listeners;
   private University uni;

   public String getId()
   {
      return this.id;
   }

   public Person setId(String value)
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

   public String getName()
   {
      return this.name;
   }

   public Person setName(String value)
   {
      if (Objects.equals(value, this.name))
      {
         return this;
      }

      final String oldValue = this.name;
      this.name = value;
      this.firePropertyChange(PROPERTY_NAME, oldValue, value);
      return this;
   }

   public int getBirthYear()
   {
      return this.birthYear;
   }

   public Person setBirthYear(int value)
   {
      if (value == this.birthYear)
      {
         return this;
      }

      final int oldValue = this.birthYear;
      this.birthYear = value;
      this.firePropertyChange(PROPERTY_BIRTH_YEAR, oldValue, value);
      return this;
   }

   public University getUni()
   {
      return this.uni;
   }

   public Person setUni(University value)
   {
      if (this.uni == value)
      {
         return this;
      }

      final University oldValue = this.uni;
      if (this.uni != null)
      {
         this.uni = null;
         oldValue.withoutPersons(this);
      }
      this.uni = value;
      if (value != null)
      {
         value.withPersons(this);
      }
      this.firePropertyChange(PROPERTY_UNI, oldValue, value);
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
      result.append(' ').append(this.getName());
      return result.substring(1);
   }

   public void removeYou()
   {
      this.setUni(null);
   }
}
