package uks.debuggen.medical.events;
import java.util.Objects;

public class GeneralPricesAddedEvent extends Event
{
   public static final String PROPERTY_CONSULTATION = "consultation";
   public static final String PROPERTY_DIAGNOSIS = "diagnosis";
   public static final String PROPERTY_TREATMENT = "treatment";
   private String consultation;
   private String diagnosis;
   private String treatment;

   public String getConsultation()
   {
      return this.consultation;
   }

   public GeneralPricesAddedEvent setConsultation(String value)
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

   public String getDiagnosis()
   {
      return this.diagnosis;
   }

   public GeneralPricesAddedEvent setDiagnosis(String value)
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

   public String getTreatment()
   {
      return this.treatment;
   }

   public GeneralPricesAddedEvent setTreatment(String value)
   {
      if (Objects.equals(value, this.treatment))
      {
         return this;
      }

      final String oldValue = this.treatment;
      this.treatment = value;
      this.firePropertyChange(PROPERTY_TREATMENT, oldValue, value);
      return this;
   }

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder(super.toString());
      result.append(' ').append(this.getConsultation());
      result.append(' ').append(this.getDiagnosis());
      result.append(' ').append(this.getTreatment());
      return result.toString();
   }
}
