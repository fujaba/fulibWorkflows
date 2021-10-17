package uks.debuggen.studyright.StudyRight;
import uks.debuggen.studyright.events.Event;
import uks.debuggen.studyright.events.RoomBuilt;

import java.util.Objects;
import java.beans.PropertyChangeSupport;
import java.util.LinkedHashMap;
import java.util.function.Consumer;
import uks.debuggen.studyright.events.*;
import java.util.function.Function;

public class StudyRightBuilder
{
   public static final String PROPERTY_MODEL = "model";
   public static final String PROPERTY_BUSINESS_LOGIC = "businessLogic";
   public static final String PROPERTY_EVENT_STORE = "eventStore";
   public static final String PROPERTY_LOADER_MAP = "loaderMap";
   public static final String PROPERTY_GROUP_STORE = "groupStore";
   public static final String PROPERTY_SERVICE = "service";
   private StudyRightModel model;
   protected PropertyChangeSupport listeners;
   private StudyRightBusinessLogic businessLogic;
   private LinkedHashMap<String, DataEvent> eventStore = new LinkedHashMap<>();
   private LinkedHashMap<Class, Function<Event, Object>> loaderMap;
   private LinkedHashMap<String, LinkedHashMap<String, DataEvent>> groupStore = new LinkedHashMap<>();
   private StudyRightService service;

   public StudyRightModel getModel()
   {
      return this.model;
   }

   public StudyRightBuilder setModel(StudyRightModel value)
   {
      if (Objects.equals(value, this.model))
      {
         return this;
      }

      final StudyRightModel oldValue = this.model;
      this.model = value;
      this.firePropertyChange(PROPERTY_MODEL, oldValue, value);
      return this;
   }

   public StudyRightBusinessLogic getBusinessLogic()
   {
      return this.businessLogic;
   }

   public StudyRightBuilder setBusinessLogic(StudyRightBusinessLogic value)
   {
      if (this.businessLogic == value)
      {
         return this;
      }

      final StudyRightBusinessLogic oldValue = this.businessLogic;
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

   public LinkedHashMap<String, DataEvent> getEventStore()
   {
      return this.eventStore;
   }

   public StudyRightBuilder setEventStore(LinkedHashMap<String, DataEvent> value)
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

   public StudyRightBuilder setLoaderMap(LinkedHashMap<Class, Function<Event, Object>> value)
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

   public StudyRightBuilder setGroupStore(LinkedHashMap<String, LinkedHashMap<String, DataEvent>> value)
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

   public StudyRightService getService()
   {
      return this.service;
   }

   public StudyRightBuilder setService(StudyRightService value)
   {
      if (this.service == value)
      {
         return this;
      }

      final StudyRightService oldValue = this.service;
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

   public void storeUniversityBuilt(Event e)
   {
      UniversityBuilt event = (UniversityBuilt) e;
      if (outdated(event)) {
         return;
      }
      // please insert a no before fulib in the next line and insert addToGroup commands as necessary
      // fulib
   }

   public University loadUniversityBuilt(Event e)
   {
      UniversityBuilt event = (UniversityBuilt) e;
      University object = model.getOrCreateUniversity(event.getBlockId());
      for (String name : stripBrackets(event.getRooms()).split(",\\s+")) {
         if (name.equals("")) continue;
         object.withRooms(model.getOrCreateRoom(name));
      }
      return object;
   }

   public void storeRoomBuilt(Event e)
   {
      RoomBuilt event = (RoomBuilt) e;
      if (outdated(event)) {
         return;
      }
      // please insert a no before fulib in the next line and insert addToGroup commands as necessary
      // fulib
   }

   public Room loadRoomBuilt(Event e)
   {
      RoomBuilt event = (RoomBuilt) e;
      Room object = model.getOrCreateRoom(event.getBlockId());
      object.setCredits(event.getCredits() == null ? 0 : Integer.parseInt(event.getCredits()));
      object.setUni(model.getOrCreateUniversity(event.getUni()));
      for (String name : stripBrackets(event.getDoors()).split(",\\s+")) {
         if (name.equals("")) continue;
         object.withDoors(model.getOrCreateRoom(name));
      }
      return object;
   }

   public void storeStudentBuilt(Event e)
   {
      StudentBuilt event = (StudentBuilt) e;
      if (outdated(event)) {
         return;
      }
      // please insert a no before fulib in the next line and insert addToGroup commands as necessary
      // fulib
   }

   public Student loadStudentBuilt(Event e)
   {
      StudentBuilt event = (StudentBuilt) e;
      Student object = model.getOrCreateStudent(event.getBlockId());
      object.setName(event.getName());
      object.setBirthYear(event.getBirthYear() == null ? 0 : Integer.parseInt(event.getBirthYear()));
      object.setStudentId(event.getStudentId());
      object.setUni(model.getOrCreateUniversity(event.getUni()));
      return object;
   }

   public void storeTourListBuilt(Event e)
   {
      TourListBuilt event = (TourListBuilt) e;
      if (outdated(event)) {
         return;
      }
      // please insert a no before fulib in the next line and insert addToGroup commands as necessary
      // fulib
   }

   public TourList loadTourListBuilt(Event e)
   {
      TourListBuilt event = (TourListBuilt) e;
      TourList object = model.getOrCreateTourList(event.getBlockId());
      return object;
   }

   public void storeStopBuilt(Event e)
   {
      StopBuilt event = (StopBuilt) e;
      if (outdated(event)) {
         return;
      }
      // please insert a no before fulib in the next line and insert addToGroup commands as necessary
      // fulib
   }

   public Stop loadStopBuilt(Event e)
   {
      StopBuilt event = (StopBuilt) e;
      Stop object = model.getOrCreateStop(event.getBlockId());
      object.setMotivation(event.getMotivation() == null ? 0 : Integer.parseInt(event.getMotivation()));
      object.setRoom(event.getRoom());
      object.setPreviousStop(model.getOrCreateStop(event.getPreviousStop()));
      return object;
   }

   public void storeTourBuilt(Event e)
   {
      TourBuilt event = (TourBuilt) e;
      if (outdated(event)) {
         return;
      }
      // please insert a no before fulib in the next line and insert addToGroup commands as necessary
      // fulib
   }

   public Tour loadTourBuilt(Event e)
   {
      TourBuilt event = (TourBuilt) e;
      Tour object = model.getOrCreateTour(event.getBlockId());
      object.setTourList(model.getOrCreateTourList(event.getTourList()));
      object.setStops(event.getStops());
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
         loaderMap.put(UniversityBuilt.class, this::loadUniversityBuilt);
         loaderMap.put(RoomBuilt.class, this::loadRoomBuilt);
         loaderMap.put(StudentBuilt.class, this::loadStudentBuilt);
         loaderMap.put(TourListBuilt.class, this::loadTourListBuilt);
         loaderMap.put(StopBuilt.class, this::loadStopBuilt);
         loaderMap.put(TourBuilt.class, this::loadTourBuilt);
      }
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

   public String getObjectId(String value)
   {
      if (value == null) {
         return null;
      }
      return value.replaceAll("\\W+", "_");
   }
}
