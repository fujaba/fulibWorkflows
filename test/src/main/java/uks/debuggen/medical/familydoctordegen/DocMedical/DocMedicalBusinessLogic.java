package uks.debuggen.medical.familydoctordegen.DocMedical;
import java.util.LinkedHashMap;
import java.util.function.Consumer;
import uks.debuggen.medical.familydoctordegen.events.*;
import java.util.Objects;
import java.beans.PropertyChangeSupport;

public class DocMedicalBusinessLogic
{
   public static final String PROPERTY_MODEL = "model";
   public static final String PROPERTY_HANDLER_MAP = "handlerMap";
   public static final String PROPERTY_BUILDER = "builder";
   public static final String PROPERTY_SERVICE = "service";
   private DocMedicalModel model;
   private LinkedHashMap<Class, Consumer<Event>> handlerMap;
   private DocMedicalBuilder builder;
   private DocMedicalService service;
   protected PropertyChangeSupport listeners;

   public DocMedicalModel getModel()
   {
      return this.model;
   }

   public DocMedicalBusinessLogic setModel(DocMedicalModel value)
   {
      if (Objects.equals(value, this.model))
      {
         return this;
      }

      final DocMedicalModel oldValue = this.model;
      this.model = value;
      this.firePropertyChange(PROPERTY_MODEL, oldValue, value);
      return this;
   }

   public LinkedHashMap<Class, Consumer<Event>> getHandlerMap()
   {
      return this.handlerMap;
   }

   public DocMedicalBusinessLogic setHandlerMap(LinkedHashMap<Class, Consumer<Event>> value)
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

   public DocMedicalBuilder getBuilder()
   {
      return this.builder;
   }

