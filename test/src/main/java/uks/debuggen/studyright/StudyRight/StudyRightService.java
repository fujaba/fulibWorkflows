package uks.debuggen.studyright.StudyRight;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.fulib.yaml.Yaml;
import spark.Request;
import spark.Response;
import spark.Service;
import uks.debuggen.studyright.events.*;

import java.util.Objects;
import java.beans.PropertyChangeSupport;

public class StudyRightService
{
   public static final String PROPERTY_HISTORY = "history";
   public static final String PROPERTY_PORT = "port";
   public static final String PROPERTY_SPARK = "spark";
   public static final String PROPERTY_MODEL = "model";
   public static final String PROPERTY_HANDLER_MAP = "handlerMap";
   private LinkedHashMap<String, Event> history = new LinkedHashMap<>();
   private int port = 42400;
   private Service spark;
   private StudyRightModel model;
   private LinkedHashMap<Class, Consumer<Event>> handlerMap;
   protected PropertyChangeSupport listeners;

   public LinkedHashMap<String, Event> getHistory()
   {
      return this.history;
   }

   public StudyRightService setHistory(LinkedHashMap<String, Event> value)
   {
      if (Objects.equals(value, this.history))
      {
         return this;
      }

      final LinkedHashMap<String, Event> oldValue = this.history;
      this.history = value;
      this.firePropertyChange(PROPERTY_HISTORY, oldValue, value);
      return this;
   }

   public int getPort()
   {
      return this.port;
   }

   public StudyRightService setPort(int value)
   {
      if (value == this.port)
      {
         return this;
      }

      final int oldValue = this.port;
      this.port = value;
      this.firePropertyChange(PROPERTY_PORT, oldValue, value);
      return this;
   }

   public Service getSpark()
   {
      return this.spark;
   }

   public StudyRightService setSpark(Service value)
   {
      if (Objects.equals(value, this.spark))
      {
         return this;
      }

      final Service oldValue = this.spark;
      this.spark = value;
      this.firePropertyChange(PROPERTY_SPARK, oldValue, value);
      return this;
   }

   public StudyRightModel getModel()
   {
      return this.model;
   }

   public StudyRightService setModel(StudyRightModel value)
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

   public LinkedHashMap<Class, Consumer<Event>> getHandlerMap()
   {
      return this.handlerMap;
   }

   public StudyRightService setHandlerMap(LinkedHashMap<Class, Consumer<Event>> value)
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

   public void start()
   {
      model = new StudyRightModel();
      ExecutorService executor = Executors.newSingleThreadExecutor();
      spark = Service.ignite();
      spark.port(port);
      spark.get("/page/:id", (req, res) -> executor.submit(() -> this.getPage(req, res)).get());
      spark.get("/", (req, res) -> executor.submit(() -> this.getHello(req, res)).get());
      spark.post("/apply", (req, res) -> executor.submit(() -> this.postApply(req, res)).get());
      executor.submit(this::subscribeAndLoadOldEvents);
      Logger.getGlobal().info("StudyRight service is up and running on port " + port);
   }

   private String getHello(Request req, Response res)
   {
      try {
         String events = Yaml.encode(getHistory().values().toArray());
         String objects = Yaml.encode(model.getModelMap().values().toArray());
         return "<p id='StudyRight'>This is the StudyRight service. </p>\n" +
               "<pre id=\"history\">" + events + "</pre>\n" +
               "<pre id=\"data\">" + objects + "</pre>\n" +
               "";
      }
      catch (Exception e) {
         Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
         return "StudyRight Error " + e.getMessage();
      }
   }

   private void subscribeAndLoadOldEvents()
   {
      ServiceSubscribed serviceSubscribed = new ServiceSubscribed()
            .setServiceUrl("http://localhost:42400/apply");
      String json = Yaml.encode(serviceSubscribed);
      try {
         String url = "http://localhost:42000/subscribe";
         HttpResponse<String> response = Unirest
               .post(url)
               .body(json)
               .asString();
         String body = response.getBody();
         Map<String, Object> objectMap = Yaml.decode(body);
         for (Object obj : objectMap.values()) {
            apply((Event) obj);
         }
      }
      catch (UnirestException e) {
         e.printStackTrace();
      }
   }

