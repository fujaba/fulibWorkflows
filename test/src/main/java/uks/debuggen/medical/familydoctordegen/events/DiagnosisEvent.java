package uks.debuggen.medical.familydoctordegen.events;
import java.util.Objects;

public class DiagnosisEvent extends Event
{
   public static final String PROPERTY_DIAGNOSIS = "diagnosis";
   public static final String PROPERTY_CONSULTATION = "consultation";
   private String diagnosis;
   private String consultation;

   public String getDiagnosis()
   {
      return this.diagnosis;
   }

   public DiagnosisEvent setDiagnosis(String value)
   {
      if (Objects.equals(value, this.diagnosis))
      {
         return this;
      }

      final String oldValue = this.diagnosis;
      this.diagnosis = value;
      this.firePropertyChange(PROPERTY_DIAGNOSIS, oldValue, value);
      return this;
   }

   public String getConsultation()
   {
      return this.consultation;
   }

   public DiagnosisEvent setConsultation(String value)
   {
      if (Objects.equals(value, this.consultation))
      {
         return this;
      }

      final String oldValue = this.consultation;
      this.consultation = value;
      this.firePropertyChange(PROPERTY_CONSULTATION, oldValue, value);
      return this;
   }

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder(super.toString());
      result.append(' ').append(this.getDiagnosis());
      result.append(' ').append(this.getConsultation());
      return result.toString();
   }
}
