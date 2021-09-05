package uks.debuggen.medical.familydoctordegen.DocMedical;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Collections;
import java.util.Collection;
import java.beans.PropertyChangeSupport;

public class Patient
{
   public static final String PROPERTY_ID = "id";
   public static final String PROPERTY_NAME = "name";
   public static final String PROPERTY_ADDRESS = "address";
   public static final String PROPERTY_BIRTH_DATE = "birthDate";
   public static final String PROPERTY_CONSULTATIONS = "consultations";
   private String id;
   private String name;
   private String address;
   private String birthDate;
   private List<Consultation> consultations;
   protected PropertyChangeSupport listeners;

   public String getId()
   {
      return this.id;
   }

   public Patient setId(String value)
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

   public String getName()
   {
      return this.name;
   }

   public Patient setName(String value)
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

   public Patient setAddress(String value)
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

   public Patient setBirthDate(String value)
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

   public List<Consultation> getConsultations()
   {
      return this.consultations != null ? Collections.unmodifiableList(this.consultations) : Collections.emptyList();
   }

   public Patient withConsultations(Consultation value)
   {
      if (this.consultations == null)
      {
         this.consultations = new ArrayList<>();
      }
      if (!this.consultations.contains(value))
      {
         this.consultations.add(value);
         value.setPatient(this);
         this.firePropertyChange(PROPERTY_CONSULTATIONS, null, value);
      }
      return this;
   }

   public Patient withConsultations(Consultation... value)
   {
      for (final Consultation item : value)
      {
         this.withConsultations(item);
      }
      return this;
   }

   public Patient withConsultations(Collection<? extends Consultation> value)
   {
      for (final Consultation item : value)
      {
         this.withConsultations(item);
      }
      return this;
   }

   public Patient withoutConsultations(Consultation value)
   {
      if (this.consultations != null && this.consultations.remove(value))
      {
         value.setPatient(null);
         this.firePropertyChange(PROPERTY_CONSULTATIONS, value, null);
      }
      return this;
   }

   public Patient withoutConsultations(Consultation... value)
   {
      for (final Consultation item : value)
      {
         this.withoutConsultations(item);
      }
      return this;
   }

   public Patient withoutConsultations(Collection<? extends Consultation> value)
   {
      for (final Consultation item : value)
      {
         this.withoutConsultations(item);
      }
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
      result.append(' ').append(this.getName());
      result.append(' ').append(this.getAddress());
      result.append(' ').append(this.getBirthDate());
      return result.substring(1);
   }

   public void removeYou()
   {
      this.withoutConsultations(new ArrayList<>(this.getConsultations()));
   }
}
