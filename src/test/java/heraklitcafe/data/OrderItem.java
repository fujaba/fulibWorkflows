package heraklitcafe.data;
import java.util.Objects;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Collection;

public class OrderItem
{
   public static final String PROPERTY_NAME = "name";
   public static final String PROPERTY_MEALS = "meals";
   private String name;
   protected PropertyChangeSupport listeners;
   private List<Meal> meals;

   public String getName()
   {
      return this.name;
   }

   public OrderItem setName(String value)
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

   public List<Meal> getMeals()
   {
      return this.meals != null ? Collections.unmodifiableList(this.meals) : Collections.emptyList();
   }

   public OrderItem withMeals(Meal value)
   {
      if (this.meals == null)
      {
         this.meals = new ArrayList<>();
      }
      if (!this.meals.contains(value))
      {
         this.meals.add(value);
         value.withItems(this);
         this.firePropertyChange(PROPERTY_MEALS, null, value);
      }
      return this;
   }

   public OrderItem withMeals(Meal... value)
   {
      for (final Meal item : value)
      {
         this.withMeals(item);
      }
      return this;
   }

   public OrderItem withMeals(Collection<? extends Meal> value)
   {
      for (final Meal item : value)
      {
         this.withMeals(item);
      }
      return this;
   }

   public OrderItem withoutMeals(Meal value)
   {
      if (this.meals != null && this.meals.remove(value))
      {
         value.withoutItems(this);
         this.firePropertyChange(PROPERTY_MEALS, value, null);
      }
      return this;
   }

   public OrderItem withoutMeals(Meal... value)
   {
      for (final Meal item : value)
      {
         this.withoutMeals(item);
      }
      return this;
   }

   public OrderItem withoutMeals(Collection<? extends Meal> value)
   {
      for (final Meal item : value)
      {
         this.withoutMeals(item);
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
      this.withoutMeals(new ArrayList<>(this.getMeals()));
   }
}