   public void apply(Event event) // no fulib
   {
      if (history.get(event.getId()) != null) {
         return;
      }
      history.put(event.getId(), event);
      initEventHandlerMap();
      Consumer<Event> handler = handlerMap.computeIfAbsent(event.getClass(), k -> this::ignoreEvent);
      handler.accept(event);
      publish(event);
   }

   public String getPage(Request request, Response response)
   {
      // no fulib
      try {
         // add your page handling here
         String id = request.params("id");
         String event = request.queryParams("event");
         if (id.equals("welcome")) {
            return getWelcomePage();
         }
         else if (id.equals("tour")) {
            return getTourPage();
         }

         return getDemoPage(request, response);
      }
      catch (Exception e) {
         e.printStackTrace();
      }
      return "Exception raised";
   }

   private void handleUniversityBuilt(Event e)
   {
      UniversityBuilt event = (UniversityBuilt) e;
      University object = model.getOrCreateUniversity(event.getBlockId());
      for (String name : stripBrackets(event.getRooms()).split("\\s+")) {
         if (name.equals("")) continue;
         object.withRooms(model.getOrCreateRoom(name));
      }
   }

   private void handleRoomBuilt(Event e)
   {
      RoomBuilt event = (RoomBuilt) e;
      Room object = model.getOrCreateRoom(event.getBlockId());
      object.setCredits(event.getCredits());
      object.setUni(model.getOrCreateUniversity(event.getUni()));
      for (String name : stripBrackets(event.getDoors()).split("\\s+")) {
         if (name.equals("")) continue;
         object.withDoors(model.getOrCreateRoom(name));
      }
   }

   private void handleStopBuilt(Event e)
   {
      StopBuilt event = (StopBuilt) e;
      Stop object = model.getOrCreateStop(event.getBlockId());
      object.setMotivation(event.getMotivation());
      object.setRoom(event.getRoom());
      object.setPreviousStop(model.getOrCreateStop(event.getPreviousStop()));
   }

   private void handleTourBuilt(Event e)
   {
      TourBuilt event = (TourBuilt) e;
      Tour object = model.getOrCreateTour(event.getBlockId());
      object.setTourList(model.getOrCreateTourList(event.getTourList()));
      object.setStops(event.getStops());
   }

   private void handleTourListBuilt(Event e)
   {
      TourListBuilt event = (TourListBuilt) e;
      TourList object = model.getOrCreateTourList(event.getBlockId());
   }

   private void handleStudentBuilt(Event e)
   {
      StudentBuilt event = (StudentBuilt) e;
      Student object = model.getOrCreateStudent(event.getBlockId());
      object.setName(event.getName());
      object.setBirthYear(Integer.parseInt(event.getBirthYear()));
      object.setStudentId(event.getStudentId());
      object.setUni(model.getOrCreateUniversity(event.getUni()));
   }

   private void handleLoadRooms(Event e)
   {
      // no fulib
      LoadRooms event = (LoadRooms) e;

      if (event.getId().equals("smallMap")) {
         loadSmallMap();
         return;
      }

      handleDemoLoadRooms(event);
   }

   private void handleDemoLoadRooms(LoadRooms event)
   {
      if (event.getId().equals("12:00")) {
         UniversityBuilt studyRightEvent = new UniversityBuilt();
         studyRightEvent.setId("12:00:00");
         studyRightEvent.setBlockId("StudyRight");
         studyRightEvent.setRooms("[math exam]");
         apply(studyRightEvent);

         RoomBuilt mathEvent = new RoomBuilt();
         mathEvent.setId("12:00:01");
         mathEvent.setBlockId("math");
         mathEvent.setCredits("23");
         mathEvent.setUni("StudyRight");
         mathEvent.setDoors("[modeling algebra]");
         apply(mathEvent);

         RoomBuilt modelingEvent = new RoomBuilt();
         modelingEvent.setId("12:00:02");
         modelingEvent.setBlockId("modeling");
         modelingEvent.setUni("StudyRight");
         modelingEvent.setCredits("42");
         modelingEvent.setDoors("[math algebra exam]");
         apply(modelingEvent);

         RoomBuilt algebraEvent = new RoomBuilt();
         algebraEvent.setId("12:00:03");
         algebraEvent.setBlockId("algebra");
         algebraEvent.setUni("StudyRight");
         algebraEvent.setCredits("12");
         apply(algebraEvent);

         RoomBuilt examEvent = new RoomBuilt();
         examEvent.setId("12:00:04");
         examEvent.setBlockId("exam");
         examEvent.setCredits("0");
         examEvent.setUni("StudyRight");
         apply(examEvent);

         StudentBuilt carliEvent = new StudentBuilt();
         carliEvent.setId("12:00:05");
         carliEvent.setBlockId("carli");
         carliEvent.setName("Carli");
         carliEvent.setBirthYear("1970");
         carliEvent.setStudentId("stud42");
         carliEvent.setUni("StudyRight");
         apply(carliEvent);


         RoomsLoaded e120006 = new RoomsLoaded();

         e120006.setId("12:00:06");
         apply(e120006);

         FindTours e1201 = new FindTours();

         e1201.setId("12:01");
         apply(e1201);
      }
   }

