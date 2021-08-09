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

   public void initEventHandlerMap()
   {
      if (handlerMap == null) {
         handlerMap = new LinkedHashMap<>();
         handlerMap.put(GetUserNameCommand.class, this::handleGetUserNameCommand);
         handlerMap.put(GetPasswordCommand.class, this::handleGetPasswordCommand);
         handlerMap.put(UserBuilt.class, builder::handleUserBuilt);
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

   private void handleGetUserNameCommand(Event e)
   {
      // to protect manuel changes to this method insert a 'no' in front of fulib in the next line
      // fulib
      GetUserNameCommand event = (GetUserNameCommand) e;
      handleDemoGetUserNameCommand(event);
   }

   private void handleDemoGetUserNameCommand(GetUserNameCommand event)
   {
      if (event.getId().equals("12:01")) {
      }
      if (event.getId().equals("13:01")) {
      }
   }

   private void handleGetPasswordCommand(Event e)
   {
      // to protect manuel changes to this method insert a 'no' in front of fulib in the next line
      // fulib
      GetPasswordCommand event = (GetPasswordCommand) e;
      handleDemoGetPasswordCommand(event);
   }

   private void handleDemoGetPasswordCommand(GetPasswordCommand event)
   {
      if (event.getId().equals("12:07")) {
         UserBuilt aliceEvent = new UserBuilt();
         aliceEvent.setId("12:07:01");
         aliceEvent.setBlockId("Alice");
         aliceEvent.setName("Alice");
         aliceEvent.setEmail("a@b.de");
         aliceEvent.setPassword("secret");
         service.apply(aliceEvent);


         LoginSucceededEvent e1210 = new LoginSucceededEvent();

         e1210.setId("12:10");
         e1210.setName("Alice");
         service.apply(e1210);
      }
   }
}
