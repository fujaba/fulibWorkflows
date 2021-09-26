package uks.debuggen.microshop.MicroShop;
import java.util.Objects;
import java.beans.PropertyChangeSupport;

public class Order
{
   public static final String PROPERTY_ID = "id";
   public static final String PROPERTY_CODE = "code";
   public static final String PROPERTY_ADDRESS = "address";
   public static final String PROPERTY_STATE = "state";
   public static final String PROPERTY_PRODUCT = "product";
   public static final String PROPERTY_CUSTOMER = "customer";
   private String id;
   private String code;
   private String address;
   private String state;
   protected PropertyChangeSupport listeners;
   private Product product;
   private Customer customer;

   public String getId()
   {
      return this.id;
   }

   public Order setId(String value)
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

   public Order setCode(String value)
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

   public String getAddress()
   {
      return this.address;
   }

   public Order setAddress(String value)
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

   public Order setState(String value)
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

   public Product getProduct()
   {
      return this.product;
   }

   public Order setProduct(Product value)
   {
      if (this.product == value)
      {
         return this;
      }

      final Product oldValue = this.product;
      if (this.product != null)
      {
         this.product = null;
         oldValue.withoutOrders(this);
      }
      this.product = value;
      if (value != null)
      {
         value.withOrders(this);
      }
      this.firePropertyChange(PROPERTY_PRODUCT, oldValue, value);
      return this;
   }

   public Customer getCustomer()
   {
      return this.customer;
   }

   public Order setCustomer(Customer value)
   {
      if (this.customer == value)
      {
         return this;
      }

      final Customer oldValue = this.customer;
      if (this.customer != null)
      {
         this.customer = null;
         oldValue.withoutOrders(this);
      }
      this.customer = value;
      if (value != null)
      {
         value.withOrders(this);
      }
      this.firePropertyChange(PROPERTY_CUSTOMER, oldValue, value);
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
      result.append(' ').append(this.getAddress());
      result.append(' ').append(this.getState());
      return result.substring(1);
   }

   public void removeYou()
   {
      this.setProduct(null);
      this.setCustomer(null);
   }
}
