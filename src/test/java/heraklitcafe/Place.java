package heraklitcafe;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Collections;
import java.util.Collection;
import java.beans.PropertyChangeSupport;

public class Place
{
   public static final String PROPERTY_NAME = "name";
   public static final String PROPERTY_TABLES = "tables";
   private String name;
   private List<Table> tables;
   protected PropertyChangeSupport listeners;

   public String getName()
   {
      return this.name;
   }

   public Place setName(String value)
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

   public List<Table> getTables()
   {
      return this.tables != null ? Collections.unmodifiableList(this.tables) : Collections.emptyList();
   }

   public Place withTables(Table value)
   {
      if (this.tables == null)
      {
         this.tables = new ArrayList<>();
      }
      if (!this.tables.contains(value))
      {
         this.tables.add(value);
         value.setPlace(this);
         this.firePropertyChange(PROPERTY_TABLES, null, value);
      }
      return this;
   }

   public Place withTables(Table... value)
   {
      for (final Table item : value)
      {
         this.withTables(item);
      }
      return this;
   }

   public Place withTables(Collection<? extends Table> value)
   {
      for (final Table item : value)
      {
         this.withTables(item);
      }
      return this;
   }

   public Place withoutTables(Table value)
   {
      if (this.tables != null && this.tables.remove(value))
      {
         value.setPlace(null);
         this.firePropertyChange(PROPERTY_TABLES, value, null);
      }
      return this;
   }

   public Place withoutTables(Table... value)
   {
      for (final Table item : value)
      {
         this.withoutTables(item);
      }
      return this;
   }

   public Place withoutTables(Collection<? extends Table> value)
   {
      for (final Table item : value)
      {
         this.withoutTables(item);
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
      return result.substring(1);
   }

   public void removeYou()
   {
      this.withoutTables(new ArrayList<>(this.getTables()));
   }
}
