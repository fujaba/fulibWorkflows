package uks.debuggen.microshop.Warehouse;
import java.util.LinkedHashMap;
import java.util.function.Consumer;
import uks.debuggen.microshop.events.*;
import java.util.Objects;
import java.beans.PropertyChangeSupport;

public class WarehouseBusinessLogic
{
   public static final String PROPERTY_MODEL = "model";
   public static final String PROPERTY_HANDLER_MAP = "handlerMap";
   public static final String PROPERTY_BUILDER = "builder";
   public static final String PROPERTY_SERVICE = "service";
   private WarehouseModel model;
   private LinkedHashMap<Class, Consumer<Event>> handlerMap;
   private WarehouseBuilder builder;
   private WarehouseService service;
   protected PropertyChangeSupport listeners;

   public WarehouseModel getModel()
   {
      return this.model;
   }

   public WarehouseBusinessLogic setModel(WarehouseModel value)
   {
      if (Objects.equals(value, this.model))
      {
         return this;
      }

      final WarehouseModel oldValue = this.model;
      this.model = value;
      this.firePropertyChange(PROPERTY_MODEL, oldValue, value);
      return this;
   }

   public LinkedHashMap<Class, Consumer<Event>> getHandlerMap()
   {
      return this.handlerMap;
   }

   public WarehouseBusinessLogic setHandlerMap(LinkedHashMap<Class, Consumer<Event>> value)
   {
      if (Objects.equals(value, this.handlerMap))
      {
         return this;
      }

      final LinkedHashMap<Class, Consumer<Event>> oldValue = this.handlerMap;
      this.handlerMap = value;
      this.firePropertyChange(PROPERTY_HANDLER_MAP, oldValue, value);
      return this;
   }

   public WarehouseBuilder getBuilder()
   {
      return this.builder;
   }

   public WarehouseBusinessLogic setBuilder(WarehouseBuilder value)
   {
      if (this.builder == value)
      {
         return this;
      }

      final WarehouseBuilder oldValue = this.builder;
      if (this.builder != null)
      {
         this.builder = null;
         oldValue.setBusinessLogic(null);
      }
      this.builder = value;
      if (value != null)
      {
         value.setBusinessLogic(this);
      }
      this.firePropertyChange(PROPERTY_BUILDER, oldValue, value);
      return this;
   }

   public WarehouseService getService()
   {
      return this.service;
   }

   public WarehouseBusinessLogic setService(WarehouseService value)
   {
      if (this.service == value)
      {
         return this;
      }

      final WarehouseService oldValue = this.service;
      if (this.service != null)
      {
         this.service = null;
         oldValue.setBusinessLogic(null);
      }
      this.service = value;
      if (value != null)
      {
         value.setBusinessLogic(this);
      }
      this.firePropertyChange(PROPERTY_SERVICE, oldValue, value);
      return this;
   }

   private void handleStoreProductCommand(Event e)
   {
      // to protect manuel changes to this method insert a 'no' in front of fulib in the next line
      // fulib
      StoreProductCommand event = (StoreProductCommand) e;
      handleDemoStoreProductCommand(event);
   }

   private void handleDemoStoreProductCommand(StoreProductCommand event)
   {
      if (event.getId().equals("12:01")) {
         BoxBuilt b001Event = new BoxBuilt();
         b001Event.setId("12:01:01");
         b001Event.setBlockId("b001");
         b001Event.setBarcode("b001");
         b001Event.setContent("red shoes");
         b001Event.setLocation("shelf 42");
         service.apply(b001Event);


         ProductStoredEvent e1202 = new ProductStoredEvent();

         e1202.setId("12:02");
         e1202.setBarcode("b001");
         e1202.setType("red shoes");
         service.apply(e1202);

         ProductOfferedEvent e1203 = new ProductOfferedEvent();

         e1203.setId("12:03");
         service.apply(e1203);

         ProductOrderedEvent e1204 = new ProductOrderedEvent();

         e1204.setId("12:04");
         service.apply(e1204);

         OrderPickedEvent e1205 = new OrderPickedEvent();

         e1205.setId("12:05");
         service.apply(e1205);

         OrderDeliveredEvent e1206 = new OrderDeliveredEvent();

         e1206.setId("12:06");
         service.apply(e1206);

         ProductOrderedEvent e1301 = new ProductOrderedEvent();

         e1301.setId("13:01");
         service.apply(e1301);

         OrderRejectedEvent e1302 = new OrderRejectedEvent();

         e1302.setId("13:02");
         service.apply(e1302);
      }
   }

   public void initEventHandlerMap()
   {
      if (handlerMap == null) {
         handlerMap = new LinkedHashMap<>();
         handlerMap.put(BoxBuilt.class, builder::storeBoxBuilt);
         handlerMap.put(StoreProductCommand.class, this::handleStoreProductCommand);
      }
   }

   private void ignoreEvent(Event event)
   {
      // empty
   }

   public Consumer<Event> getHandler(Event event)
   {
      return getHandlerMap().computeIfAbsent(event.getClass(), k -> this::ignoreEvent);
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
      this.setBuilder(null);
      this.setService(null);
   }
}
