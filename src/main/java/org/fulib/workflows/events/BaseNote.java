package org.fulib.workflows.events;
import java.util.Objects;
import java.beans.PropertyChangeSupport;

public class BaseNote
{
   public static final String PROPERTY_NAME = "name";
   public static final String PROPERTY_INDEX = "index";
   private String name;
   private int index;
   protected PropertyChangeSupport listeners;

   public String getName()
   {
      return this.name;
   }

   public BaseNote setName(String value)
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

   public int getIndex()
   {
      return this.index;
   }

   public BaseNote setIndex(int value)
   {
      if (value == this.index)
      {
         return this;
      }

      final int oldValue = this.index;
      this.index = value;
      this.firePropertyChange(PROPERTY_INDEX, oldValue, value);
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
}
