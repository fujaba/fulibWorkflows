package org.fulib.reachable;

import java.beans.PropertyChangeSupport;
import java.util.*;

public class Graph
{
   public static final String PROPERTY_NAME = "name";
   public static final String PROPERTY_LABEL = "label";
   public static final String PROPERTY_CONS = "cons";
   public static final String PROPERTY_PRODS = "prods";
   private String name;
   private String label;
   private List<Op> cons;
   private List<Op> prods;
   private Map<String, Object> objMap;
   protected PropertyChangeSupport listeners;

   public Map<String, Object> theObjMap() {
       return objMap;
   }

   public Graph setObjMap(Map<String, Object> objMap) {
       this.objMap = objMap;
       return this;
   }

   public Graph withGraph(Graph graph) {
      if (objMap == null) {
         objMap = new LinkedHashMap<>();
      }
      objMap.put(graph.getName(), graph);
      return this;
   }

   public String getName()
   {
      return this.name;
   }

   public Graph setName(String value)
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

   public String getLabel()
   {
      return this.label;
   }

   public Graph setLabel(String value)
   {
      if (Objects.equals(value, this.label))
      {
         return this;
      }

      final String oldValue = this.label;
      this.label = value;
      this.firePropertyChange(PROPERTY_LABEL, oldValue, value);
      return this;
   }

   public List<Op> getCons()
   {
      return this.cons != null ? Collections.unmodifiableList(this.cons) : Collections.emptyList();
   }

   public Graph withCons(Op value)
   {
      if (this.cons == null)
      {
         this.cons = new ArrayList<>();
      }
      if (!this.cons.contains(value))
      {
         this.cons.add(value);
         value.setSrc(this);
         this.firePropertyChange(PROPERTY_CONS, null, value);
      }
      return this;
   }

   public Graph withCons(Op... value)
   {
      for (final Op item : value)
      {
         this.withCons(item);
      }
      return this;
   }

   public Graph withCons(Collection<? extends Op> value)
   {
      for (final Op item : value)
      {
         this.withCons(item);
      }
      return this;
   }

   public Graph withoutCons(Op value)
   {
      if (this.cons != null && this.cons.remove(value))
      {
         value.setSrc(null);
         this.firePropertyChange(PROPERTY_CONS, value, null);
      }
      return this;
   }

   public Graph withoutCons(Op... value)
   {
      for (final Op item : value)
      {
         this.withoutCons(item);
      }
      return this;
   }

   public Graph withoutCons(Collection<? extends Op> value)
   {
      for (final Op item : value)
      {
         this.withoutCons(item);
      }
      return this;
   }

   public List<Op> getProds()
   {
      return this.prods != null ? Collections.unmodifiableList(this.prods) : Collections.emptyList();
   }

   public Graph withProds(Op value)
   {
      if (this.prods == null)
      {
         this.prods = new ArrayList<>();
      }
      if (!this.prods.contains(value))
      {
         this.prods.add(value);
         value.setTgt(this);
         this.firePropertyChange(PROPERTY_PRODS, null, value);
      }
      return this;
   }

   public Graph withProds(Op... value)
   {
      for (final Op item : value)
      {
         this.withProds(item);
      }
      return this;
   }

   public Graph withProds(Collection<? extends Op> value)
   {
      for (final Op item : value)
      {
         this.withProds(item);
      }
      return this;
   }

   public Graph withoutProds(Op value)
   {
      if (this.prods != null && this.prods.remove(value))
      {
         value.setTgt(null);
         this.firePropertyChange(PROPERTY_PRODS, value, null);
      }
      return this;
   }

   public Graph withoutProds(Op... value)
   {
      for (final Op item : value)
      {
         this.withoutProds(item);
      }
      return this;
   }

   public Graph withoutProds(Collection<? extends Op> value)
   {
      for (final Op item : value)
      {
         this.withoutProds(item);
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
      result.append(' ').append(this.getLabel());
      return result.substring(1);
   }

   public void removeYou()
   {
      this.withoutCons(new ArrayList<>(this.getCons()));
      this.withoutProds(new ArrayList<>(this.getProds()));
   }


}
