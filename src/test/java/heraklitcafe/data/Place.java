package heraklitcafe.data;

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
   public static final String PROPERTY_ITEMS = "items";
   public static final String PROPERTY_CLIENTS = "clients";
   private String name;
   private List<Table> tables;
   protected PropertyChangeSupport listeners;
   private List<OrderItem> items;
   private List<Client> clients;

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

   public List<OrderItem> getItems()
   {
      return this.items != null ? Collections.unmodifiableList(this.items) : Collections.emptyList();
   }

   public Place withItems(OrderItem value)
   {
      if (this.items == null)
      {
         this.items = new ArrayList<>();
      }
      if (!this.items.contains(value))
      {
         this.items.add(value);
         value.setPlace(this);
         this.firePropertyChange(PROPERTY_ITEMS, null, value);
      }
      return this;
   }

   public Place withItems(OrderItem... value)
   {
      for (final OrderItem item : value)
      {
         this.withItems(item);
      }
      return this;
   }

   public Place withItems(Collection<? extends OrderItem> value)
   {
      for (final OrderItem item : value)
      {
         this.withItems(item);
      }
      return this;
   }

   public Place withoutItems(OrderItem value)
   {
      if (this.items != null && this.items.remove(value))
      {
         value.setPlace(null);
         this.firePropertyChange(PROPERTY_ITEMS, value, null);
      }
      return this;
   }

   public Place withoutItems(OrderItem... value)
   {
      for (final OrderItem item : value)
      {
         this.withoutItems(item);
      }
      return this;
   }

   public Place withoutItems(Collection<? extends OrderItem> value)
   {
      for (final OrderItem item : value)
      {
         this.withoutItems(item);
      }
      return this;
   }

   public List<Client> getClients()
   {
      return this.clients != null ? Collections.unmodifiableList(this.clients) : Collections.emptyList();
   }

   public Place withClients(Client value)
   {
      if (this.clients == null)
      {
         this.clients = new ArrayList<>();
      }
      if (!this.clients.contains(value))
      {
         this.clients.add(value);
         value.setPlace(this);
         this.firePropertyChange(PROPERTY_CLIENTS, null, value);
      }
      return this;
   }

   public Place withClients(Client... value)
   {
      for (final Client item : value)
      {
         this.withClients(item);
      }
      return this;
   }

   public Place withClients(Collection<? extends Client> value)
   {
      for (final Client item : value)
      {
         this.withClients(item);
      }
      return this;
   }

   public Place withoutClients(Client value)
   {
      if (this.clients != null && this.clients.remove(value))
      {
         value.setPlace(null);
         this.firePropertyChange(PROPERTY_CLIENTS, value, null);
      }
      return this;
   }

   public Place withoutClients(Client... value)
   {
      for (final Client item : value)
      {
         this.withoutClients(item);
      }
      return this;
   }

   public Place withoutClients(Collection<? extends Client> value)
   {
      for (final Client item : value)
      {
         this.withoutClients(item);
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
      this.withoutClients(new ArrayList<>(this.getClients()));
      this.withoutItems(new ArrayList<>(this.getItems()));
   }
}
