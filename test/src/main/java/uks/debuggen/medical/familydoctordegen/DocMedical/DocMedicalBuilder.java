package uks.debuggen.medical.familydoctordegen.DocMedical;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

import org.fulib.yaml.Yaml;
import org.fulib.yaml.YamlIdMap;
import org.fulib.yaml.Yamler;
import org.fulib.yaml.Yamler2;
import uks.debuggen.medical.familydoctordegen.events.*;
import java.util.Objects;
import java.beans.PropertyChangeSupport;
import java.util.function.Consumer;

public class DocMedicalBuilder
{
   public static final String PROPERTY_MODEL = "model";
   public static final String PROPERTY_EVENT_STORE = "eventStore";
   public static final String PROPERTY_LOADER_MAP = "loaderMap";
   public static final String PROPERTY_GROUP_STORE = "groupStore";
   public static final String PROPERTY_BUSINESS_LOGIC = "businessLogic";
   public static final String PROPERTY_SERVICE = "service";
   private DocMedicalModel model;
   private LinkedHashMap<String, DataEvent> eventStore = new LinkedHashMap<>();
   private LinkedHashMap<Class, Function<Event, Object>> loaderMap;
   private LinkedHashMap<String, LinkedHashMap<String, DataEvent>> groupStore = new LinkedHashMap<>();
   private DocMedicalBusinessLogic businessLogic;
   private DocMedicalService service;
   protected PropertyChangeSupport listeners;

   public DocMedicalModel getModel()
   {
      return this.model;
   }

   public DocMedicalBuilder setModel(DocMedicalModel value)
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

   public LinkedHashMap<String, DataEvent> getEventStore()
   {
      return this.eventStore;
   }

   public DocMedicalBuilder setEventStore(LinkedHashMap<String, DataEvent> value)
   {
      if (Objects.equals(value, this.eventStore))
      {
         return this;
      }

      final LinkedHashMap<String, DataEvent> oldValue = this.eventStore;
      this.eventStore = value;
      this.firePropertyChange(PROPERTY_EVENT_STORE, oldValue, value);
      return this;
   }

   public LinkedHashMap<Class, Function<Event, Object>> getLoaderMap()
   {
      return this.loaderMap;
   }

   public DocMedicalBuilder setLoaderMap(LinkedHashMap<Class, Function<Event, Object>> value)
   {
      if (Objects.equals(value, this.loaderMap))
      {
         return this;
      }

      final LinkedHashMap<Class, Function<Event, Object>> oldValue = this.loaderMap;
      this.loaderMap = value;
      this.firePropertyChange(PROPERTY_LOADER_MAP, oldValue, value);
      return this;
   }

   public LinkedHashMap<String, LinkedHashMap<String, DataEvent>> getGroupStore()
   {
      return this.groupStore;
   }

   public DocMedicalBuilder setGroupStore(LinkedHashMap<String, LinkedHashMap<String, DataEvent>> value)
   {
      if (Objects.equals(value, this.groupStore))
      {
         return this;
      }

      final LinkedHashMap<String, LinkedHashMap<String, DataEvent>> oldValue = this.groupStore;
      this.groupStore = value;
      this.firePropertyChange(PROPERTY_GROUP_STORE, oldValue, value);
      return this;
   }

   public DocMedicalBusinessLogic getBusinessLogic()
   {
      return this.businessLogic;
   }

   public DocMedicalBuilder setBusinessLogic(DocMedicalBusinessLogic value)
   {
      if (this.businessLogic == value)
      {
         return this;
      }

      final DocMedicalBusinessLogic oldValue = this.businessLogic;
      if (this.businessLogic != null)
      {
         this.businessLogic = null;
         oldValue.setBuilder(null);
      }
      this.businessLogic = value;
      if (value != null)
      {
         value.setBuilder(this);
      }
      this.firePropertyChange(PROPERTY_BUSINESS_LOGIC, oldValue, value);
      return this;
   }

   public DocMedicalService getService()
   {
      return this.service;
   }

   public DocMedicalBuilder setService(DocMedicalService value)
   {
      if (this.service == value)
      {
         return this;
      }

      final DocMedicalService oldValue = this.service;
      if (this.service != null)
      {
         this.service = null;
         oldValue.setBuilder(null);
      }
      this.service = value;
      if (value != null)
      {
         value.setBuilder(this);
      }
      this.firePropertyChange(PROPERTY_SERVICE, oldValue, value);
      return this;
   }

   private boolean outdated(DataEvent event)
   {
      DataEvent oldEvent = getEventStore().get(event.getBlockId());

      if (oldEvent == null) {
         eventStore.put(event.getBlockId(), event);
         return false;
      }

      if (oldEvent.getId().compareTo(event.getId()) < 0) {
         new org.fulib.yaml.Yamler2().mergeObjects(oldEvent, event);
         eventStore.put(event.getBlockId(), event);
         return false;
      }

      return true;
   }

   public void storeDiseaseBuilt(Event e)
   {
      DiseaseBuilt event = (DiseaseBuilt) e;
      if (outdated(event)) {
         return;
      }
      // please insert a no before fulib in the next line and insert addToGroup commands as necessary
      // fulib
   }

