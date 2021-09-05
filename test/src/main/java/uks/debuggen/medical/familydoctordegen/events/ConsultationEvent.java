package uks.debuggen.medical.familydoctordegen.events;
import java.util.Objects;

public class ConsultationEvent extends Event
{
   public static final String PROPERTY_PATIENT = "patient";
   public static final String PROPERTY_DATE = "date";
   private String patient;
   private String date;

   public String getPatient()
   {
      return this.patient;
   }

   public ConsultationEvent setPatient(String value)
   {
      if (Objects.equals(value, this.patient))
      {
         return this;
      }

      final String oldValue = this.patient;
      this.patient = value;
      this.firePropertyChange(PROPERTY_PATIENT, oldValue, value);
      return this;
   }

   public String getDate()
   {
      return this.date;
   }

   public ConsultationEvent setDate(String value)
   {
      if (Objects.equals(value, this.date))
      {
         return this;
      }

      final String oldValue = this.date;
      this.date = value;
      this.firePropertyChange(PROPERTY_DATE, oldValue, value);
      return this;
   }

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder(super.toString());
      result.append(' ').append(this.getPatient());
      result.append(' ').append(this.getDate());
      return result.toString();
   }
}
