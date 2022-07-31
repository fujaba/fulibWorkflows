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
   public static final String PROPERTY_SELECTIONS = "selections";
   public static final String PROPERTY_ITEM_REFS = "itemRefs";
   private String name;
   protected PropertyChangeSupport listeners;
   private List<Selection> selections;
   private List<ItemRef> itemRefs;

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

   public List<Selection> getSelections()
   {
      return this.selections != null ? Collections.unmodifiableList(this.selections) : Collections.emptyList();
   }

   public OrderItem withSelections(Selection value)
   {
      if (this.selections == null)
      {
         this.selections = new ArrayList<>();
      }
      if (!this.selections.contains(value))
      {
         this.selections.add(value);
         value.withItems(this);
         this.firePropertyChange(PROPERTY_SELECTIONS, null, value);
      }
      return this;
   }

   public OrderItem withSelections(Selection... value)
   {
      for (final Selection item : value)
      {
         this.withSelections(item);
      }
      return this;
   }

   public OrderItem withSelections(Collection<? extends Selection> value)
   {
      for (final Selection item : value)
      {
         this.withSelections(item);
      }
      return this;
   }

   public OrderItem withoutSelections(Selection value)
   {
      if (this.selections != null && this.selections.remove(value))
      {
         value.withoutItems(this);
         this.firePropertyChange(PROPERTY_SELECTIONS, value, null);
      }
      return this;
   }

   public OrderItem withoutSelections(Selection... value)
   {
      for (final Selection item : value)
      {
         this.withoutSelections(item);
      }
      return this;
   }

   public OrderItem withoutSelections(Collection<? extends Selection> value)
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

   public OrderItem withItemRefs(ItemRef value)
   {
      if (this.itemRefs == null)
      {
         this.itemRefs = new ArrayList<>();
      }
      if (!this.itemRefs.contains(value))
      {
         this.itemRefs.add(value);
         value.setOrderItem(this);
         this.firePropertyChange(PROPERTY_ITEM_REFS, null, value);
      }
      return this;
   }

   public OrderItem withItemRefs(ItemRef... value)
   {
      for (final ItemRef item : value)
      {
         this.withItemRefs(item);
      }
      return this;
   }

   public OrderItem withItemRefs(Collection<? extends ItemRef> value)
   {
      for (final ItemRef item : value)
      {
         this.withItemRefs(item);
      }
      return this;
   }

   public OrderItem withoutItemRefs(ItemRef value)
   {
      if (this.itemRefs != null && this.itemRefs.remove(value))
      {
         value.setOrderItem(null);
         this.firePropertyChange(PROPERTY_ITEM_REFS, value, null);
      }
      return this;
   }

   public OrderItem withoutItemRefs(ItemRef... value)
   {
      for (final ItemRef item : value)
      {
         this.withoutItemRefs(item);
      }
      return this;
   }

   public OrderItem withoutItemRefs(Collection<? extends ItemRef> value)
   {
      for (final ItemRef item : value)
      {
         this.withoutItemRefs(item);
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
      this.withoutSelections(new ArrayList<>(this.getSelections()));
      this.withoutItemRefs(new ArrayList<>(this.getItemRefs()));
   }
}
