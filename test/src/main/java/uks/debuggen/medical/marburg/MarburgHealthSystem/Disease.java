package uks.debuggen.medical.marburg.MarburgHealthSystem;
import java.util.Objects;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Collection;

public class Disease
{
   public static final String PROPERTY_ID = "id";
   public static final String PROPERTY_NAME = "name";
   public static final String PROPERTY_SYMPTOMS = "symptoms";
   public static final String PROPERTY_COUNTER_SYMPTOMS = "counterSymptoms";
   private String id;
   private String name;
   protected PropertyChangeSupport listeners;
   private List<Symptom> symptoms;
   private List<Symptom> counterSymptoms;

   public String getId()
   {
      return this.id;
   }

   public Disease setId(String value)
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

   public Disease setName(String value)
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

   public List<Symptom> getSymptoms()
   {
      return this.symptoms != null ? Collections.unmodifiableList(this.symptoms) : Collections.emptyList();
   }

   public Disease withSymptoms(Symptom value)
   {
      if (this.symptoms == null)
      {
         this.symptoms = new ArrayList<>();
      }
      if (!this.symptoms.contains(value))
      {
         this.symptoms.add(value);
         value.withIndicates(this);
         this.firePropertyChange(PROPERTY_SYMPTOMS, null, value);
      }
      return this;
   }

   public Disease withSymptoms(Symptom... value)
   {
      for (final Symptom item : value)
      {
         this.withSymptoms(item);
      }
      return this;
   }

   public Disease withSymptoms(Collection<? extends Symptom> value)
   {
      for (final Symptom item : value)
      {
         this.withSymptoms(item);
      }
      return this;
   }

   public Disease withoutSymptoms(Symptom value)
   {
      if (this.symptoms != null && this.symptoms.remove(value))
      {
         value.withoutIndicates(this);
         this.firePropertyChange(PROPERTY_SYMPTOMS, value, null);
      }
      return this;
   }

   public Disease withoutSymptoms(Symptom... value)
   {
      for (final Symptom item : value)
      {
         this.withoutSymptoms(item);
      }
      return this;
   }

   public Disease withoutSymptoms(Collection<? extends Symptom> value)
   {
      for (final Symptom item : value)
      {
         this.withoutSymptoms(item);
      }
      return this;
   }

   public List<Symptom> getCounterSymptoms()
   {
      return this.counterSymptoms != null ? Collections.unmodifiableList(this.counterSymptoms) : Collections.emptyList();
   }

   public Disease withCounterSymptoms(Symptom value)
   {
      if (this.counterSymptoms == null)
      {
         this.counterSymptoms = new ArrayList<>();
      }
      if (!this.counterSymptoms.contains(value))
      {
         this.counterSymptoms.add(value);
         value.withExcludes(this);
         this.firePropertyChange(PROPERTY_COUNTER_SYMPTOMS, null, value);
      }
      return this;
   }

   public Disease withCounterSymptoms(Symptom... value)
   {
      for (final Symptom item : value)
      {
         this.withCounterSymptoms(item);
      }
      return this;
   }

   public Disease withCounterSymptoms(Collection<? extends Symptom> value)
   {
      for (final Symptom item : value)
      {
         this.withCounterSymptoms(item);
      }
      return this;
   }

   public Disease withoutCounterSymptoms(Symptom value)
   {
      if (this.counterSymptoms != null && this.counterSymptoms.remove(value))
      {
         value.withoutExcludes(this);
         this.firePropertyChange(PROPERTY_COUNTER_SYMPTOMS, value, null);
      }
      return this;
   }

   public Disease withoutCounterSymptoms(Symptom... value)
   {
      for (final Symptom item : value)
      {
         this.withoutCounterSymptoms(item);
      }
      return this;
   }

   public Disease withoutCounterSymptoms(Collection<? extends Symptom> value)
   {
      for (final Symptom item : value)
      {
         this.withoutCounterSymptoms(item);
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
      return result.substring(1);
   }

   public void removeYou()
   {
      this.withoutSymptoms(new ArrayList<>(this.getSymptoms()));
      this.withoutCounterSymptoms(new ArrayList<>(this.getCounterSymptoms()));
   }
}
