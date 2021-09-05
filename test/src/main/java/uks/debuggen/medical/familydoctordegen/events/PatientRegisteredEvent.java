package uks.debuggen.medical.familydoctordegen.events;
import java.util.Objects;

public class PatientRegisteredEvent extends Event
{
   public static final String PROPERTY_NAME = "name";
   public static final String PROPERTY_ADDRESS = "address";
   public static final String PROPERTY_BIRTH_DATE = "birthDate";
   private String name;
   private String address;
   private String birthDate;

   public String getName()
   {
      return this.name;
   }

   public PatientRegisteredEvent setName(String value)
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

   public String getAddress()
   {
      return this.address;
   }

   public PatientRegisteredEvent setAddress(String value)
   {
      if (Objects.equals(value, this.address))
      {
         return this;
      }

      final String oldValue = this.address;
      this.address = value;
      this.firePropertyChange(PROPERTY_ADDRESS, oldValue, value);
      return this;
   }

   public String getBirthDate()
   {
      return this.birthDate;
   }

   public PatientRegisteredEvent setBirthDate(String value)
   {
      if (Objects.equals(value, this.birthDate))
      {
         return this;
      }

      final String oldValue = this.birthDate;
      this.birthDate = value;
      this.firePropertyChange(PROPERTY_BIRTH_DATE, oldValue, value);
      return this;
   }

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder(super.toString());
      result.append(' ').append(this.getName());
      result.append(' ').append(this.getAddress());
      result.append(' ').append(this.getBirthDate());
      return result.toString();
   }
}
