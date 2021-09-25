package uks.debuggen.microshop.Warehouse;
import java.util.Objects;
import java.beans.PropertyChangeSupport;

public class PickTask
{
   public static final String PROPERTY_ID = "id";
   public static final String PROPERTY_CODE = "code";
   public static final String PROPERTY_PRODUCT = "product";
   public static final String PROPERTY_SHELF = "shelf";
   public static final String PROPERTY_CUSTOMER = "customer";
   public static final String PROPERTY_ADDRESS = "address";
   public static final String PROPERTY_STATE = "state";
   public static final String PROPERTY_FROM = "from";
   private String id;
   private String code;
   private String product;
   private String shelf;
   private String customer;
   private String address;
   private String state;
   protected PropertyChangeSupport listeners;
   private String from;

   public String getId()
   {
      return this.id;
   }

   public PickTask setId(String value)
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

   public String getCode()
   {
      return this.code;
   }

   public PickTask setCode(String value)
   {
      if (Objects.equals(value, this.code))
      {
         return this;
      }

      final String oldValue = this.code;
      this.code = value;
      this.firePropertyChange(PROPERTY_CODE, oldValue, value);
      return this;
   }

   public String getProduct()
   {
      return this.product;
   }

   public PickTask setProduct(String value)
   {
      if (Objects.equals(value, this.product))
      {
         return this;
      }

      final String oldValue = this.product;
      this.product = value;
      this.firePropertyChange(PROPERTY_PRODUCT, oldValue, value);
      return this;
   }

   public String getShelf()
   {
      return this.shelf;
   }

   public PickTask setShelf(String value)
   {
      if (Objects.equals(value, this.shelf))
      {
         return this;
      }

      final String oldValue = this.shelf;
      this.shelf = value;
      this.firePropertyChange(PROPERTY_SHELF, oldValue, value);
      return this;
   }

   public String getCustomer()
   {
      return this.customer;
   }

   public PickTask setCustomer(String value)
   {
      if (Objects.equals(value, this.customer))
      {
         return this;
      }

      final String oldValue = this.customer;
      this.customer = value;
      this.firePropertyChange(PROPERTY_CUSTOMER, oldValue, value);
      return this;
   }

   public String getAddress()
   {
      return this.address;
   }

   public PickTask setAddress(String value)
   {
      if (Objects.equals(value, this.address))
      {
         return this;
      }

      final String oldValue = this.address;
      this.address = value;
      this.firePropertyChange(PROPERTY_ADDRESS, oldValue, value);
      return this;
   }

   public String getState()
   {
      return this.state;
   }

   public PickTask setState(String value)
   {
      if (Objects.equals(value, this.state))
      {
         return this;
      }

      final String oldValue = this.state;
      this.state = value;
      this.firePropertyChange(PROPERTY_STATE, oldValue, value);
      return this;
   }

   public String getFrom()
   {
      return this.from;
   }

   public PickTask setFrom(String value)
   {
      if (Objects.equals(value, this.from))
      {
         return this;
      }

      final String oldValue = this.from;
      this.from = value;
      this.firePropertyChange(PROPERTY_FROM, oldValue, value);
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
      result.append(' ').append(this.getCode());
      result.append(' ').append(this.getProduct());
      result.append(' ').append(this.getShelf());
      result.append(' ').append(this.getCustomer());
      result.append(' ').append(this.getAddress());
      result.append(' ').append(this.getState());
      result.append(' ').append(this.getFrom());
      return result.substring(1);
   }
}
