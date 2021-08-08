package uks.debuggen.party.PartyApp;
import java.util.LinkedHashMap;
import java.util.function.Consumer;
import uks.debuggen.party.events.*;
import java.util.Objects;
import java.beans.PropertyChangeSupport;

public class PartyAppBusinessLogic
{
   public static final String PROPERTY_MODEL = "model";
   public static final String PROPERTY_HANDLER_MAP = "handlerMap";
   public static final String PROPERTY_BUILDER = "builder";
   public static final String PROPERTY_SERVICE = "service";
   private PartyAppModel model;
   private LinkedHashMap<Class, Consumer<Event>> handlerMap;
   private PartyAppBuilder builder;
   private PartyAppService service;
   protected PropertyChangeSupport listeners;

   public PartyAppModel getModel()
   {
      return this.model;
   }

   public PartyAppBusinessLogic setModel(PartyAppModel value)
   {
      if (Objects.equals(value, this.model))
      {
         return this;
      }

      final PartyAppModel oldValue = this.model;
      this.model = value;
      this.firePropertyChange(PROPERTY_MODEL, oldValue, value);
      return this;
   }

   public LinkedHashMap<Class, Consumer<Event>> getHandlerMap()
   {
      return this.handlerMap;
   }

   public PartyAppBusinessLogic setHandlerMap(LinkedHashMap<Class, Consumer<Event>> value)
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

   public PartyAppBuilder getBuilder()
   {
      return this.builder;
   }

   public PartyAppBusinessLogic setBuilder(PartyAppBuilder value)
   {
      if (this.builder == value)
      {
         return this;
      }

      final PartyAppBuilder oldValue = this.builder;
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

   public PartyAppService getService()
   {
      return this.service;
   }

   public PartyAppBusinessLogic setService(PartyAppService value)
   {
      if (this.service == value)
      {
         return this;
      }

      final PartyAppService oldValue = this.service;
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

   private void handleLoginCommand(Event e)
   {
      // no fulib
      LoginCommand event = (LoginCommand) e;
      handleDemoLoginCommand(event);
   }

   private void handleDemoLoginCommand(LoginCommand event)
   {
      if (event.getId().equals("12:01")) {

         LoginSucceededEvent e1210 = new LoginSucceededEvent();

         e1210.setId("12:10");
         e1210.setName("Alice");
         service.apply(e1210);
      }
   }

   public void initEventHandlerMap()
   {
      if (handlerMap == null) {
         handlerMap = new LinkedHashMap<>();
         handlerMap.put(LoginCommand.class, this::handleLoginCommand);
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
