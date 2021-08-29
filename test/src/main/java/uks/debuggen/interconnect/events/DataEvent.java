package uks.debuggen.interconnect.events;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Collections;
import java.util.Collection;

public class DataEvent extends Event
{
   public static final String PROPERTY_BLOCK_ID = "blockId";
   public static final String PROPERTY_SAGAS = "sagas";
   public static final String PROPERTY_QUERY = "query";
   private String blockId;
   private List<DataGroup> sagas;
   private Query query;

   public String getBlockId()
   {
      return this.blockId;
   }

   public DataEvent setBlockId(String value)
   {
      if (Objects.equals(value, this.blockId))
      {
         return this;
      }

      final String oldValue = this.blockId;
      this.blockId = value;
      this.firePropertyChange(PROPERTY_BLOCK_ID, oldValue, value);
      return this;
   }

   public List<DataGroup> getSagas()
   {
      return this.sagas != null ? Collections.unmodifiableList(this.sagas) : Collections.emptyList();
   }

   public DataEvent withSagas(DataGroup value)
   {
      if (this.sagas == null)
      {
         this.sagas = new ArrayList<>();
      }
      if (!this.sagas.contains(value))
      {
         this.sagas.add(value);
         value.withElements(this);
         this.firePropertyChange(PROPERTY_SAGAS, null, value);
      }
      return this;
   }

   public DataEvent withSagas(DataGroup... value)
   {
      for (final DataGroup item : value)
      {
         this.withSagas(item);
      }
      return this;
   }

   public DataEvent withSagas(Collection<? extends DataGroup> value)
   {
      for (final DataGroup item : value)
      {
         this.withSagas(item);
      }
      return this;
   }

   public DataEvent withoutSagas(DataGroup value)
   {
      if (this.sagas != null && this.sagas.remove(value))
      {
         value.withoutElements(this);
         this.firePropertyChange(PROPERTY_SAGAS, value, null);
      }
      return this;
   }

   public DataEvent withoutSagas(DataGroup... value)
   {
      for (final DataGroup item : value)
      {
         this.withoutSagas(item);
      }
      return this;
   }

   public DataEvent withoutSagas(Collection<? extends DataGroup> value)
   {
      for (final DataGroup item : value)
      {
         this.withoutSagas(item);
      }
      return this;
   }

   public Query getQuery()
   {
      return this.query;
   }

   public DataEvent setQuery(Query value)
   {
      if (this.query == value)
      {
         return this;
      }

      final Query oldValue = this.query;
      if (this.query != null)
      {
         this.query = null;
         oldValue.withoutResults(this);
      }
      this.query = value;
      if (value != null)
      {
         value.withResults(this);
      }
      this.firePropertyChange(PROPERTY_QUERY, oldValue, value);
      return this;
   }

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder(super.toString());
      result.append(' ').append(this.getBlockId());
      return result.toString();
   }

   public void removeYou()
   {
      this.withoutSagas(new ArrayList<>(this.getSagas()));
      this.setQuery(null);
   }
}
