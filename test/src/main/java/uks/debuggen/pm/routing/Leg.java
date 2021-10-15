package uks.debuggen.pm.routing;
import java.util.Objects;
import java.beans.PropertyChangeSupport;

public class Leg
{
   public static final String PROPERTY_ID = "id";
   public static final String PROPERTY_VIA = "via";
   public static final String PROPERTY_LENGTH = "length";
   public static final String PROPERTY_FROM = "from";
   public static final String PROPERTY_TO = "to";
   private String id;
   private String via;
   private String length;
   private Stop from;
   private Stop to;
   protected PropertyChangeSupport listeners;

   public String getId()
   {
      return this.id;
   }

   public Leg setId(String value)
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

   public String getVia()
   {
      return this.via;
   }

   public Leg setVia(String value)
   {
      if (Objects.equals(value, this.via))
      {
         return this;
      }

      final String oldValue = this.via;
      this.via = value;
      this.firePropertyChange(PROPERTY_VIA, oldValue, value);
      return this;
   }

   public String getLength()
   {
      return this.length;
   }

   public Leg setLength(String value)
   {
      if (Objects.equals(value, this.length))
      {
         return this;
      }

      final String oldValue = this.length;
      this.length = value;
      this.firePropertyChange(PROPERTY_LENGTH, oldValue, value);
      return this;
   }

   public Stop getFrom()
   {
      return this.from;
   }

   public Leg setFrom(Stop value)
   {
      if (this.from == value)
      {
         return this;
      }

      final Stop oldValue = this.from;
      if (this.from != null)
      {
         this.from = null;
         oldValue.setDeparture(null);
      }
      this.from = value;
      if (value != null)
      {
         value.setDeparture(this);
      }
      this.firePropertyChange(PROPERTY_FROM, oldValue, value);
      return this;
   }

   public Stop getTo()
   {
      return this.to;
   }

   public Leg setTo(Stop value)
   {
      if (this.to == value)
      {
         return this;
      }

      final Stop oldValue = this.to;
      if (this.to != null)
      {
         this.to = null;
         oldValue.setArrival(null);
      }
      this.to = value;
      if (value != null)
      {
         value.setArrival(this);
      }
      this.firePropertyChange(PROPERTY_TO, oldValue, value);
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
      result.append(' ').append(this.getVia());
      result.append(' ').append(this.getLength());
      return result.substring(1);
   }

   public void removeYou()
   {
      this.setFrom(null);
      this.setTo(null);
   }
}
