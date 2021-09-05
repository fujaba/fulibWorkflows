package uks.debuggen.medical.familydoctordegen.events;
import java.util.Objects;

public class ConsultationBuilt extends DataEvent
{
   public static final String PROPERTY_PATIENT = "patient";
   public static final String PROPERTY_DIAGNOSIS = "diagnosis";
   public static final String PROPERTY_TREATMENT = "treatment";
   public static final String PROPERTY_CID = "cid";
   private String patient;
   private String diagnosis;
   private String treatment;
   private String cid;

   public String getPatient()
   {
      return this.patient;
   }

   public ConsultationBuilt setPatient(String value)
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

   public String getDiagnosis()
   {
      return this.diagnosis;
   }

   public ConsultationBuilt setDiagnosis(String value)
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

   public ConsultationBuilt setTreatment(String value)
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

   public String getCid()
   {
      return this.cid;
   }

   public ConsultationBuilt setCid(String value)
   {
      if (Objects.equals(value, this.cid))
      {
         return this;
      }

      final String oldValue = this.cid;
      this.cid = value;
      this.firePropertyChange(PROPERTY_CID, oldValue, value);
      return this;
   }

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder(super.toString());
      result.append(' ').append(this.getCid());
      result.append(' ').append(this.getPatient());
      result.append(' ').append(this.getDiagnosis());
      result.append(' ').append(this.getTreatment());
      return result.toString();
   }
}
