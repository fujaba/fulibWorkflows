package uks.debuggen.shop.Shop;
import java.util.LinkedHashMap;
import java.util.function.Consumer;
import uks.debuggen.shop.events.*;
import java.util.Objects;
import java.beans.PropertyChangeSupport;

public class ShopBusinessLogic
{
   public static final String PROPERTY_MODEL = "model";
   public static final String PROPERTY_HANDLER_MAP = "handlerMap";
   public static final String PROPERTY_SERVICE = "service";
   private ShopModel model;
   private LinkedHashMap<Class, Consumer<Event>> handlerMap;
   private ShopService service;
   protected PropertyChangeSupport listeners;

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

   private void handleOrderRegisteredCommand(Event e)
   {
      // no fulib
      OrderRegisteredCommand event = (OrderRegisteredCommand) e;
      handleDemoOrderRegisteredCommand(event);
   }

   private void handleDemoOrderRegisteredCommand(OrderRegisteredCommand event)
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
         aliceEvent.setOrders("[ order1300 ]");
         service.apply(aliceEvent);

      }
   }

   private void handleOrderApprovedEvent(Event e)
   {
      // no fulib
      OrderApprovedEvent event = (OrderApprovedEvent) e;
      handleDemoOrderApprovedEvent(event);
   }

   private void handleDemoOrderApprovedEvent(OrderApprovedEvent event)
   {
      if (event.getId().equals("13:05")) {
         OrderBuilt order1300Event = new OrderBuilt();
         order1300Event.setId("13:06");
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
      if (event.getId().equals("14:00")) {
         OrderBuilt order1300Event = new OrderBuilt();
         order1300Event.setId("14:03");
         order1300Event.setBlockId("order1300");
         order1300Event.setState("shipping");
         service.apply(order1300Event);

      }
   }

   private void handleOrderRegisteredEvent(Event e)
   {
      // no fulib
      OrderRegisteredEvent event = (OrderRegisteredEvent) e;
      handleDemoOrderRegisteredEvent(event);
   }

   private void handleDemoOrderRegisteredEvent(OrderRegisteredEvent event)
   {
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
         aliceEvent.setOrders("[ order1300 order1310 ]");
         service.apply(aliceEvent);

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
      if (event.getId().equals("13:14")) {
         OrderBuilt order1310Event = new OrderBuilt();
         order1310Event.setId("13:12");
         order1310Event.setBlockId("order1310");
         order1310Event.setState("out of stock");
         service.apply(order1310Event);

      }
   }

   private void handleOrderBuilt(Event e)
   {
      OrderBuilt event = (OrderBuilt) e;
      Order object = model.getOrCreateOrder(event.getBlockId());
      object.setProduct(event.getProduct());
      object.setCustomer(event.getCustomer());
      object.setAddress(event.getAddress());
      object.setState(event.getState());
   }

   private void handleCustomerBuilt(Event e)
   {
      CustomerBuilt event = (CustomerBuilt) e;
      Customer object = model.getOrCreateCustomer(event.getBlockId());
      object.setOrders(event.getOrders());
   }

   public void initEventHandlerMap()
   {
      if (handlerMap == null) {
         handlerMap = new LinkedHashMap<>();
         handlerMap.put(OrderRegisteredCommand.class, this::handleOrderRegisteredCommand);
         handlerMap.put(OrderApprovedEvent.class, this::handleOrderApprovedEvent);
         handlerMap.put(OrderPickedEvent.class, this::handleOrderPickedEvent);
         handlerMap.put(OrderRegisteredEvent.class, this::handleOrderRegisteredEvent);
         handlerMap.put(OrderDeclinedEvent.class, this::handleOrderDeclinedEvent);
         handlerMap.put(OrderBuilt.class, this::handleOrderBuilt);
         handlerMap.put(CustomerBuilt.class, this::handleCustomerBuilt);
      }
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
      this.setService(null);
   }
}
