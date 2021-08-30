package uks.debuggen.medical.marburg.MarburgHealthSystem;
import java.util.LinkedHashMap;
import java.util.function.Consumer;
import uks.debuggen.medical.marburg.events.*;
import java.util.Objects;
import java.beans.PropertyChangeSupport;

public class MarburgHealthSystemBusinessLogic
{
   public static final String PROPERTY_MODEL = "model";
   public static final String PROPERTY_HANDLER_MAP = "handlerMap";
   public static final String PROPERTY_BUILDER = "builder";
   public static final String PROPERTY_SERVICE = "service";
   private MarburgHealthSystemModel model;
   private LinkedHashMap<Class, Consumer<Event>> handlerMap;
   private MarburgHealthSystemBuilder builder;
   private MarburgHealthSystemService service;
   protected PropertyChangeSupport listeners;

   public MarburgHealthSystemModel getModel()
   {
      return this.model;
   }

   public MarburgHealthSystemBusinessLogic setModel(MarburgHealthSystemModel value)
   {
      if (Objects.equals(value, this.model))
      {
         return this;
      }

      final MarburgHealthSystemModel oldValue = this.model;
      this.model = value;
      this.firePropertyChange(PROPERTY_MODEL, oldValue, value);
      return this;
   }

   public LinkedHashMap<Class, Consumer<Event>> getHandlerMap()
   {
      return this.handlerMap;
   }

   public MarburgHealthSystemBusinessLogic setHandlerMap(LinkedHashMap<Class, Consumer<Event>> value)
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

   public MarburgHealthSystemBuilder getBuilder()
   {
      return this.builder;
   }

   public MarburgHealthSystemBusinessLogic setBuilder(MarburgHealthSystemBuilder value)
   {
      if (this.builder == value)
      {
         return this;
      }

      final MarburgHealthSystemBuilder oldValue = this.builder;
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

   public MarburgHealthSystemService getService()
   {
      return this.service;
   }

   public MarburgHealthSystemBusinessLogic setService(MarburgHealthSystemService value)
   {
      if (this.service == value)
      {
         return this;
      }

      final MarburgHealthSystemService oldValue = this.service;
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

   private void handleLoadDiseasesCommand(Event e)
   {
      // to protect manuel changes to this method insert a 'no' in front of fulib in the next line
      // fulib
      LoadDiseasesCommand event = (LoadDiseasesCommand) e;
      handleDemoLoadDiseasesCommand(event);
   }

   private void handleDemoLoadDiseasesCommand(LoadDiseasesCommand event)
   {
      if (event.getId().equals("12:00")) {
         DiseaseBuilt commonColdEvent = new DiseaseBuilt();
         commonColdEvent.setId("12:00:01");
         commonColdEvent.setBlockId("commonCold");
         commonColdEvent.setName("common cold");
         commonColdEvent.setSymptoms("[runny nose, cough, hoarseness, medium fever]");
         commonColdEvent.setCounterSymptoms("[chills, joint pain]");
         service.apply(commonColdEvent);

         DiseaseBuilt influenzaEvent = new DiseaseBuilt();
         influenzaEvent.setId("12:00:02");
         influenzaEvent.setBlockId("influenza");
         influenzaEvent.setName("influenza");
         influenzaEvent.setSymptoms("[cough, medium fever, chills, joint pain, headache]");
         influenzaEvent.setCounterSymptoms("[lung noises]");
         service.apply(influenzaEvent);

         DiseaseBuilt pneumoniaEvent = new DiseaseBuilt();
         pneumoniaEvent.setId("12:00:03");
         pneumoniaEvent.setBlockId("pneumonia");
         pneumoniaEvent.setName("pneumonia");
         pneumoniaEvent.setSymptoms("[cough, medium fever, chills, joint pain, headache, lung noises]");
         service.apply(pneumoniaEvent);

         SymptomBuilt coughEvent = new SymptomBuilt();
         coughEvent.setId("12:00:04");
         coughEvent.setBlockId("cough");
         coughEvent.setName("cough");
         service.apply(coughEvent);

         SymptomBuilt runnyNoseEvent = new SymptomBuilt();
         runnyNoseEvent.setId("12:00:05");
         runnyNoseEvent.setBlockId("runnyNose");
         runnyNoseEvent.setName("runny nose");
         service.apply(runnyNoseEvent);

         SymptomBuilt hoarsenessEvent = new SymptomBuilt();
         hoarsenessEvent.setId("12:00:06");
         hoarsenessEvent.setBlockId("hoarseness");
         hoarsenessEvent.setName("hoarseness");
         service.apply(hoarsenessEvent);

         SymptomBuilt mediumFeverEvent = new SymptomBuilt();
         mediumFeverEvent.setId("12:00:07");
         mediumFeverEvent.setBlockId("mediumFever");
         mediumFeverEvent.setName("medium fever");
         service.apply(mediumFeverEvent);

         SymptomBuilt chillsEvent = new SymptomBuilt();
         chillsEvent.setId("12:00:08");
         chillsEvent.setBlockId("chills");
         chillsEvent.setName("chills");
         service.apply(chillsEvent);

         SymptomBuilt jointPainEvent = new SymptomBuilt();
         jointPainEvent.setId("12:00:09");
         jointPainEvent.setBlockId("jointPain");
         jointPainEvent.setName("joint pain");
         service.apply(jointPainEvent);

         SymptomBuilt headacheEvent = new SymptomBuilt();
         headacheEvent.setId("12:00:10");
         headacheEvent.setBlockId("headache");
         headacheEvent.setName("headache");
         service.apply(headacheEvent);

         SymptomBuilt lungNoisesEvent = new SymptomBuilt();
         lungNoisesEvent.setId("12:00:11");
         lungNoisesEvent.setBlockId("lungNoises");
         lungNoisesEvent.setName("lung noises");
         service.apply(lungNoisesEvent);

      }
   }

   public void initEventHandlerMap()
   {
      if (handlerMap == null) {
         handlerMap = new LinkedHashMap<>();
         handlerMap.put(LoadDiseasesCommand.class, this::handleLoadDiseasesCommand);
         handlerMap.put(DiseaseBuilt.class, builder::storeDiseaseBuilt);
         handlerMap.put(SymptomBuilt.class, builder::storeSymptomBuilt);
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
