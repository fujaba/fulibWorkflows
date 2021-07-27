package org.fulib.workflows;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.beans.PropertyChangeSupport;

public class PageLine
{
   public static final String PROPERTY_MAP = "map";
   public static final String PROPERTY_PAGE_NOTE = "pageNote";
   private LinkedHashMap<String, String> map;
   private PageNote pageNote;
   protected PropertyChangeSupport listeners;

   public LinkedHashMap<String, String> getMap()
   {
      return this.map;
   }

   public PageLine setMap(LinkedHashMap<String, String> value)
   {
      if (Objects.equals(value, this.map))
      {
         return this;
      }

      final LinkedHashMap<String, String> oldValue = this.map;
      this.map = value;
      this.firePropertyChange(PROPERTY_MAP, oldValue, value);
      return this;
   }

   public PageNote getPageNote()
   {
      return this.pageNote;
   }

   public PageLine setPageNote(PageNote value)
   {
      if (this.pageNote == value)
      {
         return this;
      }

      final PageNote oldValue = this.pageNote;
      if (this.pageNote != null)
      {
         this.pageNote = null;
         oldValue.withoutLines(this);
      }
      this.pageNote = value;
      if (value != null)
      {
         value.withLines(this);
      }
      this.firePropertyChange(PROPERTY_PAGE_NOTE, oldValue, value);
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

   public void removeYou()
   {
      this.setPageNote(null);
   }
}
