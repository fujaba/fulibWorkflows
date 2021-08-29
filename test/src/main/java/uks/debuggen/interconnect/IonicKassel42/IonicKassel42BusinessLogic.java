package uks.debuggen.interconnect.IonicKassel42;
import java.util.LinkedHashMap;
import java.util.function.Consumer;
import uks.debuggen.interconnect.events.*;
import java.util.Objects;
import java.beans.PropertyChangeSupport;

public class IonicKassel42BusinessLogic
{
   public static final String PROPERTY_MODEL = "model";
   public static final String PROPERTY_HANDLER_MAP = "handlerMap";
   public static final String PROPERTY_BUILDER = "builder";
   public static final String PROPERTY_SERVICE = "service";
   private IonicKassel42Model model;
   private LinkedHashMap<Class, Consumer<Event>> handlerMap;
   private IonicKassel42Builder builder;
   private IonicKassel42Service service;
   protected PropertyChangeSupport listeners;

   public IonicKassel42Model getModel()
   {
      return this.model;
   }

   public IonicKassel42BusinessLogic setModel(IonicKassel42Model value)
   {
      if (Objects.equals(value, this.model))
      {
         return this;
      }

      final IonicKassel42Model oldValue = this.model;
      this.model = value;
      this.firePropertyChange(PROPERTY_MODEL, oldValue, value);
      return this;
   }

   public LinkedHashMap<Class, Consumer<Event>> getHandlerMap()
   {
      return this.handlerMap;
   }

   public IonicKassel42BusinessLogic setHandlerMap(LinkedHashMap<Class, Consumer<Event>> value)
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

   public IonicKassel42Builder getBuilder()
   {
      return this.builder;
   }

   public IonicKassel42BusinessLogic setBuilder(IonicKassel42Builder value)
   {
      if (this.builder == value)
      {
         return this;
      }

      final IonicKassel42Builder oldValue = this.builder;
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

   public IonicKassel42Service getService()
   {
      return this.service;
   }

   public IonicKassel42BusinessLogic setService(IonicKassel42Service value)
   {
      if (this.service == value)
      {
         return this;
      }

      final IonicKassel42Service oldValue = this.service;
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

   private void handleCarConnectedEvent(Event e)
   {
      // to protect manuel changes to this method insert a 'no' in front of fulib in the next line
      // fulib
      CarConnectedEvent event = (CarConnectedEvent) e;
      handleDemoCarConnectedEvent(event);
   }

   private void handleDemoCarConnectedEvent(CarConnectedEvent event)
   {
      if (event.getId().equals("11:55")) {

         ChargerInstalledEvent e1200 = new ChargerInstalledEvent();

         e1200.setId("12:00");
         e1200.setChargerId("IonicKassel42");
         e1200.setAddress("Wilhelmshoeher Alle 73");
         e1200.setPower("100 KW");
         service.apply(e1200);

         ChargingRequestedEvent e1230 = new ChargingRequestedEvent();

         e1230.setId("12:30");
         e1230.setRequestId("IonicKassel42_1230");
         e1230.setChargerId("IonicKassel42");
         e1230.setPower("100 KW");
         e1230.setInterval("12:30 - 12:50");
         service.apply(e1230);
      }
   }

   public void initEventHandlerMap()
   {
      if (handlerMap == null) {
         handlerMap = new LinkedHashMap<>();
         handlerMap.put(CarConnectedEvent.class, this::handleCarConnectedEvent);
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
