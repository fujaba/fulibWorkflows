package org.fulib.reachable;
import java.util.Objects;
import java.beans.PropertyChangeSupport;

public class Op
{
   public static final String PROPERTY_NAME = "name";
   public static final String PROPERTY_SRC = "src";
   public static final String PROPERTY_TGT = "tgt";
   private String name;
   private Graph src;
   private Graph tgt;
   protected PropertyChangeSupport listeners;

   public String getName()
   {
      return this.name;
   }

   public Op setName(String value)
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

   public Graph getSrc()
   {
      return this.src;
   }

   public Op setSrc(Graph value)
   {
      if (this.src == value)
      {
         return this;
      }

      final Graph oldValue = this.src;
      if (this.src != null)
      {
         this.src = null;
         oldValue.withoutCons(this);
      }
      this.src = value;
      if (value != null)
      {
         value.withCons(this);
      }
      this.firePropertyChange(PROPERTY_SRC, oldValue, value);
      return this;
   }

   public Graph getTgt()
   {
      return this.tgt;
   }

   public Op setTgt(Graph value)
   {
      if (this.tgt == value)
      {
         return this;
      }

      final Graph oldValue = this.tgt;
      if (this.tgt != null)
      {
         this.tgt = null;
         oldValue.withoutProds(this);
      }
      this.tgt = value;
      if (value != null)
      {
         value.withProds(this);
      }
      this.firePropertyChange(PROPERTY_TGT, oldValue, value);
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
      this.setSrc(null);
      this.setTgt(null);
   }
}