   public Disease loadDiseaseBuilt(Event e)
   {
      DiseaseBuilt event = (DiseaseBuilt) e;
      Disease object = model.getOrCreateDisease(event.getBlockId());
      object.setName(event.getName());
      for (String name : stripBrackets(event.getSymptoms()).split(",\\s+")) {
         if (name.equals("")) continue;
         object.withSymptoms(model.getOrCreateSymptom(name));
      }
      for (String name : stripBrackets(event.getCounterSymptoms()).split(",\\s+")) {
         if (name.equals("")) continue;
         object.withCounterSymptoms(model.getOrCreateSymptom(name));
      }
      object.setMigratedTo(event.getMigratedTo());
      return object;
   }

   public void storeSymptomBuilt(Event e)
   {
      SymptomBuilt event = (SymptomBuilt) e;
      if (outdated(event)) {
         return;
      }
      // please insert a no before fulib in the next line and insert addToGroup commands as necessary
      // fulib
   }

   public Symptom loadSymptomBuilt(Event e)
   {
      SymptomBuilt event = (SymptomBuilt) e;
      Symptom object = model.getOrCreateSymptom(event.getBlockId());
      object.setName(event.getName());
      for (String name : stripBrackets(event.getConsultations()).split(",\\s+")) {
         if (name.equals("")) continue;
         object.withConsultations(model.getOrCreateConsultation(name));
      }
      return object;
   }

   public void storePatientBuilt(Event e)
   {
      PatientBuilt event = (PatientBuilt) e;
      if (outdated(event)) {
         return;
      }
      // please insert a no before fulib in the next line and insert addToGroup commands as necessary
      // fulib
   }

   public Patient loadPatientBuilt(Event e)
   {
      PatientBuilt event = (PatientBuilt) e;
      Patient object = model.getOrCreatePatient(event.getBlockId());
      object.setName(event.getName());
      object.setAddress(event.getAddress());
      object.setBirthDate(event.getBirthDate());
      return object;
   }

   public void storeConsultationBuilt(Event e)
   {
      ConsultationBuilt event = (ConsultationBuilt) e;
      if (outdated(event)) {
         return;
      }
      // please insert a no before fulib in the next line and insert addToGroup commands as necessary
      // fulib
   }

   public Consultation loadConsultationBuilt(Event e)
   {
      ConsultationBuilt event = (ConsultationBuilt) e;
      Consultation object = model.getOrCreateConsultation(event.getBlockId());
      object.setCid(event.getCid());
      object.setPatient(model.getOrCreatePatient(event.getPatient()));
      object.setDiagnosis(model.getOrCreateDisease(event.getDiagnosis()));
      object.setTreatment(event.getTreatment());
      return object;
   }

   public void storeTestBuilt(Event e)
   {
      TestBuilt event = (TestBuilt) e;
      if (outdated(event)) {
         return;
      }
      // please insert a no before fulib in the next line and insert addToGroup commands as necessary
      // fulib
   }

   public Test loadTestBuilt(Event e)
   {
      TestBuilt event = (TestBuilt) e;
      Test object = model.getOrCreateTest(event.getBlockId());
      object.setCid(event.getCid());
      object.setKind(event.getKind());
      object.setResult(event.getResult());
      object.setConsultation(model.getOrCreateConsultation(event.getConsultation()));
      return object;
   }

   public Object load(String blockId)
   {
      DataEvent dataEvent = eventStore.get(blockId);
      if (dataEvent == null) {
         return null;
      }

      initLoaderMap();
      Function<Event, Object> loader = loaderMap.get(dataEvent.getClass());
      Object object = loader.apply(dataEvent);

      LinkedHashMap<String, DataEvent> group = groupStore.computeIfAbsent(blockId, k -> new LinkedHashMap<>());
      for (DataEvent element : group.values()) {
         loader = loaderMap.get(element.getClass());
         loader.apply(element);
      }

      return object;
   }

   private void initLoaderMap()
   {
      if (loaderMap == null) {
         loaderMap = new LinkedHashMap<>();
         loaderMap.put(DiseaseBuilt.class, this::loadDiseaseBuilt);
         loaderMap.put(SymptomBuilt.class, this::loadSymptomBuilt);
         loaderMap.put(PatientBuilt.class, this::loadPatientBuilt);
         loaderMap.put(ConsultationBuilt.class, this::loadConsultationBuilt);
         loaderMap.put(TestBuilt.class, this::loadTestBuilt);
      }
   }

   public String getObjectId(String value)
   {
      if (value == null) {
         return null;
      }
      return value.replaceAll("\\W+", "_");
   }

   private void addToGroup(String groupId, String elementId)
   {
      DataEvent dataEvent = eventStore.get(elementId);

      if (dataEvent == null) {
         java.util.logging.Logger.getGlobal().severe(String.format("could not find element event %s for group %s ", elementId, groupId));
         return;
      }

      LinkedHashMap<String, DataEvent> group = groupStore.computeIfAbsent(groupId, k -> new LinkedHashMap<>());
      group.put(elementId, dataEvent);
   }

   public String stripBrackets(String back)
   {
      if (back == null) {
         return "";
      }
      int open = back.indexOf('[');
      int close = back.indexOf(']');
      if (open >= 0 && close >= 0) {
         back = back.substring(open + 1, close);
      }
      return back;
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
      this.setBusinessLogic(null);
      this.setService(null);
   }
}
