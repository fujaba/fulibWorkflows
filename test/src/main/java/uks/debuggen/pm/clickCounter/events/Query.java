package uks.debuggen.pm.clickCounter.events;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Collections;
import java.util.Collection;

public class Query extends Event
{
   public static final String PROPERTY_KEY = "key";
   public static final String PROPERTY_RESULTS = "results";
   private String key;
   private List<DataEvent> results;

   public String getKey()
   {
      return this.key;
   }

   public Query setKey(String value)
   {
      if (Objects.equals(value, this.key))
      {
         return this;
      }

      final String oldValue = this.key;
      this.key = value;
      this.firePropertyChange(PROPERTY_KEY, oldValue, value);
      return this;
   }

   public List<DataEvent> getResults()
   {
      return this.results != null ? Collections.unmodifiableList(this.results) : Collections.emptyList();
   }

   public Query withResults(DataEvent value)
   {
      if (this.results == null)
      {
         this.results = new ArrayList<>();
      }
      if (!this.results.contains(value))
      {
         this.results.add(value);
         value.setQuery(this);
         this.firePropertyChange(PROPERTY_RESULTS, null, value);
      }
      return this;
   }

   public Query withResults(DataEvent... value)
   {
      for (final DataEvent item : value)
      {
         this.withResults(item);
      }
      return this;
   }

   public Query withResults(Collection<? extends DataEvent> value)
   {
      for (final DataEvent item : value)
      {
         this.withResults(item);
      }
      return this;
   }

   public Query withoutResults(DataEvent value)
   {
      if (this.results != null && this.results.remove(value))
      {
         value.setQuery(null);
         this.firePropertyChange(PROPERTY_RESULTS, value, null);
      }
      return this;
   }

   public Query withoutResults(DataEvent... value)
   {
      for (final DataEvent item : value)
      {
         this.withoutResults(item);
      }
      return this;
   }

   public Query withoutResults(Collection<? extends DataEvent> value)
   {
      for (final DataEvent item : value)
      {
         this.withoutResults(item);
      }
      return this;
   }

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder(super.toString());
      result.append(' ').append(this.getKey());
      return result.toString();
   }

   public void removeYou()
   {
      this.withoutResults(new ArrayList<>(this.getResults()));
   }
}
