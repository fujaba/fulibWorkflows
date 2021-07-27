package org.fulib.workflows;
import java.util.Objects;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Collection;

public class PageNote extends WorkflowNote
{
   public static final String PROPERTY_SERVICE = "service";
   public static final String PROPERTY_BUTTON_ID = "buttonId";
   public static final String PROPERTY_LINES = "lines";
   public static final String PROPERTY_PREVIOUS_PAGE = "previousPage";
   public static final String PROPERTY_NEXT_PAGE = "nextPage";
   public static final String PROPERTY_RAISED_EVENT = "raisedEvent";
   private ServiceNote service;
   private String buttonId;
   private List<PageLine> lines;
   private PageNote previousPage;
   private PageNote nextPage;
   private EventNote raisedEvent;

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

   public String getButtonId()
   {
      return this.buttonId;
   }

   public PageNote setButtonId(String value)
   {
      if (Objects.equals(value, this.buttonId))
      {
         return this;
      }

      final String oldValue = this.buttonId;
      this.buttonId = value;
      this.firePropertyChange(PROPERTY_BUTTON_ID, oldValue, value);
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

   public PageNote getPreviousPage()
   {
      return this.previousPage;
   }

   public PageNote setPreviousPage(PageNote value)
   {
      if (this.previousPage == value)
      {
         return this;
      }

      final PageNote oldValue = this.previousPage;
      if (this.previousPage != null)
      {
         this.previousPage = null;
         oldValue.setNextPage(null);
      }
      this.previousPage = value;
      if (value != null)
      {
         value.setNextPage(this);
      }
      this.firePropertyChange(PROPERTY_PREVIOUS_PAGE, oldValue, value);
      return this;
   }

   public PageNote getNextPage()
   {
      return this.nextPage;
   }

   public PageNote setNextPage(PageNote value)
   {
      if (this.nextPage == value)
      {
         return this;
      }

      final PageNote oldValue = this.nextPage;
      if (this.nextPage != null)
      {
         this.nextPage = null;
         oldValue.setPreviousPage(null);
      }
      this.nextPage = value;
      if (value != null)
      {
         value.setPreviousPage(this);
      }
      this.firePropertyChange(PROPERTY_NEXT_PAGE, oldValue, value);
      return this;
   }

   public EventNote getRaisedEvent()
   {
      return this.raisedEvent;
   }

   public PageNote setRaisedEvent(EventNote value)
   {
      if (this.raisedEvent == value)
      {
         return this;
      }

      final EventNote oldValue = this.raisedEvent;
      if (this.raisedEvent != null)
      {
         this.raisedEvent = null;
         oldValue.setRaisingPage(null);
      }
      this.raisedEvent = value;
      if (value != null)
      {
         value.setRaisingPage(this);
      }
      this.firePropertyChange(PROPERTY_RAISED_EVENT, oldValue, value);
      return this;
   }

   @Override
   public void removeYou()
   {
      super.removeYou();
      this.withoutLines(new ArrayList<>(this.getLines()));
      this.setService(null);
      this.setPreviousPage(null);
      this.setNextPage(null);
      this.setRaisedEvent(null);
   }

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder(super.toString());
      result.append(' ').append(this.getButtonId());
      return result.toString();
   }
}
