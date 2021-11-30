package uks.debuggen.pm.clickCounter.ClickCounter;
import java.util.Objects;
import java.beans.PropertyChangeSupport;

public class Counter
{
   public static final String PROPERTY_ID = "id";
   public static final String PROPERTY_COUNT = "count";
   private String id;
   private int count;
   protected PropertyChangeSupport listeners;

   public String getId()
   {
      return this.id;
   }

   public Counter setId(String value)
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

   public int getCount()
   {
      return this.count;
   }

   public Counter setCount(int value)
   {
      if (value == this.count)
      {
         return this;
      }

      final int oldValue = this.count;
      this.count = value;
      this.firePropertyChange(PROPERTY_COUNT, oldValue, value);
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
}