   private void handleFindTours(Event e)
   {
      // no fulib
      FindTours event = (FindTours) e;

      if (event.getId().startsWith(idPrefix)) {
         TourListBuilt allToursEvent = new TourListBuilt();
         allToursEvent.setId(newEventId());
         allToursEvent.setBlockId(idPrefix + "TourList");
         apply(allToursEvent);

         StopBuilt s01Event = new StopBuilt();
         s01Event.setId(newEventId());
         String firstStop = "stop" + idNumber;
         s01Event.setBlockId(firstStop);
         s01Event.setMotivation("77");
         apply(s01Event);


         VisitRoom roomSelected = new VisitRoom();
         roomSelected.setId(newEventId());
         roomSelected.setRoom("math");
         roomSelected.setPreviousStop(firstStop);
         apply(roomSelected);
         return;
      }

      handleDemoFindTours(event);
   }

   private void handleDemoFindTours(FindTours event)
   {
      if (event.getId().equals("12:01")) {
         TourListBuilt allToursEvent = new TourListBuilt();
         allToursEvent.setId("12:01:00");
         allToursEvent.setBlockId("allTours");
         apply(allToursEvent);

         StopBuilt s01Event = new StopBuilt();
         s01Event.setId("12:01:01");
         s01Event.setBlockId("s01");
         s01Event.setMotivation("77");
         apply(s01Event);


         VisitRoom e1202 = new VisitRoom();

         e1202.setId("12:02");
         e1202.setRoom("math");
         e1202.setPreviousStop("s01");
         apply(e1202);
      }
   }

   private void handleVisitRoom(Event e)
   {
      // no fulib
      VisitRoom event = (VisitRoom) e;

      if (event.getId().startsWith(idPrefix)) {
         // always create a stop
         Room room = model.getOrCreateRoom(event.getRoom());
         Stop previousStop = model.getOrCreateStop(event.getPreviousStop());
         String motivation = previousStop.getMotivation();
         String credits = room.getCredits();
         int newMotivation = Integer.parseInt(motivation) - Integer.parseInt(credits);

         StopBuilt newStopEvent = new StopBuilt();
         newStopEvent.setId(newEventId());
         String newStopId = "stop" + idNumber;
         newStopEvent.setBlockId(newStopId);
         newStopEvent.setRoom(event.getRoom());
         newStopEvent.setMotivation("" + newMotivation);
         newStopEvent.setPreviousStop(event.getPreviousStop());
         apply(newStopEvent);

         // on negative motivation, stop searching
         if (newMotivation < 0) {
            TourFailed tourFailed = new TourFailed();
            tourFailed.setId(newEventId());
            tourFailed.setStop(newStopId);
            tourFailed.setRoom(room.getId());
            tourFailed.setCredits("" + newMotivation);
            apply(tourFailed);
            return;
         }

         if (newMotivation > 0 && event.getRoom().equals("exam")) {
            TourFailed tourFailed = new TourFailed();
            tourFailed.setId(newEventId());
            tourFailed.setStop(newStopId);
            tourFailed.setRoom(room.getId());
            tourFailed.setCredits("" + newMotivation);
            apply(tourFailed);
            return;
         }

         if (newMotivation == 0 && event.getRoom().equals("exam")) {
            TourList tourList = model.getOrCreateTourList(idPrefix + "TourList");
            String tourId = "tour" + tourList.getAlternatives().size();
            String stopList = "";

            CollectTourStops collectTourStops = new CollectTourStops();
            collectTourStops.setId(newEventId());
            collectTourStops.setStop(newStopId);
            collectTourStops.setTour(tourId);
            apply(collectTourStops);

            return;
         }

         // continue with neighbors
         for (Room neighbor : room.getDoors()) {
            VisitRoom roomSelected = new VisitRoom();
            roomSelected.setId(newEventId());
            roomSelected.setRoom(neighbor.getId());
            roomSelected.setPreviousStop(newStopId);
            apply(roomSelected);
         }
         return;
      }

      handleDemoVisitRoom(event);
   }

