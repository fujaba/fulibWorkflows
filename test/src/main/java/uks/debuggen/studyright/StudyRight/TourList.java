package uks.debuggen.studyright.StudyRight;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Collections;
import java.util.Collection;
import java.beans.PropertyChangeSupport;

public class TourList
{
   public static final String PROPERTY_ID = "id";
   public static final String PROPERTY_ALTERNATIVES = "alternatives";
   private String id;
   protected PropertyChangeSupport listeners;
   private List<Tour> alternatives;

   public String getId()
   {
      return this.id;
   }

   public TourList setId(String value)
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

   public List<Tour> getAlternatives()
   {
      return this.alternatives != null ? Collections.unmodifiableList(this.alternatives) : Collections.emptyList();
   }

   public TourList withAlternatives(Tour value)
   {
      if (this.alternatives == null)
      {
         this.alternatives = new ArrayList<>();
      }
      if (!this.alternatives.contains(value))
      {
         this.alternatives.add(value);
         value.setTourList(this);
         this.firePropertyChange(PROPERTY_ALTERNATIVES, null, value);
      }
      return this;
   }

   public TourList withAlternatives(Tour... value)
   {
      for (final Tour item : value)
      {
         this.withAlternatives(item);
      }
      return this;
   }

   public TourList withAlternatives(Collection<? extends Tour> value)
   {
      for (final Tour item : value)
      {
         this.withAlternatives(item);
      }
      return this;
   }

   public TourList withoutAlternatives(Tour value)
   {
      if (this.alternatives != null && this.alternatives.remove(value))
      {
         value.setTourList(null);
         this.firePropertyChange(PROPERTY_ALTERNATIVES, value, null);
      }
      return this;
   }

   public TourList withoutAlternatives(Tour... value)
   {
      for (final Tour item : value)
      {
         this.withoutAlternatives(item);
      }
      return this;
   }

   public TourList withoutAlternatives(Collection<? extends Tour> value)
   {
      for (final Tour item : value)
      {
         this.withoutAlternatives(item);
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
      return result.substring(1);
   }

   public void removeYou()
   {
      this.withoutAlternatives(new ArrayList<>(this.getAlternatives()));
   }
}
