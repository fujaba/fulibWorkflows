package uks.debuggen.pm.party;
import java.util.Objects;
import java.beans.PropertyChangeSupport;

public class Party
{
   public static final String PROPERTY_ID = "id";
   public static final String PROPERTY_NAME = "name";
   public static final String PROPERTY_LOCATION = "location";
   public static final String PROPERTY_DATE = "date";
   public static final String PROPERTY_ROOT = "root";
   private String id;
   private String name;
   private String location;
   private String date;
   private PartyApp root;
   protected PropertyChangeSupport listeners;

   public String getId()
   {
      return this.id;
   }

   public Party setId(String value)
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

   public Party setName(String value)
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

   public String getLocation()
   {
      return this.location;
   }

   public Party setLocation(String value)
   {
      if (Objects.equals(value, this.location))
      {
         return this;
      }

      final String oldValue = this.location;
      this.location = value;
      this.firePropertyChange(PROPERTY_LOCATION, oldValue, value);
      return this;
   }

   public String getDate()
   {
      return this.date;
   }

   public Party setDate(String value)
   {
      if (Objects.equals(value, this.date))
      {
         return this;
      }

      final String oldValue = this.date;
      this.date = value;
      this.firePropertyChange(PROPERTY_DATE, oldValue, value);
      return this;
   }

   public PartyApp getRoot()
   {
      return this.root;
   }

   public Party setRoot(PartyApp value)
   {
      if (this.root == value)
      {
         return this;
      }

      final PartyApp oldValue = this.root;
      if (this.root != null)
      {
         this.root = null;
         oldValue.withoutParties(this);
      }
      this.root = value;
      if (value != null)
      {
         value.withParties(this);
      }
      this.firePropertyChange(PROPERTY_ROOT, oldValue, value);
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
      result.append(' ').append(this.getLocation());
      result.append(' ').append(this.getDate());
      return result.substring(1);
   }

   public void removeYou()
   {
      this.setRoot(null);
   }
}
