package uks.debuggen.medical.marburg.MarburgHealthSystem;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Collections;
import java.util.Collection;
import java.beans.PropertyChangeSupport;

public class Symptom
{
   public static final String PROPERTY_ID = "id";
   public static final String PROPERTY_NAME = "name";
   public static final String PROPERTY_INDICATES = "indicates";
   public static final String PROPERTY_EXCLUDES = "excludes";
   private String id;
   private String name;
   private List<Disease> indicates;
   private List<Disease> excludes;
   protected PropertyChangeSupport listeners;

   public String getId()
   {
      return this.id;
   }

   public Symptom setId(String value)
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

   public Symptom setName(String value)
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

   public List<Disease> getIndicates()
   {
      return this.indicates != null ? Collections.unmodifiableList(this.indicates) : Collections.emptyList();
   }

   public Symptom withIndicates(Disease value)
   {
      if (this.indicates == null)
      {
         this.indicates = new ArrayList<>();
      }
      if (!this.indicates.contains(value))
      {
         this.indicates.add(value);
         value.withSymptoms(this);
         this.firePropertyChange(PROPERTY_INDICATES, null, value);
      }
      return this;
   }

   public Symptom withIndicates(Disease... value)
   {
      for (final Disease item : value)
      {
         this.withIndicates(item);
      }
      return this;
   }

   public Symptom withIndicates(Collection<? extends Disease> value)
   {
      for (final Disease item : value)
      {
         this.withIndicates(item);
      }
      return this;
   }

   public Symptom withoutIndicates(Disease value)
   {
      if (this.indicates != null && this.indicates.remove(value))
      {
         value.withoutSymptoms(this);
         this.firePropertyChange(PROPERTY_INDICATES, value, null);
      }
      return this;
   }

   public Symptom withoutIndicates(Disease... value)
   {
      for (final Disease item : value)
      {
         this.withoutIndicates(item);
      }
      return this;
   }

   public Symptom withoutIndicates(Collection<? extends Disease> value)
   {
      for (final Disease item : value)
      {
         this.withoutIndicates(item);
      }
      return this;
   }

   public List<Disease> getExcludes()
   {
      return this.excludes != null ? Collections.unmodifiableList(this.excludes) : Collections.emptyList();
   }

   public Symptom withExcludes(Disease value)
   {
      if (this.excludes == null)
      {
         this.excludes = new ArrayList<>();
      }
      if (!this.excludes.contains(value))
      {
         this.excludes.add(value);
         value.withCounterSymptoms(this);
         this.firePropertyChange(PROPERTY_EXCLUDES, null, value);
      }
      return this;
   }

   public Symptom withExcludes(Disease... value)
   {
      for (final Disease item : value)
      {
         this.withExcludes(item);
      }
      return this;
   }

   public Symptom withExcludes(Collection<? extends Disease> value)
   {
      for (final Disease item : value)
      {
         this.withExcludes(item);
      }
      return this;
   }

   public Symptom withoutExcludes(Disease value)
   {
      if (this.excludes != null && this.excludes.remove(value))
      {
         value.withoutCounterSymptoms(this);
         this.firePropertyChange(PROPERTY_EXCLUDES, value, null);
      }
      return this;
   }

   public Symptom withoutExcludes(Disease... value)
   {
      for (final Disease item : value)
      {
         this.withoutExcludes(item);
      }
      return this;
   }

   public Symptom withoutExcludes(Collection<? extends Disease> value)
   {
      for (final Disease item : value)
      {
         this.withoutExcludes(item);
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
      this.withoutIndicates(new ArrayList<>(this.getIndicates()));
      this.withoutExcludes(new ArrayList<>(this.getExcludes()));
   }
}
