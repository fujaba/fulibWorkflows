package uks.fulibgen.shop.Storage;
import java.util.LinkedHashMap;
import java.util.function.Consumer;
import uks.fulibgen.shop.events.*;
import static org.fulib.workflows.StrUtil.stripBrackets;
import java.util.Objects;
import java.beans.PropertyChangeSupport;

public class StorageBusinessLogic
{
   public static final String PROPERTY_MODEL = "model";
   public static final String PROPERTY_HANDLER_MAP = "handlerMap";
   public static final String PROPERTY_SERVICE = "service";
   public static final String PROPERTY_BUILDER = "builder";
   private StorageModel model;
   private LinkedHashMap<Class, Consumer<Event>> handlerMap;
   private StorageService service;
   protected PropertyChangeSupport listeners;
   private StorageBuilder builder;

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

   private void handleProductStoredEvent(Event e)
   {
      // no fulib
      ProductStoredEvent event = (ProductStoredEvent) e;
      handleDemoProductStoredEvent(event);
   }

   private void handleDemoProductStoredEvent(ProductStoredEvent event)
   {
      if (event.getId().equals("12:00")) {
         BoxBuilt box23Event = new BoxBuilt();
         box23Event.setId("12:02");
         box23Event.setBlockId("box23");
         box23Event.setProduct("shoes");
         box23Event.setPlace("shelf23");
         service.apply(box23Event);

      }
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
         PickTaskBuilt pick1300Event = new PickTaskBuilt();
         pick1300Event.setId("13:04");
         pick1300Event.setBlockId("pick1300");
         pick1300Event.setOrder("order1300");
         pick1300Event.setProduct("shoes");
         pick1300Event.setCustomer("Alice");
         pick1300Event.setAddress("Wonderland 1");
         pick1300Event.setState("todo");
         service.apply(pick1300Event);


         OrderApprovedEvent e1305 = new OrderApprovedEvent();

         e1305.setId("13:05");
         e1305.setOrder("order1300");
         service.apply(e1305);
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

         OrderDeclinedEvent e1314 = new OrderDeclinedEvent();

         e1314.setId("13:14");
         e1314.setOrder("order1310");
         service.apply(e1314);
      }
   }

   public void initEventHandlerMap()
   {
      if (handlerMap == null) {
         handlerMap = new LinkedHashMap<>();
         handlerMap.put(ProductStoredEvent.class, this::handleProductStoredEvent);
         handlerMap.put(OrderRegisteredCommand.class, this::handleOrderRegisteredCommand);
         handlerMap.put(OrderPickedEvent.class, this::handleOrderPickedEvent);
         handlerMap.put(OrderRegisteredEvent.class, this::handleOrderRegisteredEvent);
         handlerMap.put(BoxBuilt.class, builder::handleBoxBuilt);
         handlerMap.put(PickTaskBuilt.class, builder::handlePickTaskBuilt);
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
}
