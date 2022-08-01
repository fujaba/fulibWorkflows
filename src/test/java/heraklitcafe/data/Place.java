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
   public static final String PROPERTY_CLIENTS = "clients";
   public static final String PROPERTY_ORDERS = "orders";
   public static final String PROPERTY_SELECTIONS = "selections";
   public static final String PROPERTY_ITEM_REFS = "itemRefs";
   public static final String PROPERTY_MEAL_ITEMS = "mealItems";
   private String name;
   private List<Table> tables;
   protected PropertyChangeSupport listeners;
   private List<Client> clients;
   private List<Order> orders;
   private List<Selection> selections;
   private List<ItemRef> itemRefs;
   private List<MealItem> mealItems;

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

   public List<Order> getOrders()
   {
      return this.orders != null ? Collections.unmodifiableList(this.orders) : Collections.emptyList();
   }

   public Place withOrders(Order value)
   {
      if (this.orders == null)
      {
         this.orders = new ArrayList<>();
      }
      if (!this.orders.contains(value))
      {
         this.orders.add(value);
         value.withPlace(this);
         this.firePropertyChange(PROPERTY_ORDERS, null, value);
      }
      return this;
   }

   public Place withOrders(Order... value)
   {
      for (final Order item : value)
      {
         this.withOrders(item);
      }
      return this;
   }

   public Place withOrders(Collection<? extends Order> value)
   {
      for (final Order item : value)
      {
         this.withOrders(item);
      }
      return this;
   }

   public Place withoutOrders(Order value)
   {
      if (this.orders != null && this.orders.remove(value))
      {
         value.withoutPlace(this);
         this.firePropertyChange(PROPERTY_ORDERS, value, null);
      }
      return this;
   }

   public Place withoutOrders(Order... value)
   {
      for (final Order item : value)
      {
         this.withoutOrders(item);
      }
      return this;
   }

   public Place withoutOrders(Collection<? extends Order> value)
   {
      for (final Order item : value)
      {
         this.withoutOrders(item);
      }
      return this;
   }

   public List<Selection> getSelections()
   {
      return this.selections != null ? Collections.unmodifiableList(this.selections) : Collections.emptyList();
   }

   public Place withSelections(Selection value)
   {
      if (this.selections == null)
      {
         this.selections = new ArrayList<>();
      }
      if (!this.selections.contains(value))
      {
         this.selections.add(value);
         value.setPlace(this);
         this.firePropertyChange(PROPERTY_SELECTIONS, null, value);
      }
      return this;
   }

   public Place withSelections(Selection... value)
   {
      for (final Selection item : value)
      {
         this.withSelections(item);
      }
      return this;
   }

   public Place withSelections(Collection<? extends Selection> value)
   {
      for (final Selection item : value)
      {
         this.withSelections(item);
      }
      return this;
   }

   public Place withoutSelections(Selection value)
   {
      if (this.selections != null && this.selections.remove(value))
      {
         value.setPlace(null);
         this.firePropertyChange(PROPERTY_SELECTIONS, value, null);
      }
      return this;
   }

   public Place withoutSelections(Selection... value)
   {
      for (final Selection item : value)
      {
         this.withoutSelections(item);
      }
      return this;
   }

   public Place withoutSelections(Collection<? extends Selection> value)
   {
      for (final Selection item : value)
      {
         this.withoutSelections(item);
      }
      return this;
   }

   public List<ItemRef> getItemRefs()
   {
      return this.itemRefs != null ? Collections.unmodifiableList(this.itemRefs) : Collections.emptyList();
   }

   public Place withItemRefs(ItemRef value)
   {
      if (this.itemRefs == null)
      {
         this.itemRefs = new ArrayList<>();
      }
      if (!this.itemRefs.contains(value))
      {
         this.itemRefs.add(value);
         value.setPlace(this);
         this.firePropertyChange(PROPERTY_ITEM_REFS, null, value);
      }
      return this;
   }

   public Place withItemRefs(ItemRef... value)
   {
      for (final ItemRef item : value)
      {
         this.withItemRefs(item);
      }
      return this;
   }

   public Place withItemRefs(Collection<? extends ItemRef> value)
   {
      for (final ItemRef item : value)
      {
         this.withItemRefs(item);
      }
      return this;
   }

   public Place withoutItemRefs(ItemRef value)
   {
      if (this.itemRefs != null && this.itemRefs.remove(value))
      {
         value.setPlace(null);
         this.firePropertyChange(PROPERTY_ITEM_REFS, value, null);
      }
      return this;
   }

   public Place withoutItemRefs(ItemRef... value)
   {
      for (final ItemRef item : value)
      {
         this.withoutItemRefs(item);
      }
      return this;
   }

   public Place withoutItemRefs(Collection<? extends ItemRef> value)
   {
      for (final ItemRef item : value)
      {
         this.withoutItemRefs(item);
      }
      return this;
   }

   public List<MealItem> getMealItems()
   {
      return this.mealItems != null ? Collections.unmodifiableList(this.mealItems) : Collections.emptyList();
   }

   public Place withMealItems(MealItem value)
   {
      if (this.mealItems == null)
      {
         this.mealItems = new ArrayList<>();
      }
      if (!this.mealItems.contains(value))
      {
         this.mealItems.add(value);
         value.setPlace(this);
         this.firePropertyChange(PROPERTY_MEAL_ITEMS, null, value);
      }
      return this;
   }

   public Place withMealItems(MealItem... value)
   {
      for (final MealItem item : value)
      {
         this.withMealItems(item);
      }
      return this;
   }

   public Place withMealItems(Collection<? extends MealItem> value)
   {
      for (final MealItem item : value)
      {
         this.withMealItems(item);
      }
      return this;
   }

   public Place withoutMealItems(MealItem value)
   {
      if (this.mealItems != null && this.mealItems.remove(value))
      {
         value.setPlace(null);
         this.firePropertyChange(PROPERTY_MEAL_ITEMS, value, null);
      }
      return this;
   }

   public Place withoutMealItems(MealItem... value)
   {
      for (final MealItem item : value)
      {
         this.withoutMealItems(item);
      }
      return this;
   }

   public Place withoutMealItems(Collection<? extends MealItem> value)
   {
      for (final MealItem item : value)
      {
         this.withoutMealItems(item);
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
      this.withoutSelections(new ArrayList<>(this.getSelections()));
      this.withoutOrders(new ArrayList<>(this.getOrders()));
      this.withoutItemRefs(new ArrayList<>(this.getItemRefs()));
      this.withoutMealItems(new ArrayList<>(this.getMealItems()));
   }
}
