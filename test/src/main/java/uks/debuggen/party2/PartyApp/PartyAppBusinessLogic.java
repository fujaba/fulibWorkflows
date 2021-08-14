package uks.debuggen.party2.PartyApp;
import java.util.LinkedHashMap;
import java.util.function.Consumer;
import uks.debuggen.party2.events.*;
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

   private void handleCheckNameCommand(Event e)
   {
      // to protect manuel changes to this method insert a 'no' in front of fulib in the next line
      // fulib
      CheckNameCommand event = (CheckNameCommand) e;
      handleDemoCheckNameCommand(event);
   }

   private void handleDemoCheckNameCommand(CheckNameCommand event)
   {
      if (event.getId().equals("12:01")) {
      }
      if (event.getId().equals("13:01")) {
      }
   }

   private void handleCheckEmailCommand(Event e)
   {
      // to protect manuel changes to this method insert a 'no' in front of fulib in the next line
      // fulib
      CheckEmailCommand event = (CheckEmailCommand) e;
      handleDemoCheckEmailCommand(event);
   }

   private void handleDemoCheckEmailCommand(CheckEmailCommand event)
   {
      if (event.getId().equals("12:03")) {
      }
   }

   private void handleCheckPasswordCommand(Event e)
   {
      // to protect manuel changes to this method insert a 'no' in front of fulib in the next line
      // fulib
      CheckPasswordCommand event = (CheckPasswordCommand) e;
      handleDemoCheckPasswordCommand(event);
   }

   private void handleDemoCheckPasswordCommand(CheckPasswordCommand event)
   {
      if (event.getId().equals("12:05")) {
         UserBuilt aliceEvent = new UserBuilt();
         aliceEvent.setId("12:05:02");
         aliceEvent.setBlockId("Alice");
         aliceEvent.setName("Alice");
         aliceEvent.setEmail("a@b.de");
         aliceEvent.setPassword("secret");
         service.apply(aliceEvent);


         UserRegisteredEvent e120503 = new UserRegisteredEvent();

         e120503.setId("12:05:03");
         e120503.setName("Alice");
         service.apply(e120503);

         LoginSucceededEvent e120504 = new LoginSucceededEvent();

         e120504.setId("12:05:04");
         e120504.setName("Alice");
         service.apply(e120504);
      }
      if (event.getId().equals("13:03")) {
      }
   }

   private void handleGetPartyCommand(Event e)
   {
      // to protect manuel changes to this method insert a 'no' in front of fulib in the next line
      // fulib
      GetPartyCommand event = (GetPartyCommand) e;
      handleDemoGetPartyCommand(event);
   }

   private void handleDemoGetPartyCommand(GetPartyCommand event)
   {
      if (event.getId().equals("14:01")) {
         PartyBuilt sE_BBQEvent = new PartyBuilt();
         sE_BBQEvent.setId("14:01:02");
         sE_BBQEvent.setBlockId("sE_BBQ");
         sE_BBQEvent.setName("SE BBQ");
         sE_BBQEvent.setLocation("Uni");
         service.apply(sE_BBQEvent);

      }
   }

   private void handleBuildItemCommand(Event e)
   {
      // to protect manuel changes to this method insert a 'no' in front of fulib in the next line
      // fulib
      BuildItemCommand event = (BuildItemCommand) e;
      handleDemoBuildItemCommand(event);
   }

   private void handleDemoBuildItemCommand(BuildItemCommand event)
   {
      if (event.getId().equals("14:05")) {
         ItemBuilt beerEvent = new ItemBuilt();
         beerEvent.setId("14:05:01");
         beerEvent.setBlockId("beer");
         beerEvent.setName("beer");
         beerEvent.setPrice("12.00");
         beerEvent.setBuyer("sE_BBQ_Bob");
         beerEvent.setParty("sE_BBQ");
         service.apply(beerEvent);

         GuestBuilt sE_BBQ_BobEvent = new GuestBuilt();
         sE_BBQ_BobEvent.setId("14:05:02");
         sE_BBQ_BobEvent.setBlockId("sE_BBQ_Bob");
         sE_BBQ_BobEvent.setName("Bob");
         sE_BBQ_BobEvent.setParty("sE_BBQ");
         service.apply(sE_BBQ_BobEvent);

      }
      if (event.getId().equals("14:09")) {
         ItemBuilt meatEvent = new ItemBuilt();
         meatEvent.setId("14:09:01");
         meatEvent.setBlockId("meat");
         meatEvent.setName("meat");
         meatEvent.setPrice("21.00");
         meatEvent.setBuyer("sE_BBQ_Alice");
         meatEvent.setParty("sE_BBQ");
         service.apply(meatEvent);

         GuestBuilt sE_BBQ_AliceEvent = new GuestBuilt();
         sE_BBQ_AliceEvent.setId("14:09:02");
         sE_BBQ_AliceEvent.setBlockId("sE_BBQ_Alice");
         sE_BBQ_AliceEvent.setName("Alice");
         sE_BBQ_AliceEvent.setExpenses("0.00");
         sE_BBQ_AliceEvent.setParty("sE_BBQ");
         service.apply(sE_BBQ_AliceEvent);

      }
   }

   public void initEventHandlerMap()
   {
      if (handlerMap == null) {
         handlerMap = new LinkedHashMap<>();
         handlerMap.put(CheckNameCommand.class, this::handleCheckNameCommand);
         handlerMap.put(CheckEmailCommand.class, this::handleCheckEmailCommand);
         handlerMap.put(CheckPasswordCommand.class, this::handleCheckPasswordCommand);
         handlerMap.put(GetPartyCommand.class, this::handleGetPartyCommand);
         handlerMap.put(BuildItemCommand.class, this::handleBuildItemCommand);
         handlerMap.put(UserBuilt.class, builder::handleUserBuilt);
         handlerMap.put(PartyBuilt.class, builder::handlePartyBuilt);
         handlerMap.put(ItemBuilt.class, builder::handleItemBuilt);
         handlerMap.put(GuestBuilt.class, builder::handleGuestBuilt);
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
