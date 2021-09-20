package uks.debuggen.medical.familydoctordegen.events;
import java.util.Objects;

public class SymptomValidatedEvent extends Event
{
   public static final String PROPERTY_SYMPTOM = "symptom";
   public static final String PROPERTY_CONSULTATION = "consultation";
   public static final String PROPERTY_CONSULTATIONS = "consultations";
   private String symptom;
   private String consultation;
   private String consultations;

   public String getSymptom()
   {
      return this.symptom;
   }

   public SymptomValidatedEvent setSymptom(String value)
   {
      if (Objects.equals(value, this.symptom))
      {
         return this;
      }

      final String oldValue = this.symptom;
      this.symptom = value;
      this.firePropertyChange(PROPERTY_SYMPTOM, oldValue, value);
      return this;
   }

   public String getConsultation()
   {
      return this.consultation;
   }

   public SymptomValidatedEvent setConsultation(String value)
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

   public String getConsultations()
   {
      return this.consultations;
   }

   public SymptomValidatedEvent setConsultations(String value)
   {
      if (Objects.equals(value, this.consultations))
      {
         return this;
      }

      final String oldValue = this.consultations;
      this.consultations = value;
      this.firePropertyChange(PROPERTY_CONSULTATIONS, oldValue, value);
      return this;
   }

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder(super.toString());
      result.append(' ').append(this.getSymptom());
      result.append(' ').append(this.getConsultation());
      result.append(' ').append(this.getConsultations());
      return result.toString();
   }
}
