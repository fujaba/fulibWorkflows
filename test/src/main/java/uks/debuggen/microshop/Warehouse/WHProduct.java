package uks.debuggen.microshop.Warehouse;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Collections;
import java.util.Collection;
import java.beans.PropertyChangeSupport;

public class WHProduct
{
   public static final String PROPERTY_ID = "id";
   public static final String PROPERTY_NAME = "name";
   public static final String PROPERTY_AMOUNT = "amount";
   public static final String PROPERTY_PALETTES = "palettes";
   public static final String PROPERTY_PICK_TASKS = "pickTasks";
   private String id;
   private String name;
   private int amount;
   private List<Palette> palettes;
   protected PropertyChangeSupport listeners;
   private List<PickTask> pickTasks;

   public String getId()
   {
      return this.id;
   }

   public WHProduct setId(String value)
   {
      if (Objects.equals(value, this.id))
      {
         return this;
      }

      final String oldValue = this.id;
      this.id = value;
      this.firePropertyChange(PROPERTY_ID, oldValue, value);
      return this;
   }

   public String getName()
   {
      return this.name;
   }

   public WHProduct setName(String value)
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

   public int getAmount()
   {
      return this.amount;
   }

   public WHProduct setAmount(int value)
   {
      if (value == this.amount)
      {
         return this;
      }

      final int oldValue = this.amount;
      this.amount = value;
      this.firePropertyChange(PROPERTY_AMOUNT, oldValue, value);
      return this;
   }

   public List<Palette> getPalettes()
   {
      return this.palettes != null ? Collections.unmodifiableList(this.palettes) : Collections.emptyList();
   }

   public WHProduct withPalettes(Palette value)
   {
      if (this.palettes == null)
      {
         this.palettes = new ArrayList<>();
      }
      if (!this.palettes.contains(value))
      {
         this.palettes.add(value);
         value.setProduct(this);
         this.firePropertyChange(PROPERTY_PALETTES, null, value);
      }
      return this;
   }

   public WHProduct withPalettes(Palette... value)
   {
      for (final Palette item : value)
      {
         this.withPalettes(item);
      }
      return this;
   }

   public WHProduct withPalettes(Collection<? extends Palette> value)
   {
      for (final Palette item : value)
      {
         this.withPalettes(item);
      }
      return this;
   }

   public WHProduct withoutPalettes(Palette value)
   {
      if (this.palettes != null && this.palettes.remove(value))
      {
         value.setProduct(null);
         this.firePropertyChange(PROPERTY_PALETTES, value, null);
      }
      return this;
   }

   public WHProduct withoutPalettes(Palette... value)
   {
      for (final Palette item : value)
      {
         this.withoutPalettes(item);
      }
      return this;
   }

   public WHProduct withoutPalettes(Collection<? extends Palette> value)
   {
      for (final Palette item : value)
      {
         this.withoutPalettes(item);
      }
      return this;
   }

   public List<PickTask> getPickTasks()
   {
      return this.pickTasks != null ? Collections.unmodifiableList(this.pickTasks) : Collections.emptyList();
   }

   public WHProduct withPickTasks(PickTask value)
   {
      if (this.pickTasks == null)
      {
         this.pickTasks = new ArrayList<>();
      }
      if (!this.pickTasks.contains(value))
      {
         this.pickTasks.add(value);
         value.setProduct(this);
         this.firePropertyChange(PROPERTY_PICK_TASKS, null, value);
      }
      return this;
   }

   public WHProduct withPickTasks(PickTask... value)
   {
      for (final PickTask item : value)
      {
         this.withPickTasks(item);
      }
      return this;
   }

   public WHProduct withPickTasks(Collection<? extends PickTask> value)
   {
      for (final PickTask item : value)
      {
         this.withPickTasks(item);
      }
      return this;
   }

   public WHProduct withoutPickTasks(PickTask value)
   {
      if (this.pickTasks != null && this.pickTasks.remove(value))
      {
         value.setProduct(null);
         this.firePropertyChange(PROPERTY_PICK_TASKS, value, null);
      }
      return this;
   }

   public WHProduct withoutPickTasks(PickTask... value)
   {
      for (final PickTask item : value)
      {
         this.withoutPickTasks(item);
      }
      return this;
   }

   public WHProduct withoutPickTasks(Collection<? extends PickTask> value)
   {
      for (final PickTask item : value)
      {
         this.withoutPickTasks(item);
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
      result.append(' ').append(this.getId());
      result.append(' ').append(this.getName());
      return result.substring(1);
   }

   public void removeYou()
   {
      this.withoutPalettes(new ArrayList<>(this.getPalettes()));
      this.withoutPickTasks(new ArrayList<>(this.getPickTasks()));
   }
}
