package uks.debuggen.medical.events;
import java.util.Objects;

public class TreatmentInitiatedEvent extends Event
{
   public static final String PROPERTY_TREATMENT = "treatment";
   public static final String PROPERTY_CONSULTATION = "consultation";
   private String treatment;
   private String consultation;

   public String getTreatment()
   {
      return this.treatment;
   }

   public TreatmentInitiatedEvent setTreatment(String value)
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

   public String getConsultation()
   {
      return this.consultation;
   }

   public TreatmentInitiatedEvent setConsultation(String value)
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
      result.append(' ').append(this.getTreatment());
      result.append(' ').append(this.getConsultation());
      return result.toString();
   }
}
