package uks.debuggen.pm.clickCounter.ClickCounter;
import java.util.LinkedHashMap;
import java.util.function.Consumer;
import uks.debuggen.pm.clickCounter.events.*;
import java.util.Objects;
import java.beans.PropertyChangeSupport;

public class ClickCounterBusinessLogic
{
   public static final String PROPERTY_MODEL = "model";
   public static final String PROPERTY_HANDLER_MAP = "handlerMap";
   public static final String PROPERTY_BUILDER = "builder";
   public static final String PROPERTY_SERVICE = "service";
   private ClickCounterModel model;
   private LinkedHashMap<Class, Consumer<Event>> handlerMap;
   private ClickCounterBuilder builder;
   private ClickCounterService service;
   protected PropertyChangeSupport listeners;

   public ClickCounterModel getModel()
   {
      return this.model;
   }

   public ClickCounterBusinessLogic setModel(ClickCounterModel value)
   {
      if (Objects.equals(value, this.model))
      {
         return this;
      }

      final ClickCounterModel oldValue = this.model;
      this.model = value;
      this.firePropertyChange(PROPERTY_MODEL, oldValue, value);
      return this;
   }

   public LinkedHashMap<Class, Consumer<Event>> getHandlerMap()
   {
      return this.handlerMap;
   }

   public ClickCounterBusinessLogic setHandlerMap(LinkedHashMap<Class, Consumer<Event>> value)
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

   public ClickCounterBuilder getBuilder()
   {
      return this.builder;
   }

   public ClickCounterBusinessLogic setBuilder(ClickCounterBuilder value)
   {
      if (this.builder == value)
      {
         return this;
      }

      final ClickCounterBuilder oldValue = this.builder;
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

   public ClickCounterService getService()
   {
      return this.service;
   }

   public ClickCounterBusinessLogic setService(ClickCounterService value)
   {
      if (this.service == value)
      {
         return this;
      }

      final ClickCounterService oldValue = this.service;
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
         handlerMap.put(CounterBuilt.class, builder::storeCounterBuilt);
         handlerMap.put(DoCountCommand.class, this::handleDoCountCommand);
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

   private void handleDoCountCommand(Event e)
   {
      // to protect manuel changes to this method insert a 'no' in front of fulib in the next line
      // fulib
      DoCountCommand event = (DoCountCommand) e;
      handleDemoDoCountCommand(event);
   }

   private void handleDemoDoCountCommand(DoCountCommand event)
   {
      if (event.getId().equals("12:01:01")) {
         CounterBuilt modelEvent = new CounterBuilt();
         modelEvent.setId("12:01:02");
         modelEvent.setBlockId("model");
         modelEvent.setCount("1");
         service.apply(modelEvent);

      }
      if (event.getId().equals("12:02:01")) {
         CounterBuilt modelEvent = new CounterBuilt();
         modelEvent.setId("12:02:02");
         modelEvent.setBlockId("model");
         modelEvent.setCount("2");
         service.apply(modelEvent);

      }
      if (event.getId().equals("12:03:01")) {
         CounterBuilt modelEvent = new CounterBuilt();
         modelEvent.setId("12:03:02");
         modelEvent.setBlockId("model");
         modelEvent.setCount("3");
         service.apply(modelEvent);

      }
   }
}
