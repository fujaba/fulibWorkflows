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

   public void initEventHandlerMap()
   {
      if (handlerMap == null) {
         handlerMap = new LinkedHashMap<>();
         handlerMap.put(BoxBuilt.class, builder::storeBoxBuilt);
         handlerMap.put(StoreCommand.class, this::handleStoreCommand);
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

   private void handleStoreCommand(Event e)
   {
      // to protect manuel changes to this method insert a 'no' in front of fulib in the next line
      // fulib
      StoreCommand event = (StoreCommand) e;
      handleDemoStoreCommand(event);
   }

   private void handleDemoStoreCommand(StoreCommand event)
   {
      if (event.getId().equals("12:02:01")) {
         BoxBuilt b001Event = new BoxBuilt();
         b001Event.setId("12:02:02");
         b001Event.setBlockId("b001");
         b001Event.setBarcode("b001");
         b001Event.setContent("red shoes");
         b001Event.setLocation("shelf 42");
         service.apply(b001Event);


         ProductStoredEvent e1203 = new ProductStoredEvent();

         e1203.setId("12:03");
         e1203.setBarcode("b001");
         e1203.setType("red shoes");
         service.apply(e1203);
      }
      if (event.getId().equals("12:05:01")) {
         BoxBuilt b002Event = new BoxBuilt();
         b002Event.setId("12:05:02");
         b002Event.setBlockId("b002");
         b002Event.setBarcode("b002");
         b002Event.setContent("red shoes");
         b002Event.setLocation("shelf 23");
         service.apply(b002Event);


         ProductStoredEvent e1206 = new ProductStoredEvent();

         e1206.setId("12:06");
         e1206.setBarcode("b002");
         e1206.setType("red shoes");
         service.apply(e1206);
      }
      if (event.getId().equals("12:08:01")) {
         BoxBuilt b003Event = new BoxBuilt();
         b003Event.setId("12:08:02");
         b003Event.setBlockId("b003");
         b003Event.setBarcode("b003");
         b003Event.setContent("blue jeans");
         b003Event.setLocation("shelf 1337");
         service.apply(b003Event);


         ProductStoredEvent e1209 = new ProductStoredEvent();

         e1209.setId("12:09");
         e1209.setBarcode("b003");
         e1209.setType("blue jeans");
         service.apply(e1209);
      }
   }
}
