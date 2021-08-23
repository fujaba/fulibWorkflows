package uks.debuggen.medical.events;
import java.util.Objects;

public class DiagnosisDoneEvent extends Event
{
   public static final String PROPERTY_DISEASE = "disease";
   public static final String PROPERTY_DIAGNOSIS = "diagnosis";
   public static final String PROPERTY_CONSULTATION = "consultation";
   private String disease;
   private String diagnosis;
   private String consultation;

   public String getDisease()
   {
      return this.disease;
   }

   public DiagnosisDoneEvent setDisease(String value)
   {
      if (Objects.equals(value, this.disease))
      {
         return this;
      }

      final String oldValue = this.disease;
      this.disease = value;
      this.firePropertyChange(PROPERTY_DISEASE, oldValue, value);
      return this;
   }

   public String getDiagnosis()
   {
      return this.diagnosis;
   }

   public DiagnosisDoneEvent setDiagnosis(String value)
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

   public DiagnosisDoneEvent setConsultation(String value)
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
      result.append(' ').append(this.getDisease());
      result.append(' ').append(this.getDiagnosis());
      result.append(' ').append(this.getConsultation());
      return result.toString();
   }
}
