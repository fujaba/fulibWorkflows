package uks.fulibgen.shop.Shop;
import java.util.LinkedHashMap;
import java.util.function.Consumer;
import uks.fulibgen.shop.events.*;
import static org.fulib.workflows.StrUtil.stripBrackets;
import java.util.Objects;
import java.beans.PropertyChangeSupport;

public class ShopBusinessLogic
{
   public static final String PROPERTY_MODEL = "model";
   public static final String PROPERTY_HANDLER_MAP = "handlerMap";
   public static final String PROPERTY_SERVICE = "service";
   public static final String PROPERTY_BUILDER = "builder";
   private ShopModel model;
   private LinkedHashMap<Class, Consumer<Event>> handlerMap;
   private ShopService service;
   protected PropertyChangeSupport listeners;
   private ShopBuilder builder;

   public ShopModel getModel()
   {
      return this.model;
   }

   public ShopBusinessLogic setModel(ShopModel value)
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

   public LinkedHashMap<Class, Consumer<Event>> getHandlerMap()
   {
      return this.handlerMap;
   }

   public ShopBusinessLogic setHandlerMap(LinkedHashMap<Class, Consumer<Event>> value)
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

   public ShopService getService()
   {
      return this.service;
   }

   public ShopBusinessLogic setService(ShopService value)
   {
      if (this.service == value)
      {
         return this;
      }

      final ShopService oldValue = this.service;
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

   public ShopBuilder getBuilder()
   {
      return this.builder;
   }

   public ShopBusinessLogic setBuilder(ShopBuilder value)
   {
      if (this.builder == value)
      {
         return this;
      }

      final ShopBuilder oldValue = this.builder;
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

   private void handleOrderApprovedEvent(Event e)
   {
      // no fulib
      OrderApprovedEvent event = (OrderApprovedEvent) e;
      handleDemoOrderApprovedEvent(event);
   }

   private void handleDemoOrderApprovedEvent(OrderApprovedEvent event)
   {
      if (event.getId().equals("13:06")) {
         OrderBuilt order1300Event = new OrderBuilt();
         order1300Event.setId("13:06:01");
         order1300Event.setBlockId("order1300");
         order1300Event.setState("picking");
         service.apply(order1300Event);

      }
   }

   private void handleOrderPickedEvent(Event e)
   {
      // no fulib
      OrderPickedEvent event = (OrderPickedEvent) e;
      handleDemoOrderPickedEvent(event);
   }

   private void handleDemoOrderPickedEvent(OrderPickedEvent event)
   {
      if (event.getId().equals("14:03")) {
         OrderBuilt order1300Event = new OrderBuilt();
         order1300Event.setId("14:04");
         order1300Event.setBlockId("order1300");
         order1300Event.setState("shipping");
         service.apply(order1300Event);

      }
   }

   private void handleOrderDeclinedEvent(Event e)
   {
      // no fulib
      OrderDeclinedEvent event = (OrderDeclinedEvent) e;
      handleDemoOrderDeclinedEvent(event);
   }

   private void handleDemoOrderDeclinedEvent(OrderDeclinedEvent event)
   {
      if (event.getId().equals("13:15")) {
         OrderBuilt order1310Event = new OrderBuilt();
         order1310Event.setId("13:16");
         order1310Event.setBlockId("order1310");
         order1310Event.setState("out of stock");
         service.apply(order1310Event);

      }
   }

   public void initEventHandlerMap()
   {
      if (handlerMap == null) {
         handlerMap = new LinkedHashMap<>();
         handlerMap.put(OrderBuilt.class, builder::storeOrderBuilt);
         handlerMap.put(CustomerBuilt.class, builder::storeCustomerBuilt);
         handlerMap.put(SubmitOrderCommand.class, this::handleSubmitOrderCommand);
         handlerMap.put(OrderApprovedEvent.class, this::handleOrderApprovedEvent);
         handlerMap.put(OrderPickedEvent.class, this::handleOrderPickedEvent);
         handlerMap.put(OrderDeclinedEvent.class, this::handleOrderDeclinedEvent);
      }
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

   private void ignoreEvent(Event event)
   {
      // empty
   }

   public Consumer<Event> getHandler(Event event)
   {
      return getHandlerMap().computeIfAbsent(event.getClass(), k -> this::ignoreEvent);
   }

   private void handleSubmitOrderCommand(Event e)
   {
      // no fulib
      SubmitOrderCommand event = (SubmitOrderCommand) e;
      handleDemoSubmitOrderCommand(event);
   }

   private void handleDemoSubmitOrderCommand(SubmitOrderCommand event)
   {
      if (event.getId().equals("13:01")) {
         OrderBuilt order1300Event = new OrderBuilt();
         order1300Event.setId("13:02");
         order1300Event.setBlockId("order1300");
         order1300Event.setProduct("shoes");
         order1300Event.setCustomer("Alice");
         order1300Event.setAddress("Wonderland 1");
         order1300Event.setState("pending");
         service.apply(order1300Event);

         CustomerBuilt aliceEvent = new CustomerBuilt();
         aliceEvent.setId("13:03");
         aliceEvent.setBlockId("Alice");
         aliceEvent.setOrders("[order1300]");
         service.apply(aliceEvent);


         OrderRegisteredEvent e1304 = new OrderRegisteredEvent();

         e1304.setId("13:04");
         e1304.setOrder("order1300");
         e1304.setProduct("shoes");
         e1304.setCustomer("Alice");
         e1304.setAddress("Wonderland 1");
         service.apply(e1304);
      }
      if (event.getId().equals("13:11")) {
         OrderBuilt order1310Event = new OrderBuilt();
         order1310Event.setId("13:12");
         order1310Event.setBlockId("order1310");
         order1310Event.setProduct("tshirt");
         order1310Event.setCustomer("Alice");
         order1310Event.setAddress("Wonderland 1");
         order1310Event.setState("pending");
         service.apply(order1310Event);

         CustomerBuilt aliceEvent = new CustomerBuilt();
         aliceEvent.setId("13:13");
         aliceEvent.setBlockId("Alice");
         aliceEvent.setOrders("[order1300, order1310]");
         service.apply(aliceEvent);


         OrderRegisteredEvent e1314 = new OrderRegisteredEvent();

         e1314.setId("13:14");
         e1314.setOrder("order1310");
         e1314.setTrigger("button OK");
         e1314.setProduct("tshirt");
         e1314.setCustomer("Alice");
         e1314.setAddress("Wonderland 1");
         service.apply(e1314);
      }
   }
}