   public DocMedicalBusinessLogic setBuilder(DocMedicalBuilder value)
   {
      if (this.builder == value)
      {
         return this;
      }

      final DocMedicalBuilder oldValue = this.builder;
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

   public DocMedicalService getService()
   {
      return this.service;
   }

   public DocMedicalBusinessLogic setService(DocMedicalService value)
   {
      if (this.service == value)
      {
         return this;
      }

      final DocMedicalService oldValue = this.service;
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

   private void handlePatientRegisteredEvent(Event e)
   {
      // to protect manuel changes to this method insert a 'no' in front of fulib in the next line
      // fulib
      PatientRegisteredEvent event = (PatientRegisteredEvent) e;
      handleDemoPatientRegisteredEvent(event);
   }

   private void handleDemoPatientRegisteredEvent(PatientRegisteredEvent event)
   {
      if (event.getId().equals("14:01")) {
         PatientBuilt aliceEvent = new PatientBuilt();
         aliceEvent.setId("14:01:01");
         aliceEvent.setBlockId("Alice");
         aliceEvent.setName("Alice");
         aliceEvent.setAddress("Wonderland 1");
         aliceEvent.setBirthDate("1970-01-01");
         service.apply(aliceEvent);

      }
   }

   public void initEventHandlerMap()
   {
      if (handlerMap == null) {
         handlerMap = new LinkedHashMap<>();
         handlerMap.put(DiseaseBuilt.class, builder::storeDiseaseBuilt);
         handlerMap.put(SymptomBuilt.class, builder::storeSymptomBuilt);
         handlerMap.put(PatientBuilt.class, builder::storePatientBuilt);
         handlerMap.put(ConsultationBuilt.class, builder::storeConsultationBuilt);
         handlerMap.put(TestBuilt.class, builder::storeTestBuilt);
         handlerMap.put(PatientRegisteredEvent.class, this::handlePatientRegisteredEvent);
         handlerMap.put(ConsultationRegisteredEvent.class, this::handleConsultationRegisteredEvent);
         handlerMap.put(SymptomValidatedEvent.class, this::handleSymptomValidatedEvent);
         handlerMap.put(TestDoneEvent.class, this::handleTestDoneEvent);
         handlerMap.put(DiagnosisDoneEvent.class, this::handleDiagnosisDoneEvent);
         handlerMap.put(TreatmentInitiatedEvent.class, this::handleTreatmentInitiatedEvent);
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

   private void handleConsultationRegisteredEvent(Event e)
   {
      // to protect manuel changes to this method insert a 'no' in front of fulib in the next line
      // fulib
      ConsultationRegisteredEvent event = (ConsultationRegisteredEvent) e;
      handleDemoConsultationRegisteredEvent(event);
   }

   private void handleDemoConsultationRegisteredEvent(ConsultationRegisteredEvent event)
   {
      if (event.getId().equals("14:02")) {
         ConsultationBuilt alice_2021_06_02T14_00Event = new ConsultationBuilt();
         alice_2021_06_02T14_00Event.setId("14:02:01");
         alice_2021_06_02T14_00Event.setBlockId("Alice_2021_06_02T14_00");
         alice_2021_06_02T14_00Event.setCid("Alice#2021-06-02T14:00");
         alice_2021_06_02T14_00Event.setPatient("Alice");
         service.apply(alice_2021_06_02T14_00Event);

      }
   }

   private void handleSymptomValidatedEvent(Event e)
   {
      // to protect manuel changes to this method insert a 'no' in front of fulib in the next line
      // fulib
      SymptomValidatedEvent event = (SymptomValidatedEvent) e;
      handleDemoSymptomValidatedEvent(event);
   }

   private void handleDemoSymptomValidatedEvent(SymptomValidatedEvent event)
   {
      if (event.getId().equals("14:03")) {
         SymptomBuilt coughEvent = new SymptomBuilt();
         coughEvent.setId("14:03:01");
         coughEvent.setBlockId("cough");
         coughEvent.setName("cough");
         coughEvent.setConsultations("[Alice#2021-06-02T14:00]");
         service.apply(coughEvent);

      }
      if (event.getId().equals("14:04")) {
         SymptomBuilt runny_noseEvent = new SymptomBuilt();
         runny_noseEvent.setId("14:04:01");
         runny_noseEvent.setBlockId("runny_nose");
         runny_noseEvent.setName("runny nose");
         runny_noseEvent.setConsultations("[Alice#2021-06-02T14:00]");
         service.apply(runny_noseEvent);

      }
   }

   private void handleTestDoneEvent(Event e)
   {
      // to protect manuel changes to this method insert a 'no' in front of fulib in the next line
      // fulib
      TestDoneEvent event = (TestDoneEvent) e;
      handleDemoTestDoneEvent(event);
   }

   private void handleDemoTestDoneEvent(TestDoneEvent event)
   {
      if (event.getId().equals("14:05")) {
         TestBuilt alice_t001_2021_06_02T14_00Event = new TestBuilt();
         alice_t001_2021_06_02T14_00Event.setId("14:05:01");
         alice_t001_2021_06_02T14_00Event.setBlockId("Alice_t001_2021_06_02T14_00");
         alice_t001_2021_06_02T14_00Event.setCid("Alice#t001#2021-06-02T14:00");
         alice_t001_2021_06_02T14_00Event.setKind("temperature");
         alice_t001_2021_06_02T14_00Event.setResult("39.8 Celsius");
         alice_t001_2021_06_02T14_00Event.setConsultation("Alice#2021-06-02T14:00");
         service.apply(alice_t001_2021_06_02T14_00Event);

         SymptomBuilt medium_feverEvent = new SymptomBuilt();
         medium_feverEvent.setId("14:05:02");
         medium_feverEvent.setBlockId("medium_fever");
         medium_feverEvent.setName("medium fever");
         medium_feverEvent.setConsultations("[Alice#2021-06-02T14:00]");
         service.apply(medium_feverEvent);

      }
   }

   private void handleDiagnosisDoneEvent(Event e)
   {
      // to protect manuel changes to this method insert a 'no' in front of fulib in the next line
      // fulib
      DiagnosisDoneEvent event = (DiagnosisDoneEvent) e;
      handleDemoDiagnosisDoneEvent(event);
   }

   private void handleDemoDiagnosisDoneEvent(DiagnosisDoneEvent event)
   {
      if (event.getId().equals("14:06")) {
         ConsultationBuilt alice_2021_06_02T14_00Event = new ConsultationBuilt();
         alice_2021_06_02T14_00Event.setId("14:06:01");
         alice_2021_06_02T14_00Event.setBlockId("Alice_2021_06_02T14_00");
         alice_2021_06_02T14_00Event.setCid("Alice#2021-06-02T14:00");
         alice_2021_06_02T14_00Event.setDiagnosis("common cold");
         service.apply(alice_2021_06_02T14_00Event);

      }
   }

   private void handleTreatmentInitiatedEvent(Event e)
   {
      // to protect manuel changes to this method insert a 'no' in front of fulib in the next line
      // fulib
      TreatmentInitiatedEvent event = (TreatmentInitiatedEvent) e;
      handleDemoTreatmentInitiatedEvent(event);
   }

   private void handleDemoTreatmentInitiatedEvent(TreatmentInitiatedEvent event)
   {
      if (event.getId().equals("14:07")) {
         ConsultationBuilt alice_2021_06_02T14_00Event = new ConsultationBuilt();
         alice_2021_06_02T14_00Event.setId("14:07:01");
         alice_2021_06_02T14_00Event.setBlockId("Alice_2021_06_02T14_00");
         alice_2021_06_02T14_00Event.setCid("Alice#2021-06-02T14:00");
         alice_2021_06_02T14_00Event.setTreatment("ibuprofen 400 1-1-1");
         service.apply(alice_2021_06_02T14_00Event);

      }
   }
}
