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
   public static final String PROPERTY_SERVICE = "service";
   public static final String PROPERTY_EVENT_STORE = "eventStore";
   private ShopModel model;
   private ShopBusinessLogic businessLogic;
   protected PropertyChangeSupport listeners;
   private ShopService service;
   private LinkedHashMap<String, DataEvent> eventStore = new LinkedHashMap<>();

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

   public ShopService getService()
   {
      return this.service;
   }

   public ShopBuilder setService(ShopService value)
   {
      if (this.service == value)
      {
         return this;
      }

      final ShopService oldValue = this.service;
      if (this.service != null)
      {
         this.service = null;
         oldValue.setBuilder(null);
      }
      this.service = value;
      if (value != null)
      {
         value.setBuilder(this);
      }
      this.firePropertyChange(PROPERTY_SERVICE, oldValue, value);
      return this;
   }

   public LinkedHashMap<String, DataEvent> getEventStore()
   {
      return this.eventStore;
   }

   public ShopBuilder setEventStore(LinkedHashMap<String, DataEvent> value)
   {
      if (Objects.equals(value, this.eventStore))
      {
         return this;
      }

      final LinkedHashMap<String, DataEvent> oldValue = this.eventStore;
      this.eventStore = value;
      this.firePropertyChange(PROPERTY_EVENT_STORE, oldValue, value);
      return this;
   }

   public void handleOrderBuilt(Event e)
   {
      OrderBuilt event = (OrderBuilt) e;
      if (outdated(event)) {
         return;
      }
      Order object = model.getOrCreateOrder(event.getBlockId());
      object.setProduct(event.getProduct());
      object.setCustomer(event.getCustomer());
      object.setAddress(event.getAddress());
      object.setState(event.getState());
   }

   public void handleCustomerBuilt(Event e)
   {
      CustomerBuilt event = (CustomerBuilt) e;
      if (outdated(event)) {
         return;
      }
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
      this.setService(null);
   }

   private boolean outdated(DataEvent event)
   {
      DataEvent oldEvent = getEventStore().get(event.getBlockId());

      if (oldEvent == null) {
         eventStore.put(event.getBlockId(), event);
         return false;
      }

      if (oldEvent.getId().compareTo(event.getId()) < 0) {
         eventStore.put(event.getBlockId(), event);
         return false;
      }

      return true;
   }
}
