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
         handlerMap.put(PaletteBuilt.class, builder::storePaletteBuilt);
         handlerMap.put(WHProductBuilt.class, builder::storeWHProductBuilt);
         handlerMap.put(PickTaskBuilt.class, builder::storePickTaskBuilt);
         handlerMap.put(StoreProductCommand.class, this::handleStoreProductCommand);
         handlerMap.put(ProductOrderedEvent.class, this::handleProductOrderedEvent);
         handlerMap.put(Command.class, this::handleCommand);
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

   private void handleProductOrderedEvent(Event e)
   {
      // to protect manuel changes to this method insert a 'no' in front of fulib in the next line
      // fulib
      ProductOrderedEvent event = (ProductOrderedEvent) e;
      handleDemoProductOrderedEvent(event);
   }

   private void handleDemoProductOrderedEvent(ProductOrderedEvent event)
   {
      if (event.getId().equals("12:22")) {
         PickTaskBuilt pt_o0925_1Event = new PickTaskBuilt();
         pt_o0925_1Event.setId("12:22:01");
         pt_o0925_1Event.setBlockId("pt_o0925_1");
         pt_o0925_1Event.setCode("pt_o0925_1");
         pt_o0925_1Event.setProduct("red_shoes");
         pt_o0925_1Event.setShelf("[shelf_42, shelf_23]");
         pt_o0925_1Event.setCustomer("Carli_Customer");
         pt_o0925_1Event.setAddress("Wonderland 1");
         pt_o0925_1Event.setState("picking");
         service.apply(pt_o0925_1Event);


         PickTaskCreatedEvent e1223 = new PickTaskCreatedEvent();

         e1223.setId("12:23");
         e1223.setCode("pt_o0925_1");
         e1223.setOrder("o0925_1");
         service.apply(e1223);
      }
   }

   private void handleCommand(Event e)
   {
      // to protect manuel changes to this method insert a 'no' in front of fulib in the next line
      // fulib
      Command event = (Command) e;
      handleDemoCommand(event);
   }

   private void handleDemoCommand(Command event)
   {
      if (event.getId().equals("12:26:01")) {
         PickTaskBuilt pt_o0925_1Event = new PickTaskBuilt();
         pt_o0925_1Event.setId("12:26:02");
         pt_o0925_1Event.setBlockId("pt_o0925_1");
         pt_o0925_1Event.setCode("pt_o0925_1");
         pt_o0925_1Event.setFrom("shelf_42");
         pt_o0925_1Event.setPalette("b001");
         pt_o0925_1Event.setState("shipping");
         service.apply(pt_o0925_1Event);

         PaletteBuilt b001Event = new PaletteBuilt();
         b001Event.setId("12:26:03");
         b001Event.setBlockId("b001");
         b001Event.setBarcode("b001");
         b001Event.setAmount("9");
         service.apply(b001Event);

         WHProductBuilt red_shoesEvent = new WHProductBuilt();
         red_shoesEvent.setId("12:26:04");
         red_shoesEvent.setBlockId("red_shoes");
         red_shoesEvent.setName("red_shoes");
         red_shoesEvent.setAmount("17");
         service.apply(red_shoesEvent);


         OrderPickedEvent e1227 = new OrderPickedEvent();

         e1227.setId("12:27");
         e1227.setOrder("o0925_1");
         service.apply(e1227);
      }
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
      if (event.getId().equals("12:03:01")) {
         PaletteBuilt b001Event = new PaletteBuilt();
         b001Event.setId("12:03:02");
         b001Event.setBlockId("b001");
         b001Event.setBarcode("b001");
         b001Event.setProduct("red_shoes");
         b001Event.setAmount("10");
         b001Event.setLocation("shelf_42");
         service.apply(b001Event);

         WHProductBuilt red_shoesEvent = new WHProductBuilt();
         red_shoesEvent.setId("12:03:03");
         red_shoesEvent.setBlockId("red_shoes");
         red_shoesEvent.setName("red_shoes");
         red_shoesEvent.setAmount("10");
         service.apply(red_shoesEvent);


         ProductStoredEvent e1204 = new ProductStoredEvent();

         e1204.setId("12:04");
         e1204.setBarcode("b001");
         e1204.setProduct("red_shoes");
         e1204.setAmount("10");
         service.apply(e1204);
      }
      if (event.getId().equals("12:06:01")) {
         PaletteBuilt b002Event = new PaletteBuilt();
         b002Event.setId("12:06:02");
         b002Event.setBlockId("b002");
         b002Event.setBarcode("b002");
         b002Event.setProduct("red_shoes");
         b002Event.setAmount("8");
         b002Event.setLocation("shelf_23");
         service.apply(b002Event);

         WHProductBuilt red_shoesEvent = new WHProductBuilt();
         red_shoesEvent.setId("12:06:03");
         red_shoesEvent.setBlockId("red_shoes");
         red_shoesEvent.setName("red_shoes");
         red_shoesEvent.setAmount("18");
         service.apply(red_shoesEvent);


         ProductStoredEvent e1207 = new ProductStoredEvent();

         e1207.setId("12:07");
         e1207.setBarcode("b002");
         e1207.setProduct("red_shoes");
         e1207.setAmount("8");
         service.apply(e1207);
      }
      if (event.getId().equals("12:09:01")) {
         PaletteBuilt b003Event = new PaletteBuilt();
         b003Event.setId("12:09:02");
         b003Event.setBlockId("b003");
         b003Event.setBarcode("b003");
         b003Event.setProduct("blue_jeans");
         b003Event.setAmount("6");
         b003Event.setLocation("shelf_1337");
         service.apply(b003Event);

         WHProductBuilt blue_jeansEvent = new WHProductBuilt();
         blue_jeansEvent.setId("12:09:03");
         blue_jeansEvent.setBlockId("blue_jeans");
         blue_jeansEvent.setName("blue_jeans");
         blue_jeansEvent.setAmount("6");
         service.apply(blue_jeansEvent);


         ProductStoredEvent e1210 = new ProductStoredEvent();

         e1210.setId("12:10");
         e1210.setBarcode("b003");
         e1210.setProduct("blue_jeans");
         e1210.setAmount("6");
         service.apply(e1210);
      }
   }
}
