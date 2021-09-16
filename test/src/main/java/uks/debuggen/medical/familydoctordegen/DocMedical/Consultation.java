package uks.debuggen.medical.familydoctordegen.DocMedical;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Collections;
import java.util.Collection;
import java.beans.PropertyChangeSupport;

public class Consultation
{
   public static final String PROPERTY_ID = "id";
   public static final String PROPERTY_TREATMENT = "treatment";
   public static final String PROPERTY_SYMPTOMS = "symptoms";
   public static final String PROPERTY_PATIENT = "patient";
   public static final String PROPERTY_DIAGNOSIS = "diagnosis";
   public static final String PROPERTY_TESTS = "tests";
   public static final String PROPERTY_CID = "cid";
   private String id;
   private String treatment;
   private List<Symptom> symptoms;
   private Patient patient;
   private Disease diagnosis;
   private List<Test> tests;
   protected PropertyChangeSupport listeners;
   private String cid;

   public String getId()
   {
      return this.id;
   }

   public Consultation setId(String value)
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

   public String getTreatment()
   {
      return this.treatment;
   }

   public Consultation setTreatment(String value)
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

   public List<Symptom> getSymptoms()
   {
      return this.symptoms != null ? Collections.unmodifiableList(this.symptoms) : Collections.emptyList();
   }

   public Consultation withSymptoms(Symptom value)
   {
      if (this.symptoms == null)
      {
         this.symptoms = new ArrayList<>();
      }
      if (!this.symptoms.contains(value))
      {
         this.symptoms.add(value);
         value.withConsultations(this);
         this.firePropertyChange(PROPERTY_SYMPTOMS, null, value);
      }
      return this;
   }

   public Consultation withSymptoms(Symptom... value)
   {
      for (final Symptom item : value)
      {
         this.withSymptoms(item);
      }
      return this;
   }

   public Consultation withSymptoms(Collection<? extends Symptom> value)
   {
      for (final Symptom item : value)
      {
         this.withSymptoms(item);
      }
      return this;
   }

   public Consultation withoutSymptoms(Symptom value)
   {
      if (this.symptoms != null && this.symptoms.remove(value))
      {
         value.withoutConsultations(this);
         this.firePropertyChange(PROPERTY_SYMPTOMS, value, null);
      }
      return this;
   }

   public Consultation withoutSymptoms(Symptom... value)
   {
      for (final Symptom item : value)
      {
         this.withoutSymptoms(item);
      }
      return this;
   }

   public Consultation withoutSymptoms(Collection<? extends Symptom> value)
   {
      for (final Symptom item : value)
      {
         this.withoutSymptoms(item);
      }
      return this;
   }

   public Patient getPatient()
   {
      return this.patient;
   }

   public Consultation setPatient(Patient value)
   {
      if (this.patient == value)
      {
         return this;
      }

      final Patient oldValue = this.patient;
      if (this.patient != null)
      {
         this.patient = null;
         oldValue.withoutConsultations(this);
      }
      this.patient = value;
      if (value != null)
      {
         value.withConsultations(this);
      }
      this.firePropertyChange(PROPERTY_PATIENT, oldValue, value);
      return this;
   }

   public Disease getDiagnosis()
   {
      return this.diagnosis;
   }

   public Consultation setDiagnosis(Disease value)
   {
      if (this.diagnosis == value)
      {
         return this;
      }

      final Disease oldValue = this.diagnosis;
      if (this.diagnosis != null)
      {
         this.diagnosis = null;
         oldValue.withoutConsultations(this);
      }
      this.diagnosis = value;
      if (value != null)
      {
         value.withConsultations(this);
      }
      this.firePropertyChange(PROPERTY_DIAGNOSIS, oldValue, value);
      return this;
   }

   public List<Test> getTests()
   {
      return this.tests != null ? Collections.unmodifiableList(this.tests) : Collections.emptyList();
   }

   public Consultation withTests(Test value)
   {
      if (this.tests == null)
      {
         this.tests = new ArrayList<>();
      }
      if (!this.tests.contains(value))
      {
         this.tests.add(value);
         value.setConsultation(this);
         this.firePropertyChange(PROPERTY_TESTS, null, value);
      }
      return this;
   }

   public Consultation withTests(Test... value)
   {
      for (final Test item : value)
      {
         this.withTests(item);
      }
      return this;
   }

   public Consultation withTests(Collection<? extends Test> value)
   {
      for (final Test item : value)
      {
         this.withTests(item);
      }
      return this;
   }

   public Consultation withoutTests(Test value)
   {
      if (this.tests != null && this.tests.remove(value))
      {
         value.setConsultation(null);
         this.firePropertyChange(PROPERTY_TESTS, value, null);
      }
      return this;
   }

   public Consultation withoutTests(Test... value)
   {
      for (final Test item : value)
      {
         this.withoutTests(item);
      }
      return this;
   }

   public Consultation withoutTests(Collection<? extends Test> value)
   {
      for (final Test item : value)
      {
         this.withoutTests(item);
      }
      return this;
   }

   public String getCid()
   {
      return this.cid;
   }

   public Consultation setCid(String value)
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
      result.append(' ').append(this.getCid());
      result.append(' ').append(this.getTreatment());
      return result.substring(1);
   }

   public void removeYou()
   {
      this.setPatient(null);
      this.withoutSymptoms(new ArrayList<>(this.getSymptoms()));
      this.withoutTests(new ArrayList<>(this.getTests()));
      this.setDiagnosis(null);
   }
}
