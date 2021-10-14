package uks.debuggen.pm.routing;
import java.util.Objects;
import java.beans.PropertyChangeSupport;

public class Route
{
   public static final String PROPERTY_ID = "id";
   public static final String PROPERTY_LENGTH = "length";
   public static final String PROPERTY_START = "start";
   public static final String PROPERTY_END = "end";
   private String id;
   private String length;
   private Stop start;
   private Stop end;
   protected PropertyChangeSupport listeners;

   public String getId()
   {
      return this.id;
   }

   public Route setId(String value)
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

   public String getLength()
   {
      return this.length;
   }

   public Route setLength(String value)
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

   public Stop getStart()
   {
      return this.start;
   }

   public Route setStart(Stop value)
   {
      if (this.start == value)
      {
         return this;
      }

      final Stop oldValue = this.start;
      if (this.start != null)
      {
         this.start = null;
         oldValue.withoutRouteStarts(this);
      }
      this.start = value;
      if (value != null)
      {
         value.withRouteStarts(this);
      }
      this.firePropertyChange(PROPERTY_START, oldValue, value);
      return this;
   }

   public Stop getEnd()
   {
      return this.end;
   }

   public Route setEnd(Stop value)
   {
      if (this.end == value)
      {
         return this;
      }

      final Stop oldValue = this.end;
      if (this.end != null)
      {
         this.end = null;
         oldValue.withoutRouteEnds(this);
      }
      this.end = value;
      if (value != null)
      {
         value.withRouteEnds(this);
      }
      this.firePropertyChange(PROPERTY_END, oldValue, value);
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
      result.append(' ').append(this.getLength());
      return result.substring(1);
   }

   public void removeYou()
   {
      this.setStart(null);
      this.setEnd(null);
   }
}