   private void handleDemoVisitRoom(VisitRoom event)
   {
      if (event.getId().equals("12:02")) {
         StopBuilt s02Event = new StopBuilt();
         s02Event.setId("12:02:01");
         s02Event.setBlockId("s02");
         s02Event.setRoom("math");
         s02Event.setMotivation("54");
         s02Event.setPreviousStop("s01");
         apply(s02Event);


         VisitRoom e1203 = new VisitRoom();

         e1203.setId("12:03");
         e1203.setRoom("algebra");
         e1203.setPreviousStop("s02");
         apply(e1203);

         VisitRoom e1204 = new VisitRoom();

         e1204.setId("12:04");
         e1204.setRoom("modeling");
         e1204.setPreviousStop("s02");
         apply(e1204);
      }
      if (event.getId().equals("12:03")) {
         StopBuilt s03Event = new StopBuilt();
         s03Event.setId("12:03:01");
         s03Event.setBlockId("s03");
         s03Event.setRoom("algebra");
         s03Event.setPreviousStop("s02");
         s03Event.setMotivation("42");
         apply(s03Event);


         VisitRoom e1205 = new VisitRoom();

         e1205.setId("12:05");
         e1205.setPreviousStop("s03");
         e1205.setRoom("modeling");
         apply(e1205);

         VisitRoom e1206 = new VisitRoom();

         e1206.setId("12:06");
         e1206.setPreviousStop("s03");
         e1206.setRoom("math");
         apply(e1206);
      }
      if (event.getId().equals("12:05")) {
         StopBuilt s05Event = new StopBuilt();
         s05Event.setId("12:05:01");
         s05Event.setBlockId("s05");
         s05Event.setRoom("modeling");
         s05Event.setPreviousStop("s03");
         s05Event.setMotivation("0");
         apply(s05Event);


         VisitRoom e1207 = new VisitRoom();

         e1207.setId("12:07");
         e1207.setPreviousStop("s05");
         e1207.setRoom("math");
         apply(e1207);

         VisitRoom e1208 = new VisitRoom();

         e1208.setId("12:08");
         e1208.setPreviousStop("s05");
         e1208.setRoom("exam");
         apply(e1208);
      }
      if (event.getId().equals("12:07")) {
         StopBuilt s07Event = new StopBuilt();
         s07Event.setId("12:07:01");
         s07Event.setBlockId("s07");
         s07Event.setRoom("math");
         s07Event.setPreviousStop("s05");
         s07Event.setMotivation("-23");
         apply(s07Event);


         TourFailed e120702 = new TourFailed();

         e120702.setId("12:07:02");
         e120702.setStop("s07");
         e120702.setRoom("math");
         e120702.setCredits("-23");
         apply(e120702);
      }
      if (event.getId().equals("12:08")) {
         StopBuilt s08Event = new StopBuilt();
         s08Event.setId("12:08:01");
         s08Event.setBlockId("s08");
         s08Event.setRoom("exam");
         s08Event.setPreviousStop("s05");
         s08Event.setMotivation("0");
         apply(s08Event);


         CollectTourStops e1209 = new CollectTourStops();

         e1209.setId("12:09");
         e1209.setStop("s08");
         e1209.setTour("tour1");
         apply(e1209);
      }
   }

