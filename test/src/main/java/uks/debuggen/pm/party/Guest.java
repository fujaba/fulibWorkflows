package uks.debuggen.pm.party;
import java.util.Objects;
import java.beans.PropertyChangeSupport;

public class Guest
{
   public static final String PROPERTY_ID = "id";
   public static final String PROPERTY_NAME = "name";
   public static final String PROPERTY_APP = "app";
   private String id;
   private String name;
   private PartyApp app;
   protected PropertyChangeSupport listeners;

   public String getId()
   {
      return this.id;
   }

   public Guest setId(String value)
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

   public Guest setName(String value)
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

   public PartyApp getApp()
   {
      return this.app;
   }

   public Guest setApp(PartyApp value)
   {
      if (this.app == value)
      {
         return this;
      }

      final PartyApp oldValue = this.app;
      if (this.app != null)
      {
         this.app = null;
         oldValue.withoutGuests(this);
      }
      this.app = value;
      if (value != null)
      {
         value.withGuests(this);
      }
      this.firePropertyChange(PROPERTY_APP, oldValue, value);
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
      this.setApp(null);
   }
}
