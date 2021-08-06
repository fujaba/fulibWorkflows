package uks.fulibgen.shop.Shop;
import java.util.LinkedHashMap;
import java.util.function.Consumer;
import uks.fulibgen.shop.events.*;
import java.util.Objects;
import java.beans.PropertyChangeSupport;

public class ShopBuilder
{
   public static final String PROPERTY_MODEL = "model";
   public static final String PROPERTY_BUSINESS_LOGIC = "businessLogic";
   private ShopModel model;
   private ShopBusinessLogic businessLogic;
   protected PropertyChangeSupport listeners;

   public ShopModel getModel()
   {
      return this.model;
   }

   public ShopBuilder setModel(ShopModel value)
   {
      if (Objects.equals(value, this.model))
      {
         return this;
      }

      final ShopModel oldValue = this.model;
      this.model = value;
      this.firePropertyChange(PROPERTY_MODEL, oldValue, value);
      return this;
   }

   public ShopBusinessLogic getBusinessLogic()
   {
      return this.businessLogic;
   }

   public ShopBuilder setBusinessLogic(ShopBusinessLogic value)
   {
      if (this.businessLogic == value)
      {
         return this;
      }

      final ShopBusinessLogic oldValue = this.businessLogic;
      if (this.businessLogic != null)
      {
         this.businessLogic = null;
         oldValue.setBuilder(null);
      }
      this.businessLogic = value;
      if (value != null)
      {
         value.setBuilder(this);
      }
      this.firePropertyChange(PROPERTY_BUSINESS_LOGIC, oldValue, value);
      return this;
   }

   public void handleOrderBuilt(Event e)
   {
      OrderBuilt event = (OrderBuilt) e;
      Order object = model.getOrCreateOrder(event.getBlockId());
      object.setProduct(event.getProduct());
      object.setCustomer(event.getCustomer());
      object.setAddress(event.getAddress());
      object.setState(event.getState());
   }

   public void handleCustomerBuilt(Event e)
   {
      CustomerBuilt event = (CustomerBuilt) e;
      Customer object = model.getOrCreateCustomer(event.getBlockId());
      object.setOrders(event.getOrders());
   }

   public String stripBrackets(String back)
   {
      if (back == null) {
         return "";
      }
      int open = back.indexOf('[');
      int close = back.indexOf(']');
      if (open >= 0 && close >= 0) {
         back = back.substring(open + 1, close);
      }
      return back;
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

   public void removeYou()
   {
      this.setBusinessLogic(null);
   }
}
