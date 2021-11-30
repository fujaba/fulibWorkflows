package uks.debuggen.pm.clickCounter.events;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Collection;

public class DataGroup extends DataEvent
{
   public static final String PROPERTY_ELEMENTS = "elements";
   private List<DataEvent> elements;

   public List<DataEvent> getElements()
   {
      return this.elements != null ? Collections.unmodifiableList(this.elements) : Collections.emptyList();
   }

   public DataGroup withElements(DataEvent value)
   {
      if (this.elements == null)
      {
         this.elements = new ArrayList<>();
      }
      if (!this.elements.contains(value))
      {
         this.elements.add(value);
         value.withSagas(this);
         this.firePropertyChange(PROPERTY_ELEMENTS, null, value);
      }
      return this;
   }

   public DataGroup withElements(DataEvent... value)
   {
      for (final DataEvent item : value)
      {
         this.withElements(item);
      }
      return this;
   }

   public DataGroup withElements(Collection<? extends DataEvent> value)
   {
      for (final DataEvent item : value)
      {
         this.withElements(item);
      }
      return this;
   }

   public DataGroup withoutElements(DataEvent value)
   {
      if (this.elements != null && this.elements.remove(value))
      {
         value.withoutSagas(this);
         this.firePropertyChange(PROPERTY_ELEMENTS, value, null);
      }
      return this;
   }

   public DataGroup withoutElements(DataEvent... value)
   {
      for (final DataEvent item : value)
      {
         this.withoutElements(item);
      }
      return this;
   }

   public DataGroup withoutElements(Collection<? extends DataEvent> value)
   {
      for (final DataEvent item : value)
      {
         this.withoutElements(item);
      }
      return this;
   }

   @Override
   public void removeYou()
   {
      super.removeYou();
      this.withoutElements(new ArrayList<>(this.getElements()));
   }
}
