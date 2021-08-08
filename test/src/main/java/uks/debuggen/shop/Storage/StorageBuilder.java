package uks.debuggen.shop.Storage;
import java.util.LinkedHashMap;
import java.util.function.Consumer;
import uks.debuggen.shop.events.*;
import java.util.Objects;
import java.beans.PropertyChangeSupport;

public class StorageBuilder
{
   public static final String PROPERTY_MODEL = "model";
   public static final String PROPERTY_BUSINESS_LOGIC = "businessLogic";
   private StorageModel model;
   private StorageBusinessLogic businessLogic;
   protected PropertyChangeSupport listeners;

   public StorageModel getModel()
   {
      return this.model;
   }

   public StorageBuilder setModel(StorageModel value)
   {
      if (Objects.equals(value, this.model))
      {
         return this;
      }

      final StorageModel oldValue = this.model;
      this.model = value;
      this.firePropertyChange(PROPERTY_MODEL, oldValue, value);
      return this;
   }

   public StorageBusinessLogic getBusinessLogic()
   {
      return this.businessLogic;
   }

   public StorageBuilder setBusinessLogic(StorageBusinessLogic value)
   {
      if (this.businessLogic == value)
      {
         return this;
      }

      final StorageBusinessLogic oldValue = this.businessLogic;
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

   public void handleBoxBuilt(Event e)
   {
      BoxBuilt event = (BoxBuilt) e;
      Box object = model.getOrCreateBox(event.getBlockId());
      object.setProduct(event.getProduct());
      object.setPlace(event.getPlace());
   }

   public void handlePickTaskBuilt(Event e)
   {
      PickTaskBuilt event = (PickTaskBuilt) e;
      PickTask object = model.getOrCreatePickTask(event.getBlockId());
      object.setOrder(event.getOrder());
      object.setProduct(event.getProduct());
      object.setCustomer(event.getCustomer());
      object.setAddress(event.getAddress());
      object.setState(event.getState());
      object.setBox(event.getBox());
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
