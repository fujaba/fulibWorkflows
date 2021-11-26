package uks.debuggen.microshop.Warehouse;
import java.util.Objects;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Collection;

public class Palette
{
   public static final String PROPERTY_ID = "id";
   public static final String PROPERTY_BARCODE = "barcode";
   public static final String PROPERTY_AMOUNT = "amount";
   public static final String PROPERTY_LOCATION = "location";
   public static final String PROPERTY_PRODUCT = "product";
   public static final String PROPERTY_PICK_TASKS = "pickTasks";
   private String id;
   private String barcode;
   private int amount;
   private String location;
   protected PropertyChangeSupport listeners;
   private WHProduct product;
   private List<PickTask> pickTasks;

   public String getId()
   {
      return this.id;
   }

   public Palette setId(String value)
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

   public String getBarcode()
   {
      return this.barcode;
   }

   public Palette setBarcode(String value)
   {
      if (Objects.equals(value, this.barcode))
      {
         return this;
      }

      final String oldValue = this.barcode;
      this.barcode = value;
      this.firePropertyChange(PROPERTY_BARCODE, oldValue, value);
      return this;
   }

   public int getAmount()
   {
      return this.amount;
   }

   public Palette setAmount(int value)
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

   public String getLocation()
   {
      return this.location;
   }

   public Palette setLocation(String value)
   {
      if (Objects.equals(value, this.location))
      {
         return this;
      }

      final String oldValue = this.location;
      this.location = value;
      this.firePropertyChange(PROPERTY_LOCATION, oldValue, value);
      return this;
   }

   public WHProduct getProduct()
   {
      return this.product;
   }

   public Palette setProduct(WHProduct value)
   {
      if (this.product == value)
      {
         return this;
      }

      final WHProduct oldValue = this.product;
      if (this.product != null)
      {
         this.product = null;
         oldValue.withoutPalettes(this);
      }
      this.product = value;
      if (value != null)
      {
         value.withPalettes(this);
      }
      this.firePropertyChange(PROPERTY_PRODUCT, oldValue, value);
      return this;
   }

   public List<PickTask> getPickTasks()
   {
      return this.pickTasks != null ? Collections.unmodifiableList(this.pickTasks) : Collections.emptyList();
   }

   public Palette withPickTasks(PickTask value)
   {
      if (this.pickTasks == null)
      {
         this.pickTasks = new ArrayList<>();
      }
      if (!this.pickTasks.contains(value))
      {
         this.pickTasks.add(value);
         value.setPalette(this);
         this.firePropertyChange(PROPERTY_PICK_TASKS, null, value);
      }
      return this;
   }

   public Palette withPickTasks(PickTask... value)
   {
      for (final PickTask item : value)
      {
         this.withPickTasks(item);
      }
      return this;
   }

   public Palette withPickTasks(Collection<? extends PickTask> value)
   {
      for (final PickTask item : value)
      {
         this.withPickTasks(item);
      }
      return this;
   }

   public Palette withoutPickTasks(PickTask value)
   {
      if (this.pickTasks != null && this.pickTasks.remove(value))
      {
         value.setPalette(null);
         this.firePropertyChange(PROPERTY_PICK_TASKS, value, null);
      }
      return this;
   }

   public Palette withoutPickTasks(PickTask... value)
   {
      for (final PickTask item : value)
      {
         this.withoutPickTasks(item);
      }
      return this;
   }

   public Palette withoutPickTasks(Collection<? extends PickTask> value)
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
      result.append(' ').append(this.getBarcode());
      result.append(' ').append(this.getLocation());
      return result.substring(1);
   }

   public void removeYou()
   {
      this.setProduct(null);
      this.withoutPickTasks(new ArrayList<>(this.getPickTasks()));
   }
}
