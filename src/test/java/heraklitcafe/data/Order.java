package heraklitcafe.data;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Collections;
import java.util.Collection;
import java.beans.PropertyChangeSupport;

public class Order
{
   public static final String PROPERTY_NAME = "name";
   public static final String PROPERTY_PLACE = "place";
   public static final String PROPERTY_TABLE = "table";
   public static final String PROPERTY_SELECTION = "selection";
   public static final String PROPERTY_MEAL_ITEMS = "mealItems";
   private String name;
   private List<Place> place;
   protected PropertyChangeSupport listeners;
   private Table table;
   private Selection selection;
   private List<MealItem> mealItems;

   public String getName()
   {
      return this.name;
   }

   public Order setName(String value)
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

   public List<Place> getPlace()
   {
      return this.place != null ? Collections.unmodifiableList(this.place) : Collections.emptyList();
   }

   public Order withPlace(Place value)
   {
      if (this.place == null)
      {
         this.place = new ArrayList<>();
      }
      if (!this.place.contains(value))
      {
         this.place.add(value);
         value.withOrders(this);
         this.firePropertyChange(PROPERTY_PLACE, null, value);
      }
      return this;
   }

   public Order withPlace(Place... value)
   {
      for (final Place item : value)
      {
         this.withPlace(item);
      }
      return this;
   }

   public Order withPlace(Collection<? extends Place> value)
   {
      for (final Place item : value)
      {
         this.withPlace(item);
      }
      return this;
   }

   public Order withoutPlace(Place value)
   {
      if (this.place != null && this.place.remove(value))
      {
         value.withoutOrders(this);
         this.firePropertyChange(PROPERTY_PLACE, value, null);
      }
      return this;
   }

   public Order withoutPlace(Place... value)
   {
      for (final Place item : value)
      {
         this.withoutPlace(item);
      }
      return this;
   }

   public Order withoutPlace(Collection<? extends Place> value)
   {
      for (final Place item : value)
      {
         this.withoutPlace(item);
      }
      return this;
   }

   public Table getTable()
   {
      return this.table;
   }

   public Order setTable(Table value)
   {
      if (this.table == value)
      {
         return this;
      }

      final Table oldValue = this.table;
      if (this.table != null)
      {
         this.table = null;
         oldValue.setOrder(null);
      }
      this.table = value;
      if (value != null)
      {
         value.setOrder(this);
      }
      this.firePropertyChange(PROPERTY_TABLE, oldValue, value);
      return this;
   }

   public Selection getSelection()
   {
      return this.selection;
   }

   public Order setSelection(Selection value)
   {
      if (this.selection == value)
      {
         return this;
      }

      final Selection oldValue = this.selection;
      if (this.selection != null)
      {
         this.selection = null;
         oldValue.withoutOrders(this);
      }
      this.selection = value;
      if (value != null)
      {
         value.withOrders(this);
      }
      this.firePropertyChange(PROPERTY_SELECTION, oldValue, value);
      return this;
   }

   public List<MealItem> getMealItems()
   {
      return this.mealItems != null ? Collections.unmodifiableList(this.mealItems) : Collections.emptyList();
   }

   public Order withMealItems(MealItem value)
   {
      if (this.mealItems == null)
      {
         this.mealItems = new ArrayList<>();
      }
      if (!this.mealItems.contains(value))
      {
         this.mealItems.add(value);
         value.setOrder(this);
         this.firePropertyChange(PROPERTY_MEAL_ITEMS, null, value);
      }
      return this;
   }

   public Order withMealItems(MealItem... value)
   {
      for (final MealItem item : value)
      {
         this.withMealItems(item);
      }
      return this;
   }

   public Order withMealItems(Collection<? extends MealItem> value)
   {
      for (final MealItem item : value)
      {
         this.withMealItems(item);
      }
      return this;
   }

   public Order withoutMealItems(MealItem value)
   {
      if (this.mealItems != null && this.mealItems.remove(value))
      {
         value.setOrder(null);
         this.firePropertyChange(PROPERTY_MEAL_ITEMS, value, null);
      }
      return this;
   }

   public Order withoutMealItems(MealItem... value)
   {
      for (final MealItem item : value)
      {
         this.withoutMealItems(item);
      }
      return this;
   }

   public Order withoutMealItems(Collection<? extends MealItem> value)
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
      this.withoutPlace(new ArrayList<>(this.getPlace()));
      this.setTable(null);
      this.setSelection(null);
      this.withoutMealItems(new ArrayList<>(this.getMealItems()));
   }
}