   private void handleCollectTourStops(Event e)
   {
      // no fulib
      CollectTourStops event = (CollectTourStops) e;

      Stop currentStop = model.getOrCreateStop(event.getStop());
      String stopList = "";
      while (currentStop.getPreviousStop() != null) {
         stopList = currentStop.getRoom() + " " + stopList;
         TourBuilt tour1Event = new TourBuilt();
         tour1Event.setId(newEventId());
         tour1Event.setBlockId(event.getTour());
         tour1Event.setStops(stopList);
         tour1Event.setTourList(idPrefix + "TourList");
         apply(tour1Event);

         currentStop = currentStop.getPreviousStop();
      }

      handleDemoCollectTourStops(event);
   }

   private void handleDemoCollectTourStops(CollectTourStops event)
   {
      if (event.getId().equals("12:09")) {
         TourBuilt tour1Event = new TourBuilt();
         tour1Event.setId("12:09:02");
         tour1Event.setBlockId("tour1");
         tour1Event.setTourList("allTours");
         tour1Event.setStops("exam");
         apply(tour1Event);


         CollectTourStops e1210 = new CollectTourStops();

         e1210.setId("12:10");
         e1210.setStop("s05");
         e1210.setTour("tour1");
         apply(e1210);
      }
      if (event.getId().equals("12:10")) {
         TourBuilt tour1Event = new TourBuilt();
         tour1Event.setId("12:10:01");
         tour1Event.setBlockId("tour1");
         tour1Event.setStops("modeling exam");
         apply(tour1Event);


         CollectTourStops e1211 = new CollectTourStops();

         e1211.setId("12:11");
         e1211.setStop("s03");
         e1211.setTour("tour1");
         apply(e1211);
      }
      if (event.getId().equals("12:11")) {
         TourBuilt tour1Event = new TourBuilt();
         tour1Event.setId("12:11:01");
         tour1Event.setBlockId("tour1");
         tour1Event.setStops("algebra modeling exam");
         apply(tour1Event);


         CollectTourStops e1212 = new CollectTourStops();

         e1212.setId("12:12");
         e1212.setStop("s02");
         e1212.setTour("tour1");
         apply(e1212);
      }
      if (event.getId().equals("12:12")) {
         TourBuilt tour1Event = new TourBuilt();
         tour1Event.setId("12:12:01");
         tour1Event.setBlockId("tour1");
         tour1Event.setStops("math algebra modeling exam");
         apply(tour1Event);


         TourFound e1213 = new TourFound();

         e1213.setId("12:13");
         e1213.setTour("tour1");
         apply(e1213);
      }
   }

   private String idPrefix = "e";
   private int idNumber = 0;

   private String newEventId()
   {
      idNumber++;
      return idPrefix + idNumber;
   }

   private String getTourPage()
   {
      // load demo rooms
      idPrefix = "smallMap";
      idNumber = 0;
      LoadRooms loadSmallMap = new LoadRooms();
      loadSmallMap.setId("smallMap");
      apply(loadSmallMap);

      // find tours
      apply(new FindTours().setId(newEventId()));


      TourList tourList = model.getOrCreateTourList(idPrefix + "TourList");

      StringBuilder html = new StringBuilder();
      html.append("<form action=\"/page/tour\" method=\"get\">\n");
      // StudyRight 11:00
      html.append("   <p>Our topics are: </p>\n");
      University studyRight = model.getOrCreateUniversity("StudyRight");
      for (Room room : studyRight.getRooms()) {
         html.append(String.format("   <p>topic: %s \tcredits: %s</p>\n", room.getId(), room.getCredits()));
      }
      html.append("   <p> found tours: </p>\n");
      for (Tour tour : tourList.getAlternatives()) {
         html.append(String.format("   <p>%s: %s</p>\n", tour.getId(), tour.getStops()));
      }
      html.append("</form>\n");
      return html.toString();
   }

   private String getWelcomePage()
   {
      StringBuilder html = new StringBuilder();
      html.append("<form action=\"/page/tour\" method=\"get\">\n");
      // StudyRight 11:00
      html.append("   <p>Welcome at Study Right</p>\n");
      html.append("   <p>Find your way, start with math</p>\n");
      html.append("   <p><input id=\"event\" name=\"event\" type=\"hidden\" value=\"rooms loaded 12:00\"></p>\n");
      html.append("   <p><input id=\"ok\" name=\"button\" type=\"submit\" value=\"ok\"></p>\n");
      html.append("</form>\n");
      return html.toString();
   }

