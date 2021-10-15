package uks.fulibgen.shop.Storage;
import java.util.LinkedHashMap;
import java.util.function.Consumer;
import uks.fulibgen.shop.events.*;
import java.util.Objects;
import java.beans.PropertyChangeSupport;

public class StorageBusinessLogic
{
   public static final String PROPERTY_MODEL = "model";
   public static final String PROPERTY_HANDLER_MAP = "handlerMap";
   public static final String PROPERTY_BUILDER = "builder";
   public static final String PROPERTY_SERVICE = "service";
   private StorageModel model;
   private LinkedHashMap<Class, Consumer<Event>> handlerMap;
   private StorageBuilder builder;
   private StorageService service;
   protected PropertyChangeSupport listeners;

   public StorageModel getModel()
   {
      return this.model;
   }

   public StorageBusinessLogic setModel(StorageModel value)
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

   public LinkedHashMap<Class, Consumer<Event>> getHandlerMap()
   {
      return this.handlerMap;
   }

   public StorageBusinessLogic setHandlerMap(LinkedHashMap<Class, Consumer<Event>> value)
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

   public StorageBuilder getBuilder()
   {
      return this.builder;
   }

   public StorageBusinessLogic setBuilder(StorageBuilder value)
   {
      if (this.builder == value)
      {
         return this;
      }

      final StorageBuilder oldValue = this.builder;
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

   public StorageService getService()
   {
      return this.service;
   }

   public StorageBusinessLogic setService(StorageService value)
   {
      if (this.service == value)
      {
         return this;
      }

      final StorageService oldValue = this.service;
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

   private void handleStoreBoxCommand(Event e)
   {
      // to protect manuel changes to this method insert a 'no' in front of fulib in the next line
      // fulib
      StoreBoxCommand event = (StoreBoxCommand) e;
      handleDemoStoreBoxCommand(event);
   }

   private void handleDemoStoreBoxCommand(StoreBoxCommand event)
   {
      if (event.getId().equals("12:00")) {
         BoxBuilt box23Event = new BoxBuilt();
         box23Event.setId("12:01");
         box23Event.setBlockId("box23");
         box23Event.setProduct("shoes");
         box23Event.setPlace("shelf23");
         service.apply(box23Event);


         ProductStoredEvent e1202 = new ProductStoredEvent();

         e1202.setId("12:02");
         e1202.setBox("box23");
         e1202.setProduct("shoes");
         e1202.setPlace("shelf23");
         service.apply(e1202);
      }
   }

   private void handleOrderRegisteredEvent(Event e)
   {
      // to protect manuel changes to this method insert a 'no' in front of fulib in the next line
      // fulib
      OrderRegisteredEvent event = (OrderRegisteredEvent) e;
      handleDemoOrderRegisteredEvent(event);
   }

   private void handleDemoOrderRegisteredEvent(OrderRegisteredEvent event)
   {
      if (event.getId().equals("13:04")) {
         PickTaskBuilt pick1300Event = new PickTaskBuilt();
         pick1300Event.setId("13:05");
         pick1300Event.setBlockId("pick1300");
         pick1300Event.setOrder("order1300");
         pick1300Event.setProduct("shoes");
         pick1300Event.setCustomer("Alice");
         pick1300Event.setAddress("Wonderland 1");
         pick1300Event.setState("todo");
         service.apply(pick1300Event);


         OrderApprovedEvent e1306 = new OrderApprovedEvent();

         e1306.setId("13:06");
         e1306.setOrder("order1300");
         service.apply(e1306);
      }
      if (event.getId().equals("13:14")) {

         OrderDeclinedEvent e1315 = new OrderDeclinedEvent();

         e1315.setId("13:15");
         e1315.setOrder("order1310");
         service.apply(e1315);
      }
   }

   private void handlePickOrderCommand(Event e)
   {
      // to protect manuel changes to this method insert a 'no' in front of fulib in the next line
      // fulib
      PickOrderCommand event = (PickOrderCommand) e;
      handleDemoPickOrderCommand(event);
   }

   private void handleDemoPickOrderCommand(PickOrderCommand event)
   {
      if (event.getId().equals("14:00")) {
         PickTaskBuilt pick1300Event = new PickTaskBuilt();
         pick1300Event.setId("14:01");
         pick1300Event.setBlockId("pick1300");
         pick1300Event.setState("done");
         pick1300Event.setBox("box23");
         service.apply(pick1300Event);

         BoxBuilt box23Event = new BoxBuilt();
         box23Event.setId("14:02");
         box23Event.setBlockId("box23");
         box23Event.setPlace("shipping");
         service.apply(box23Event);


         OrderPickedEvent e1403 = new OrderPickedEvent();

         e1403.setId("14:03");
         e1403.setPickTask("pick1300");
         e1403.setBox("box23");
         e1403.setUser("Bob");
         service.apply(e1403);
      }
   }

   public void initEventHandlerMap()
   {
      if (handlerMap == null) {
         handlerMap = new LinkedHashMap<>();
         handlerMap.put(BoxBuilt.class, builder::storeBoxBuilt);
         handlerMap.put(PickTaskBuilt.class, builder::storePickTaskBuilt);
         handlerMap.put(StoreBoxCommand.class, this::handleStoreBoxCommand);
         handlerMap.put(OrderRegisteredEvent.class, this::handleOrderRegisteredEvent);
         handlerMap.put(PickOrderCommand.class, this::handlePickOrderCommand);
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
