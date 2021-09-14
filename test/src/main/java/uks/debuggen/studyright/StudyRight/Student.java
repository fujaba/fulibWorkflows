package uks.debuggen.studyright.StudyRight;
import java.util.Objects;
import java.beans.PropertyChangeSupport;

public class Student
extends Person {
   public static final String PROPERTY_STUDENT_ID = "studentId";
   public static final String PROPERTY_NAME = "name";
   public static final String PROPERTY_BIRTH_YEAR = "birthYear";
   public static final String PROPERTY_UNI = "uni";
   private String studentId;
   private String name;
   private String birthYear;
   private String uni;

   public String getStudentId()
   {
      return this.studentId;
   }

   public Student setStudentId(String value)
   {
      if (Objects.equals(value, this.studentId))
      {
         return this;
      }

      final String oldValue = this.studentId;
      this.studentId = value;
      this.firePropertyChange(PROPERTY_STUDENT_ID, oldValue, value);
      return this;
   }

   public String getName()
   {
      return this.name;
   }

   public Student setName(String value)
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

   public String getBirthYear()
   {
      return this.birthYear;
   }

   public Student setBirthYear(String value)
   {
      if (Objects.equals(value, this.birthYear))
      {
         return this;
      }

      final String oldValue = this.birthYear;
      this.birthYear = value;
      this.firePropertyChange(PROPERTY_BIRTH_YEAR, oldValue, value);
      return this;
   }

   public String getUni()
   {
      return this.uni;
   }

   public Student setUni(String value)
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

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder(super.toString());
      result.append(' ').append(this.getName());
      result.append(' ').append(this.getBirthYear());
      result.append(' ').append(this.getStudentId());
      result.append(' ').append(this.getUni());
      return result.toString();
   }
}
