package uks.debuggen.interconnect.CityElectricsKassel;
import java.util.LinkedHashMap;
import java.util.function.Consumer;
import uks.debuggen.interconnect.events.*;
import java.util.Objects;
import java.beans.PropertyChangeSupport;

public class CityElectricsKasselBusinessLogic
{
   public static final String PROPERTY_MODEL = "model";
   public static final String PROPERTY_HANDLER_MAP = "handlerMap";
   public static final String PROPERTY_BUILDER = "builder";
   public static final String PROPERTY_SERVICE = "service";
   private CityElectricsKasselModel model;
   private LinkedHashMap<Class, Consumer<Event>> handlerMap;
   private CityElectricsKasselBuilder builder;
   private CityElectricsKasselService service;
   protected PropertyChangeSupport listeners;

   public CityElectricsKasselModel getModel()
   {
      return this.model;
   }

   public CityElectricsKasselBusinessLogic setModel(CityElectricsKasselModel value)
   {
      if (Objects.equals(value, this.model))
      {
         return this;
      }

      final CityElectricsKasselModel oldValue = this.model;
      this.model = value;
      this.firePropertyChange(PROPERTY_MODEL, oldValue, value);
      return this;
   }

   public LinkedHashMap<Class, Consumer<Event>> getHandlerMap()
   {
      return this.handlerMap;
   }

   public CityElectricsKasselBusinessLogic setHandlerMap(LinkedHashMap<Class, Consumer<Event>> value)
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

   public CityElectricsKasselBuilder getBuilder()
   {
      return this.builder;
   }

   public CityElectricsKasselBusinessLogic setBuilder(CityElectricsKasselBuilder value)
   {
      if (this.builder == value)
      {
         return this;
      }

      final CityElectricsKasselBuilder oldValue = this.builder;
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

   public CityElectricsKasselService getService()
   {
      return this.service;
   }

   public CityElectricsKasselBusinessLogic setService(CityElectricsKasselService value)
   {
      if (this.service == value)
      {
         return this;
      }

      final CityElectricsKasselService oldValue = this.service;
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

   private void handleChargingRequestedEvent(Event e)
   {
      // to protect manuel changes to this method insert a 'no' in front of fulib in the next line
      // fulib
      ChargingRequestedEvent event = (ChargingRequestedEvent) e;
      handleDemoChargingRequestedEvent(event);
   }

   private void handleDemoChargingRequestedEvent(ChargingRequestedEvent event)
   {
      if (event.getId().equals("12:30")) {

         ChargingApprovedEvent e1231 = new ChargingApprovedEvent();

         e1231.setId("12:31");
         e1231.setRequestId("IonicKassel42_1230");
         e1231.setChargerId("IonicKassel42");
         e1231.setPower("100 KW");
         e1231.setInterval("12:30 - 12:50");
         service.apply(e1231);
      }
   }

   public void initEventHandlerMap()
   {
      if (handlerMap == null) {
         handlerMap = new LinkedHashMap<>();
         handlerMap.put(ChargingRequestedEvent.class, this::handleChargingRequestedEvent);
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
