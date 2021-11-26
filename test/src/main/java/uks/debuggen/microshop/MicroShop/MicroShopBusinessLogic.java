package uks.debuggen.microshop.MicroShop;
import java.util.LinkedHashMap;
import java.util.function.Consumer;
import uks.debuggen.microshop.events.*;
import java.util.Objects;
import java.beans.PropertyChangeSupport;

public class MicroShopBusinessLogic
{
   public static final String PROPERTY_MODEL = "model";
   public static final String PROPERTY_HANDLER_MAP = "handlerMap";
   public static final String PROPERTY_BUILDER = "builder";
   public static final String PROPERTY_SERVICE = "service";
   private MicroShopModel model;
   private LinkedHashMap<Class, Consumer<Event>> handlerMap;
   private MicroShopBuilder builder;
   private MicroShopService service;
   protected PropertyChangeSupport listeners;

   public MicroShopModel getModel()
   {
      return this.model;
   }

   public MicroShopBusinessLogic setModel(MicroShopModel value)
   {
      if (Objects.equals(value, this.model))
      {
         return this;
      }

      final MicroShopModel oldValue = this.model;
      this.model = value;
      this.firePropertyChange(PROPERTY_MODEL, oldValue, value);
      return this;
   }

   public LinkedHashMap<Class, Consumer<Event>> getHandlerMap()
   {
      return this.handlerMap;
   }

   public MicroShopBusinessLogic setHandlerMap(LinkedHashMap<Class, Consumer<Event>> value)
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

   public MicroShopBuilder getBuilder()
   {
      return this.builder;
   }