   public String getDemoPage(Request request, Response response)
   {
      StringBuilder html = new StringBuilder();
      String id = request.params("id");
      String event = request.queryParams("event");


      // 11:00
      if (id.equals("11_00")) {
         html.append("<form action=\"/page/12_13\" method=\"get\">\n");
         // StudyRight 11:00
         html.append("   <p>Welcome at Study Right</p>\n");
         html.append("   <p>Find your way, start with math</p>\n");
         html.append("   <p><input id=\"ok\" name=\"button\" type=\"submit\" value=\"ok\"></p>\n");
         html.append("</form>\n");
         return html.toString();
      }

      // 12:13
      if (id.equals("12_13")) {
         html.append("<form action=\"/page/next_page\" method=\"get\">\n");
         // StudyRight 12:13
         html.append("   <p>This are your alternatives</p>\n");
         html.append("   <p>tour1 math algebra modeling exam</p>\n");
         html.append("</form>\n");
         return html.toString();
      }



      html.append("This is the Shop Service page " + id + "\n");
      return html.toString();
   }

   private void loadSmallMap()
   {
      UniversityBuilt studyRightEvent = new UniversityBuilt();
      studyRightEvent.setId(newEventId());
      studyRightEvent.setBlockId("StudyRight");
      studyRightEvent.setRooms("[math exam]");
      apply(studyRightEvent);

      RoomBuilt mathEvent = new RoomBuilt();
      mathEvent.setId(newEventId());
      mathEvent.setBlockId("math");
      mathEvent.setCredits("23");
      mathEvent.setUni("StudyRight");
      mathEvent.setDoors("[modeling algebra]");
      apply(mathEvent);

      RoomBuilt modelingEvent = new RoomBuilt();
      modelingEvent.setId(newEventId());
      modelingEvent.setBlockId("modeling");
      modelingEvent.setUni("StudyRight");
      modelingEvent.setCredits("42");
      modelingEvent.setDoors("[math algebra exam]");
      apply(modelingEvent);

      RoomBuilt algebraEvent = new RoomBuilt();
      algebraEvent.setId(newEventId());
      algebraEvent.setBlockId("algebra");
      algebraEvent.setUni("StudyRight");
      algebraEvent.setCredits("12");
      apply(algebraEvent);

      RoomBuilt examEvent = new RoomBuilt();
      examEvent.setId(newEventId());
      examEvent.setBlockId("exam");
      examEvent.setDoors("[modeling algebra]");
      examEvent.setCredits("0");
      examEvent.setUni("StudyRight");
      apply(examEvent);
   }

   private void initEventHandlerMap()
   {
      if (handlerMap == null) {
         handlerMap = new LinkedHashMap<>();
         handlerMap.put(LoadRooms.class, this::handleLoadRooms);
         handlerMap.put(FindTours.class, this::handleFindTours);
         handlerMap.put(VisitRoom.class, this::handleVisitRoom);
         handlerMap.put(CollectTourStops.class, this::handleCollectTourStops);
         handlerMap.put(UniversityBuilt.class, this::handleUniversityBuilt);
         handlerMap.put(RoomBuilt.class, this::handleRoomBuilt);
         handlerMap.put(StudentBuilt.class, this::handleStudentBuilt);
         handlerMap.put(TourListBuilt.class, this::handleTourListBuilt);
         handlerMap.put(StopBuilt.class, this::handleStopBuilt);
         handlerMap.put(TourBuilt.class, this::handleTourBuilt);
      }
   }

   private void ignoreEvent(Event event)
   {
      // empty
   }

   public void publish(Event event)
   {
      String json = Yaml.encode(event);

      try {
         HttpResponse<String> response = Unirest
               .post("http://localhost:42000/publish")
               .body(json)
               .asString();
      }
      catch (UnirestException e) {
         e.printStackTrace();
      }
   }

   private String postApply(Request req, Response res)
   {
      try {
         String body = req.body();
         Map<String, Object> map = Yaml.decode(body);
         for (Object value : map.values()) {
            Event event = (Event) value;
            apply(event);
         }
      }
      catch (Exception e) {
         Logger.getGlobal().log(Level.SEVERE, "postApply failed", e);
      }
      return "apply done";
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

}
