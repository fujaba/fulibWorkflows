package heraklitcafe.data;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Collections;
import java.util.Collection;
import java.beans.PropertyChangeSupport;

public class Transition
{
   public static final String PROPERTY_NAME = "name";
   public static final String PROPERTY_SRC = "src";
   public static final String PROPERTY_TGT = "tgt";
   private String name;
   private List<Place> src;
   private List<Place> tgt;
   protected PropertyChangeSupport listeners;

   public String getName()
   {
      return this.name;
   }

   public Transition setName(String value)
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

   public List<Place> getSrc()
   {
      return this.src != null ? Collections.unmodifiableList(this.src) : Collections.emptyList();
   }

   public Transition withSrc(Place value)
   {
      if (this.src == null)
      {
         this.src = new ArrayList<>();
      }
      if (!this.src.contains(value))
      {
         this.src.add(value);
         value.withOut(this);
         this.firePropertyChange(PROPERTY_SRC, null, value);
      }
      return this;
   }

   public Transition withSrc(Place... value)
   {
      for (final Place item : value)
      {
         this.withSrc(item);
      }
      return this;
   }

   public Transition withSrc(Collection<? extends Place> value)
   {
      for (final Place item : value)
      {
         this.withSrc(item);
      }
      return this;
   }

   public Transition withoutSrc(Place value)
   {
      if (this.src != null && this.src.remove(value))
      {
         value.withoutOut(this);
         this.firePropertyChange(PROPERTY_SRC, value, null);
      }
      return this;
   }

   public Transition withoutSrc(Place... value)
   {
      for (final Place item : value)
      {
         this.withoutSrc(item);
      }
      return this;
   }

   public Transition withoutSrc(Collection<? extends Place> value)
   {
      for (final Place item : value)
      {
         this.withoutSrc(item);
      }
      return this;
   }

   public List<Place> getTgt()
   {
      return this.tgt != null ? Collections.unmodifiableList(this.tgt) : Collections.emptyList();
   }

   public Transition withTgt(Place value)
   {
      if (this.tgt == null)
      {
         this.tgt = new ArrayList<>();
      }
      if (!this.tgt.contains(value))
      {
         this.tgt.add(value);
         value.withIn(this);
         this.firePropertyChange(PROPERTY_TGT, null, value);
      }
      return this;
   }

   public Transition withTgt(Place... value)
   {
      for (final Place item : value)
      {
         this.withTgt(item);
      }
      return this;
   }

   public Transition withTgt(Collection<? extends Place> value)
   {
      for (final Place item : value)
      {
         this.withTgt(item);
      }
      return this;
   }

   public Transition withoutTgt(Place value)
   {
      if (this.tgt != null && this.tgt.remove(value))
      {
         value.withoutIn(this);
         this.firePropertyChange(PROPERTY_TGT, value, null);
      }
      return this;
   }

   public Transition withoutTgt(Place... value)
   {
      for (final Place item : value)
      {
         this.withoutTgt(item);
      }
      return this;
   }

   public Transition withoutTgt(Collection<? extends Place> value)
   {
      for (final Place item : value)
      {
         this.withoutTgt(item);
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
      result.append(' ').append(this.getName());
      return result.substring(1);
   }

   public void removeYou()
   {
      this.withoutSrc(new ArrayList<>(this.getSrc()));
      this.withoutTgt(new ArrayList<>(this.getTgt()));
   }
}
