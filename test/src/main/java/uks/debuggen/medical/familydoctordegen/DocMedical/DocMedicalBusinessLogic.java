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
      if (event.getId().equals("14:00")) {
         PatientBuilt aliceEvent = new PatientBuilt();
         aliceEvent.setId("12:00:01");
         aliceEvent.setBlockId("alice");
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
         handlerMap.put(ConsultationEvent.class, this::handleConsultationEvent);
         handlerMap.put(SymptomEvent.class, this::handleSymptomEvent);
         handlerMap.put(TestEvent.class, this::handleTestEvent);
         handlerMap.put(DiagnosisEvent.class, this::handleDiagnosisEvent);
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

   private void handleConsultationEvent(Event e)
   {
      // to protect manuel changes to this method insert a 'no' in front of fulib in the next line
      // fulib
      ConsultationEvent event = (ConsultationEvent) e;
      handleDemoConsultationEvent(event);
   }

   private void handleDemoConsultationEvent(ConsultationEvent event)
   {
      if (event.getId().equals("12:00:02")) {
         ConsultationBuilt alice20210602T1400Event = new ConsultationBuilt();
         alice20210602T1400Event.setId("12:00:03");
         alice20210602T1400Event.setBlockId("alice#2021-06-02T14:00");
         alice20210602T1400Event.setCid("Alice#2021-06-02T14:00");
         alice20210602T1400Event.setPatient("Alice");
         service.apply(alice20210602T1400Event);

      }
   }

   private void handleSymptomEvent(Event e)
   {
      // to protect manuel changes to this method insert a 'no' in front of fulib in the next line
      // fulib
      SymptomEvent event = (SymptomEvent) e;
      handleDemoSymptomEvent(event);
   }

   private void handleDemoSymptomEvent(SymptomEvent event)
   {
      if (event.getId().equals("12:00:04")) {
         SymptomBuilt coughEvent = new SymptomBuilt();
         coughEvent.setId("12:00:05");
         coughEvent.setBlockId("cough");
         coughEvent.setName("cough");
         coughEvent.setConsultations("[Alice#2021-06-02T14:00]");
         service.apply(coughEvent);

      }
      if (event.getId().equals("12:00:06")) {
         SymptomBuilt runnyNoseEvent = new SymptomBuilt();
         runnyNoseEvent.setId("12:00:07");
         runnyNoseEvent.setBlockId("runnyNose");
         runnyNoseEvent.setName("runny nose");
         runnyNoseEvent.setConsultations("[Alice#2021-06-02T14:00]");
         service.apply(runnyNoseEvent);

      }
   }

   private void handleTestEvent(Event e)
   {
      // to protect manuel changes to this method insert a 'no' in front of fulib in the next line
      // fulib
      TestEvent event = (TestEvent) e;
      handleDemoTestEvent(event);
   }

   private void handleDemoTestEvent(TestEvent event)
   {
      if (event.getId().equals("12:00:08")) {
         TestBuilt alicet00120210602T1400Event = new TestBuilt();
         alicet00120210602T1400Event.setId("12:00:09");
         alicet00120210602T1400Event.setBlockId("alice#t001#2021-06-02T14:00");
         alicet00120210602T1400Event.setCid("Alice#t001#2021-06-02T14:00");
         alicet00120210602T1400Event.setKind("temperature");
         alicet00120210602T1400Event.setResult("39.8 Celsius");
         alicet00120210602T1400Event.setConsultation("Alice#2021-06-02T14:00");
         service.apply(alicet00120210602T1400Event);

         SymptomBuilt feverEvent = new SymptomBuilt();
         feverEvent.setId("12:00:10");
         feverEvent.setBlockId("fever");
         feverEvent.setName("fever");
         feverEvent.setConsultations("[Alice#2021-06-02T14:00]");
         service.apply(feverEvent);

      }
   }

   private void handleDiagnosisEvent(Event e)
   {
      // to protect manuel changes to this method insert a 'no' in front of fulib in the next line
      // fulib
      DiagnosisEvent event = (DiagnosisEvent) e;
      handleDemoDiagnosisEvent(event);
   }

   private void handleDemoDiagnosisEvent(DiagnosisEvent event)
   {
      if (event.getId().equals("12:00:11")) {
         ConsultationBuilt alice20210602T1400Event = new ConsultationBuilt();
         alice20210602T1400Event.setId("12:00:12");
         alice20210602T1400Event.setBlockId("alice#2021-06-02T14:00");
         alice20210602T1400Event.setCid("Alice#2021-06-02T14:00");
         alice20210602T1400Event.setDiagnosis("common cold");
         service.apply(alice20210602T1400Event);

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
      if (event.getId().equals("14:05:01")) {
         ConsultationBuilt alice20210602T1400Event = new ConsultationBuilt();
         alice20210602T1400Event.setId("12:00:13");
         alice20210602T1400Event.setBlockId("alice#2021-06-02T14:00");
         alice20210602T1400Event.setCid("Alice#2021-06-02T14:00");
         alice20210602T1400Event.setTreatment("ibuprofen 400 1-1-1");
         service.apply(alice20210602T1400Event);

      }
   }
}
