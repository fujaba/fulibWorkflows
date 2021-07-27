package org.fulib.workflows;
import java.util.Objects;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Collection;

public class PageNote extends WorkflowNote
{
   public static final String PROPERTY_SERVICE = "service";
   public static final String PROPERTY_LINES = "lines";
   private ServiceNote service;
   private List<PageLine> lines;

   public ServiceNote getService()
   {
      return this.service;
   }

   public PageNote setService(ServiceNote value)
   {
      if (this.service == value)
      {
         return this;
      }

      final ServiceNote oldValue = this.service;
      if (this.service != null)
      {
         this.service = null;
         oldValue.withoutPages(this);
      }
      this.service = value;
      if (value != null)
      {
         value.withPages(this);
      }
      this.firePropertyChange(PROPERTY_SERVICE, oldValue, value);
      return this;
   }

   public List<PageLine> getLines()
   {
      return this.lines != null ? Collections.unmodifiableList(this.lines) : Collections.emptyList();
   }

   public PageNote withLines(PageLine value)
   {
      if (this.lines == null)
      {
         this.lines = new ArrayList<>();
      }
      if (!this.lines.contains(value))
      {
         this.lines.add(value);
         value.setPageNote(this);
         this.firePropertyChange(PROPERTY_LINES, null, value);
      }
      return this;
   }

   public PageNote withLines(PageLine... value)
   {
      for (final PageLine item : value)
      {
         this.withLines(item);
      }
      return this;
   }

   public PageNote withLines(Collection<? extends PageLine> value)
   {
      for (final PageLine item : value)
      {
         this.withLines(item);
      }
      return this;
   }

   public PageNote withoutLines(PageLine value)
   {
      if (this.lines != null && this.lines.remove(value))
      {
         value.setPageNote(null);
         this.firePropertyChange(PROPERTY_LINES, value, null);
      }
      return this;
   }

   public PageNote withoutLines(PageLine... value)
   {
      for (final PageLine item : value)
      {
         this.withoutLines(item);
      }
      return this;
   }

   public PageNote withoutLines(Collection<? extends PageLine> value)
   {
      for (final PageLine item : value)
      {
         this.withoutLines(item);
      }
      return this;
   }

   @Override
   public void removeYou()
   {
      super.removeYou();
      this.withoutLines(new ArrayList<>(this.getLines()));
      this.setService(null);
   }
}
