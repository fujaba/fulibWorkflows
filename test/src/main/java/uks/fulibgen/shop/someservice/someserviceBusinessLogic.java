package uks.fulibgen.shop.someservice;
import java.util.LinkedHashMap;
import java.util.function.Consumer;
import uks.fulibgen.shop.events.*;
import static org.fulib.workflows.StrUtil.stripBrackets;
import java.util.Objects;
import java.beans.PropertyChangeSupport;

public class someserviceBusinessLogic
{
   public static final String PROPERTY_MODEL = "model";
   public static final String PROPERTY_HANDLER_MAP = "handlerMap";
   public static final String PROPERTY_SERVICE = "service";
   public static final String PROPERTY_BUILDER = "builder";
   private someserviceModel model;
   private LinkedHashMap<Class, Consumer<Event>> handlerMap;
   private someserviceService service;
   protected PropertyChangeSupport listeners;
   private someserviceBuilder builder;

   public someserviceModel getModel()
   {
      return this.model;
   }

   public someserviceBusinessLogic setModel(someserviceModel value)
   {
      if (Objects.equals(value, this.model))
      {
         return this;
      }

      final someserviceModel oldValue = this.model;
      this.model = value;
      this.firePropertyChange(PROPERTY_MODEL, oldValue, value);
      return this;
   }

   public LinkedHashMap<Class, Consumer<Event>> getHandlerMap()
   {
      return this.handlerMap;
   }

   public someserviceBusinessLogic setHandlerMap(LinkedHashMap<Class, Consumer<Event>> value)
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

   public someserviceService getService()
   {
      return this.service;
   }

   public someserviceBusinessLogic setService(someserviceService value)
   {
      if (this.service == value)
      {
         return this;
      }

      final someserviceService oldValue = this.service;
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

   public someserviceBuilder getBuilder()
   {
      return this.builder;
   }

   public someserviceBusinessLogic setBuilder(someserviceBuilder value)
   {
      if (this.builder == value)
      {
         return this;
      }

      final someserviceBuilder oldValue = this.builder;
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
         box23Event.setId("12:01");
         box23Event.setBlockId("box23");
         box23Event.setProduct("shoes");
         box23Event.setPlace("shelf23");
         service.apply(box23Event);

      }
   }

   public void initEventHandlerMap()
   {
      if (handlerMap == null) {
         handlerMap = new LinkedHashMap<>();
         handlerMap.put(ProductStoredEvent.class, this::handleProductStoredEvent);
         handlerMap.put(BoxBuilt.class, builder::handleBoxBuilt);
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
}