   public MicroShopBusinessLogic setBuilder(MicroShopBuilder value)
   {
      if (this.builder == value)
      {
         return this;
      }

      final MicroShopBuilder oldValue = this.builder;
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

   public MicroShopService getService()
   {
      return this.service;
   }

   public MicroShopBusinessLogic setService(MicroShopService value)
   {
      if (this.service == value)
      {
         return this;
      }

      final MicroShopService oldValue = this.service;
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

   private void handleAddCommand(Event e)
   {
      // to protect manuel changes to this method insert a 'no' in front of fulib in the next line
      // fulib
      AddCommand event = (AddCommand) e;
      handleDemoAddCommand(event);
   }

   private void handleDemoAddCommand(AddCommand event)
   {
      if (event.getId().equals("12:17:01")) {
         MSProductBuilt blue_jeansEvent = new MSProductBuilt();
         blue_jeansEvent.setId("12:17:02");
         blue_jeansEvent.setBlockId("blue_jeans");
         blue_jeansEvent.setName("blue_jeans");
         blue_jeansEvent.setPrice("$63");
         service.apply(blue_jeansEvent);


         ProductOfferedEvent e1218 = new ProductOfferedEvent();

         e1218.setId("12:18");
         e1218.setName("blue_jeans");
         e1218.setPrice("$63");
         service.apply(e1218);
      }
   }

   public void initEventHandlerMap()
   {
      if (handlerMap == null) {
         handlerMap = new LinkedHashMap<>();
         handlerMap.put(MSProductBuilt.class, builder::storeMSProductBuilt);
         handlerMap.put(OrderBuilt.class, builder::storeOrderBuilt);
         handlerMap.put(CustomerBuilt.class, builder::storeCustomerBuilt);
         handlerMap.put(ProductStoredEvent.class, this::handleProductStoredEvent);
         handlerMap.put(AddOfferCommand.class, this::handleAddOfferCommand);
         handlerMap.put(AddCommand.class, this::handleAddCommand);
         handlerMap.put(PlaceCommand.class, this::handlePlaceCommand);
         handlerMap.put(PickTaskCreatedEvent.class, this::handlePickTaskCreatedEvent);
         handlerMap.put(OrderPickedEvent.class, this::handleOrderPickedEvent);
         handlerMap.put(DeliverCommand.class, this::handleDeliverCommand);
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

   private void handlePlaceCommand(Event e)
   {
      // to protect manuel changes to this method insert a 'no' in front of fulib in the next line
      // fulib
      PlaceCommand event = (PlaceCommand) e;
      handleDemoPlaceCommand(event);
   }

   private void handleDemoPlaceCommand(PlaceCommand event)
   {
      if (event.getId().equals("12:21:01")) {
         OrderBuilt o0925_1Event = new OrderBuilt();
         o0925_1Event.setId("12:21:02");
         o0925_1Event.setBlockId("o0925_1");
         o0925_1Event.setCode("o0925_1");
         o0925_1Event.setProduct("red_shoes");
         o0925_1Event.setCustomer("Carli_Customer");
         o0925_1Event.setAddress("Wonderland 1");
         o0925_1Event.setState("new order");
         service.apply(o0925_1Event);

         CustomerBuilt carli_CustomerEvent = new CustomerBuilt();
         carli_CustomerEvent.setId("12:21:03");
         carli_CustomerEvent.setBlockId("Carli_Customer");
         carli_CustomerEvent.setName("Carli_Customer");
         service.apply(carli_CustomerEvent);

         MSProductBuilt red_shoesEvent = new MSProductBuilt();
         red_shoesEvent.setId("12:21:04");
         red_shoesEvent.setBlockId("red_shoes");
         red_shoesEvent.setName("red_shoes");
         red_shoesEvent.setAmount("17");
         service.apply(red_shoesEvent);


         ProductOrderedEvent e1222 = new ProductOrderedEvent();

         e1222.setId("12:22");
         e1222.setCode("o0925_1");
         e1222.setProduct("red_shoes");
         e1222.setCustomer("Carli_Customer");
         e1222.setAddress("Wonderland 1");
         service.apply(e1222);
      }
   }

   private void handleOrderPickedEvent(Event e)
   {
      // to protect manuel changes to this method insert a 'no' in front of fulib in the next line
      // fulib
      OrderPickedEvent event = (OrderPickedEvent) e;
      handleDemoOrderPickedEvent(event);
   }

   private void handleDemoOrderPickedEvent(OrderPickedEvent event)
   {
      if (event.getId().equals("12:27")) {
         OrderBuilt o0925_1Event = new OrderBuilt();
         o0925_1Event.setId("12:27:01");
         o0925_1Event.setBlockId("o0925_1");
         o0925_1Event.setCode("o0925_1");
         o0925_1Event.setState("shipping");
         service.apply(o0925_1Event);

      }
   }

   private void handlePickTaskCreatedEvent(Event e)
   {
      // to protect manuel changes to this method insert a 'no' in front of fulib in the next line
      // fulib
      PickTaskCreatedEvent event = (PickTaskCreatedEvent) e;
      handleDemoPickTaskCreatedEvent(event);
   }

   private void handleDemoPickTaskCreatedEvent(PickTaskCreatedEvent event)
   {
      if (event.getId().equals("12:23")) {
         OrderBuilt o0925_1Event = new OrderBuilt();
         o0925_1Event.setId("12:23:01");
         o0925_1Event.setBlockId("o0925_1");
         o0925_1Event.setCode("o0925_1");
         o0925_1Event.setState("picking");
         service.apply(o0925_1Event);

      }
   }

   private void handleAddOfferCommand(Event e)
   {
      // to protect manuel changes to this method insert a 'no' in front of fulib in the next line
      // fulib
      AddOfferCommand event = (AddOfferCommand) e;
      handleDemoAddOfferCommand(event);
   }

   private void handleDemoAddOfferCommand(AddOfferCommand event)
   {
      if (event.getId().equals("12:14:42")) {
         MSProductBuilt red_shoesEvent = new MSProductBuilt();
         red_shoesEvent.setId("12:14:43");
         red_shoesEvent.setBlockId("red_shoes");
         red_shoesEvent.setName("red_shoes");
         red_shoesEvent.setPrice("$42");
         service.apply(red_shoesEvent);


         ProductOfferedEvent e1215 = new ProductOfferedEvent();

         e1215.setId("12:15");
         e1215.setName("red_shoes");
         e1215.setPrice("$42");
         service.apply(e1215);
      }
   }

   private void handleDeliverCommand(Event e)
   {
      // to protect manuel changes to this method insert a 'no' in front of fulib in the next line
      // fulib
      DeliverCommand event = (DeliverCommand) e;
      handleDemoDeliverCommand(event);
   }

   private void handleDemoDeliverCommand(DeliverCommand event)
   {
      if (event.getId().equals("12:30:01")) {
         OrderBuilt o0925_1Event = new OrderBuilt();
         o0925_1Event.setId("12:30:02");
         o0925_1Event.setBlockId("o0925_1");
         o0925_1Event.setCode("o0925_1");
         o0925_1Event.setState("delivered");
         service.apply(o0925_1Event);


         OrderDeliveredEvent e1231 = new OrderDeliveredEvent();

         e1231.setId("12:31");
         e1231.setOrder("o0925_1");
         service.apply(e1231);
      }
   }

   private void handleProductStoredEvent(Event e)
   {
      // to protect manuel changes to this method insert a 'no' in front of fulib in the next line
      // fulib
      ProductStoredEvent event = (ProductStoredEvent) e;
      handleDemoProductStoredEvent(event);
   }

   private void handleDemoProductStoredEvent(ProductStoredEvent event)
   {
      if (event.getId().equals("12:04")) {
         MSProductBuilt red_shoesEvent = new MSProductBuilt();
         red_shoesEvent.setId("12:04:01");
         red_shoesEvent.setBlockId("red_shoes");
         red_shoesEvent.setName("red_shoes");
         red_shoesEvent.setAmount("10");
         red_shoesEvent.setState("in stock");
         service.apply(red_shoesEvent);

      }
      if (event.getId().equals("12:07")) {
         MSProductBuilt red_shoesEvent = new MSProductBuilt();
         red_shoesEvent.setId("12:07:01");
         red_shoesEvent.setBlockId("red_shoes");
         red_shoesEvent.setName("red_shoes");
         red_shoesEvent.setAmount("18");
         red_shoesEvent.setState("in stock");
         service.apply(red_shoesEvent);

      }
      if (event.getId().equals("12:10")) {
         MSProductBuilt blue_jeansEvent = new MSProductBuilt();
         blue_jeansEvent.setId("12:10:01");
         blue_jeansEvent.setBlockId("blue_jeans");
         blue_jeansEvent.setName("blue_jeans");
         blue_jeansEvent.setState("in stock");
         blue_jeansEvent.setAmount("6");
         service.apply(blue_jeansEvent);

      }
   }
}
