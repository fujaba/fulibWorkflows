package uks.debuggen.party2.PartyApp;
import java.util.Objects;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Collection;

public class Region
{
   public static final String PROPERTY_ID = "id";
   public static final String PROPERTY_PARTIES = "parties";
   private String id;
   protected PropertyChangeSupport listeners;
   private List<Party2> parties;

   public String getId()
   {
      return this.id;
   }

   public Region setId(String value)
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

   public List<Party2> getParties()
   {
      return this.parties != null ? Collections.unmodifiableList(this.parties) : Collections.emptyList();
   }

   public Region withParties(Party2 value)
   {
      if (this.parties == null)
      {
         this.parties = new ArrayList<>();
      }
      if (!this.parties.contains(value))
      {
         this.parties.add(value);
         value.setRegion(this);
         this.firePropertyChange(PROPERTY_PARTIES, null, value);
      }
      return this;
   }

   public Region withParties(Party2... value)
   {
      for (final Party2 item : value)
      {
         this.withParties(item);
      }
      return this;
   }

   public Region withParties(Collection<? extends Party2> value)
   {
      for (final Party2 item : value)
      {
         this.withParties(item);
      }
      return this;
   }

   public Region withoutParties(Party2 value)
   {
      if (this.parties != null && this.parties.remove(value))
      {
         value.setRegion(null);
         this.firePropertyChange(PROPERTY_PARTIES, value, null);
      }
      return this;
   }

   public Region withoutParties(Party2... value)
   {
      for (final Party2 item : value)
      {
         this.withoutParties(item);
      }
      return this;
   }

   public Region withoutParties(Collection<? extends Party2> value)
   {
      for (final Party2 item : value)
      {
         this.withoutParties(item);
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
      this.withoutParties(new ArrayList<>(this.getParties()));
   }
}
