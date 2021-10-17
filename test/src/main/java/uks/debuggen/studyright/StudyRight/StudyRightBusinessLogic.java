package uks.debuggen.studyright.StudyRight;
import java.util.Objects;
import java.beans.PropertyChangeSupport;
import java.util.LinkedHashMap;
import java.util.function.Consumer;
import uks.debuggen.studyright.events.*;

public class StudyRightBusinessLogic
{
   public static final String PROPERTY_MODEL = "model";
   public static final String PROPERTY_SERVICE = "service";
   public static final String PROPERTY_HANDLER_MAP = "handlerMap";
   public static final String PROPERTY_BUILDER = "builder";
   private StudyRightModel model;
   protected PropertyChangeSupport listeners;
   private StudyRightService service;
   private LinkedHashMap<Class, Consumer<Event>> handlerMap;
   private StudyRightBuilder builder;

   public StudyRightModel getModel()
   {
      return this.model;
   }

   public StudyRightBusinessLogic setModel(StudyRightModel value)
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

   public StudyRightService getService()
   {
      return this.service;
   }

   public StudyRightBusinessLogic setService(StudyRightService value)
   {
      if (this.service == value)
      {
         return this;
      }

      final StudyRightService oldValue = this.service;
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

   public LinkedHashMap<Class, Consumer<Event>> getHandlerMap()
   {
      return this.handlerMap;
   }

   public StudyRightBusinessLogic setHandlerMap(LinkedHashMap<Class, Consumer<Event>> value)
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

   public StudyRightBuilder getBuilder()
   {
      return this.builder;
   }

   public StudyRightBusinessLogic setBuilder(StudyRightBuilder value)
   {
      if (this.builder == value)
      {
         return this;
      }

      final StudyRightBuilder oldValue = this.builder;
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

   private void handleDemoLoadRoomsCommand(LoadRoomsCommand event)
   {
      if (event.getId().equals("12:00")) {
         UniversityBuilt studyRightEvent = new UniversityBuilt();
         studyRightEvent.setId("12:00:00");
         studyRightEvent.setBlockId("StudyRight");
         studyRightEvent.setRooms("[math, exam]");
         service.apply(studyRightEvent);

         RoomBuilt mathEvent = new RoomBuilt();
         mathEvent.setId("12:00:01");
         mathEvent.setBlockId("math");
         mathEvent.setCredits("23");
         mathEvent.setUni("StudyRight");
         mathEvent.setDoors("[modeling, algebra]");
         service.apply(mathEvent);

         RoomBuilt modelingEvent = new RoomBuilt();
         modelingEvent.setId("12:00:02");
         modelingEvent.setBlockId("modeling");
         modelingEvent.setUni("StudyRight");
         modelingEvent.setCredits("42");
         modelingEvent.setDoors("[math, algebra, exam]");
         service.apply(modelingEvent);

         RoomBuilt algebraEvent = new RoomBuilt();
         algebraEvent.setId("12:00:03");
         algebraEvent.setBlockId("algebra");
         algebraEvent.setUni("StudyRight");
         algebraEvent.setCredits("12");
         service.apply(algebraEvent);

         RoomBuilt examEvent = new RoomBuilt();
         examEvent.setId("12:00:04");
         examEvent.setBlockId("exam");
         examEvent.setCredits("0");
         examEvent.setUni("StudyRight");
         service.apply(examEvent);

         StudentBuilt carliEvent = new StudentBuilt();
         carliEvent.setId("12:00:05");
         carliEvent.setBlockId("carli");
         carliEvent.setName("Carli");
         carliEvent.setBirthYear("1970");
         carliEvent.setStudentId("stud42");
         carliEvent.setUni("StudyRight");
         service.apply(carliEvent);


         RoomsLoadedEvent e120006 = new RoomsLoadedEvent();

         e120006.setId("12:00:06");
         service.apply(e120006);
      }
   }

   private void ignoreEvent(Event event)
   {
      // empty
   }

   public String idPrefix = "e";
   public int idNumber = 0;

   public String newEventId()
   {
      idNumber++;
      return idPrefix + idNumber;
   }

   private void loadSmallMap()
   {
      UniversityBuilt studyRightEvent = new UniversityBuilt();
      studyRightEvent.setId(newEventId());
      studyRightEvent.setBlockId("StudyRight");
      studyRightEvent.setRooms("[math, exam]");
      service.apply(studyRightEvent);

      RoomBuilt mathEvent = new RoomBuilt();
      mathEvent.setId(newEventId());
      mathEvent.setBlockId("math");
      mathEvent.setCredits("23");
      mathEvent.setUni("StudyRight");
      mathEvent.setDoors("[modeling, algebra]");
      service.apply(mathEvent);

      RoomBuilt modelingEvent = new RoomBuilt();
      modelingEvent.setId(newEventId());
      modelingEvent.setBlockId("modeling");
      modelingEvent.setUni("StudyRight");
      modelingEvent.setCredits("42");
      modelingEvent.setDoors("[math, algebra, exam]");
      service.apply(modelingEvent);

      RoomBuilt algebraEvent = new RoomBuilt();
      algebraEvent.setId(newEventId());
      algebraEvent.setBlockId("algebra");
      algebraEvent.setUni("StudyRight");
      algebraEvent.setCredits("12");
      service.apply(algebraEvent);

      RoomBuilt examEvent = new RoomBuilt();
      examEvent.setId(newEventId());
      examEvent.setBlockId("exam");
      examEvent.setDoors("[modeling, algebra]");
      examEvent.setCredits("0");
      examEvent.setUni("StudyRight");
      service.apply(examEvent);
   }

   private void handleFindToursCommand(Event e)
   {
      // no fulib
      FindToursCommand event = (FindToursCommand) e;

      if (event.getId().startsWith(idPrefix)) {
         TourListBuilt allToursEvent = new TourListBuilt();
         allToursEvent.setId(newEventId());
         allToursEvent.setBlockId(idPrefix + "TourList");
         service.apply(allToursEvent);

         StopBuilt s01Event = new StopBuilt();
         s01Event.setId(newEventId());
         String firstStop = "stop" + idNumber;
         s01Event.setBlockId(firstStop);
         s01Event.setMotivation("77");
         service.apply(s01Event);


         VisitRoomCommand roomSelected = new VisitRoomCommand();
         roomSelected.setId(newEventId());
         roomSelected.setRoom("math");
         roomSelected.setPreviousStop(firstStop);
         service.apply(roomSelected);
         return;
      }

      handleDemoFindToursCommand(event);
   }

   private void handleDemoFindToursCommand(FindToursCommand event)
   {
      if (event.getId().equals("12:01")) {
         TourListBuilt allToursEvent = new TourListBuilt();
         allToursEvent.setId("12:01:00");
         allToursEvent.setBlockId("allTours");
         service.apply(allToursEvent);

         StopBuilt s01Event = new StopBuilt();
         s01Event.setId("12:01:01");
         s01Event.setBlockId("s01");
         s01Event.setMotivation("77");
         service.apply(s01Event);

      }
   }

   private void handleVisitRoomCommand(Event e)
   {
      // no fulib
      VisitRoomCommand event = (VisitRoomCommand) e;

      if (event.getId().startsWith(idPrefix)) {
         // always create a stop
         Room room = (Room) builder.load(event.getRoom());
         Stop previousStop = (Stop) builder.load(event.getPreviousStop());
         int motivation = previousStop.getMotivation();
         int credits = room.getCredits();
         int newMotivation = motivation - credits;

         StopBuilt newStopEvent = new StopBuilt();
         newStopEvent.setId(newEventId());
         String newStopId = "stop" + idNumber;
         newStopEvent.setBlockId(newStopId);
         newStopEvent.setRoom(event.getRoom());
         newStopEvent.setMotivation("" + newMotivation);
         newStopEvent.setPreviousStop(event.getPreviousStop());
         service.apply(newStopEvent);

         // on negative motivation, stop searching
         if (newMotivation < 0) {
            TourFailedEvent tourFailed = new TourFailedEvent();
            tourFailed.setId(newEventId());
            tourFailed.setStop(newStopId);
            tourFailed.setRoom(room.getId());
            tourFailed.setCredits("" + newMotivation);
            service.apply(tourFailed);
            return;
         }

         if (newMotivation > 0 && event.getRoom().equals("exam")) {
            TourFailedEvent tourFailed = new TourFailedEvent();
            tourFailed.setId(newEventId());
            tourFailed.setStop(newStopId);
            tourFailed.setRoom(room.getId());
            tourFailed.setCredits("" + newMotivation);
            service.apply(tourFailed);
            return;
         }

         if (newMotivation == 0 && event.getRoom().equals("exam")) {
            TourList tourList = model.getOrCreateTourList(idPrefix + "TourList");
            String tourId = "tour" + tourList.getAlternatives().size();
            String stopList = "";

            CollectTourStopsCommand collectTourStops = new CollectTourStopsCommand();
            collectTourStops.setId(newEventId());
            collectTourStops.setStop(newStopId);
            collectTourStops.setTour(tourId);
            service.apply(collectTourStops);

            return;
         }

         // continue with neighbors
         for (Room neighbor : room.getDoors()) {
            VisitRoomCommand roomSelected = new VisitRoomCommand();
            roomSelected.setId(newEventId());
            roomSelected.setRoom(neighbor.getId());
            roomSelected.setPreviousStop(newStopId);
            service.apply(roomSelected);
         }
         return;
      }

      handleDemoVisitRoomCommand(event);
   }

   private void handleCollectTourStopsCommand(Event e)
   {
      // no fulib
      CollectTourStopsCommand event = (CollectTourStopsCommand) e;

      if  (event.getId().startsWith(idPrefix)) {
         Stop currentStop = model.getOrCreateStop(event.getStop());
         String stopList = "";
         while (currentStop.getPreviousStop() != null) {
            stopList = currentStop.getRoom() + " " + stopList;
            TourBuilt tour1Event = new TourBuilt();
            tour1Event.setId(newEventId());
            tour1Event.setBlockId(event.getTour());
            tour1Event.setStops(stopList);
            tour1Event.setTourList(idPrefix + "TourList");
            service.apply(tour1Event);

            currentStop = currentStop.getPreviousStop();
         }
         return;
      }

      handleDemoCollectTourStopsCommand(event);
   }

   private void handleLoadRoomsCommand(Event e)
   {
      // no fulib
      LoadRoomsCommand event = (LoadRoomsCommand) e;

      if (event.getId().equals("smallMap")) {
         loadSmallMap();
         return;
      }

      handleDemoLoadRoomsCommand(event);
   }

   private void handleDemoVisitRoomCommand(VisitRoomCommand event)
   {
      if (event.getId().equals("12:02")) {
         StopBuilt s02Event = new StopBuilt();
         s02Event.setId("12:02:01");
         s02Event.setBlockId("s02");
         s02Event.setRoom("math");
         s02Event.setMotivation("54");
         s02Event.setPreviousStop("s01");
         service.apply(s02Event);

      }
      if (event.getId().equals("12:03")) {
         StopBuilt s03Event = new StopBuilt();
         s03Event.setId("12:03:01");
         s03Event.setBlockId("s03");
         s03Event.setRoom("algebra");
         s03Event.setPreviousStop("s02");
         s03Event.setMotivation("42");
         service.apply(s03Event);

      }
      if (event.getId().equals("12:05")) {
         StopBuilt s05Event = new StopBuilt();
         s05Event.setId("12:05:01");
         s05Event.setBlockId("s05");
         s05Event.setRoom("modeling");
         s05Event.setPreviousStop("s03");
         s05Event.setMotivation("0");
         service.apply(s05Event);

      }
      if (event.getId().equals("12:07")) {
         StopBuilt s07Event = new StopBuilt();
         s07Event.setId("12:07:01");
         s07Event.setBlockId("s07");
         s07Event.setRoom("math");
         s07Event.setPreviousStop("s05");
         s07Event.setMotivation("-23");
         service.apply(s07Event);


         TourFailedEvent e120702 = new TourFailedEvent();

         e120702.setId("12:07:02");
         e120702.setStop("s07");
         e120702.setRoom("math");
         e120702.setCredits("-23");
         service.apply(e120702);
      }
      if (event.getId().equals("12:08")) {
         StopBuilt s08Event = new StopBuilt();
         s08Event.setId("12:08:01");
         s08Event.setBlockId("s08");
         s08Event.setRoom("exam");
         s08Event.setPreviousStop("s05");
         s08Event.setMotivation("0");
         service.apply(s08Event);

      }
   }

   private void handleDemoCollectTourStopsCommand(CollectTourStopsCommand event)
   {
      if (event.getId().equals("12:09")) {
         TourBuilt tour1Event = new TourBuilt();
         tour1Event.setId("12:09:02");
         tour1Event.setBlockId("tour1");
         tour1Event.setTourList("allTours");
         tour1Event.setStops("exam");
         service.apply(tour1Event);

      }
      if (event.getId().equals("12:10")) {
         TourBuilt tour1Event = new TourBuilt();
         tour1Event.setId("12:10:01");
         tour1Event.setBlockId("tour1");
         tour1Event.setStops("modeling exam");
         service.apply(tour1Event);

      }
      if (event.getId().equals("12:11")) {
         TourBuilt tour1Event = new TourBuilt();
         tour1Event.setId("12:11:01");
         tour1Event.setBlockId("tour1");
         tour1Event.setStops("algebra modeling exam");
         service.apply(tour1Event);

      }
      if (event.getId().equals("12:12")) {
         TourBuilt tour1Event = new TourBuilt();
         tour1Event.setId("12:12:01");
         tour1Event.setBlockId("tour1");
         tour1Event.setStops("math algebra modeling exam");
         service.apply(tour1Event);


         TourFoundEvent e1213 = new TourFoundEvent();

         e1213.setId("12:13");
         e1213.setTour("tour1");
         service.apply(e1213);
      }
   }

   public Consumer<Event> getHandler(Event event)
   {
      return getHandlerMap().computeIfAbsent(event.getClass(), k -> this::ignoreEvent);
   }

   public void initEventHandlerMap()
   {
      if (handlerMap == null) {
         handlerMap = new LinkedHashMap<>();
         handlerMap.put(UniversityBuilt.class, builder::storeUniversityBuilt);
         handlerMap.put(RoomBuilt.class, builder::storeRoomBuilt);
         handlerMap.put(StudentBuilt.class, builder::storeStudentBuilt);
         handlerMap.put(TourListBuilt.class, builder::storeTourListBuilt);
         handlerMap.put(StopBuilt.class, builder::storeStopBuilt);
         handlerMap.put(TourBuilt.class, builder::storeTourBuilt);
         handlerMap.put(LoadRoomsCommand.class, this::handleLoadRoomsCommand);
         handlerMap.put(FindToursCommand.class, this::handleFindToursCommand);
         handlerMap.put(VisitRoomCommand.class, this::handleVisitRoomCommand);
         handlerMap.put(CollectTourStopsCommand.class, this::handleCollectTourStopsCommand);
      }
   }
}
