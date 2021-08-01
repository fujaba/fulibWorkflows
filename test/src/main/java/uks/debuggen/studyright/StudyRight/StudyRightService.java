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
      RoomsLoaded loadSmallMap = new RoomsLoaded();
      loadSmallMap.setId("smallMap");
      apply(loadSmallMap);

      // find tours
      StopEdited s01Event = new StopEdited();
      s01Event.setId(newEventId());
      String firstStop = "stop" + idNumber;
      s01Event.setIncrement(firstStop);
      s01Event.setMotivation("77");
      apply(s01Event);


      RoomSelected roomSelected = new RoomSelected();
      roomSelected.setId(newEventId());
      roomSelected.setRoom("math");
      roomSelected.setPreviousStop(firstStop);
      apply(roomSelected);

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

      if ("rooms loaded 12:00".equals(event)) {

         // create RoomsLoaded: rooms loaded 12:00
         RoomsLoaded e1200 = new RoomsLoaded();
         e1200.setId("12:00");
         apply(e1200);
      }



      // 11:00
      if (id.equals("11_00")) {
         html.append("<form action=\"/page/12_13\" method=\"get\">\n");
         // StudyRight 11:00
         html.append("   <p>Welcome at Study Right</p>\n");
         html.append("   <p>Find your way, start with math</p>\n");
         html.append("   <p><input id=\"event\" name=\"event\" type=\"hidden\" value=\"rooms loaded 12:00\"></p>\n");
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

   private void handleRoomsLoaded(Event e)
   {
      // no fulib
      RoomsLoaded event = (RoomsLoaded) e;

      if (event.getId().equals("smallMap")) {
         loadSmallMap();
         return;
      }

      handleDemoRoomsLoaded(event);
   }

   private void loadSmallMap()
   {
      UniversityEdited studyRightEvent = new UniversityEdited();
      studyRightEvent.setId(newEventId());
      studyRightEvent.setIncrement("StudyRight");
      studyRightEvent.setRooms("[math exam]");
      apply(studyRightEvent);

      RoomEdited mathEvent = new RoomEdited();
      mathEvent.setId(newEventId());
      mathEvent.setIncrement("math");
      mathEvent.setCredits("23");
      mathEvent.setUni("StudyRight");
      mathEvent.setDoors("[modeling algebra]");
      apply(mathEvent);

      RoomEdited modelingEvent = new RoomEdited();
      modelingEvent.setId(newEventId());
      modelingEvent.setIncrement("modeling");
      modelingEvent.setUni("StudyRight");
      modelingEvent.setCredits("42");
      modelingEvent.setDoors("[math algebra exam]");
      apply(modelingEvent);

      RoomEdited algebraEvent = new RoomEdited();
      algebraEvent.setId(newEventId());
      algebraEvent.setIncrement("algebra");
      algebraEvent.setUni("StudyRight");
      algebraEvent.setCredits("12");
      apply(algebraEvent);

      RoomEdited examEvent = new RoomEdited();
      examEvent.setId(newEventId());
      examEvent.setIncrement("exam");
      examEvent.setDoors("[modeling algebra]");
      examEvent.setCredits("0");
      examEvent.setUni("StudyRight");
      apply(examEvent);
   }

   private void initEventHandlerMap()
   {
      if (handlerMap == null) {
         handlerMap = new LinkedHashMap<>();
         handlerMap.put(RoomsLoaded.class, this::handleRoomsLoaded);
         handlerMap.put(TourStarted.class, this::handleTourStarted);
         handlerMap.put(RoomSelected.class, this::handleRoomSelected);
         handlerMap.put(TourEndFound.class, this::handleTourEndFound);
         handlerMap.put(UniversityEdited.class, this::handleUniversityEdited);
         handlerMap.put(RoomEdited.class, this::handleRoomEdited);
         handlerMap.put(StopEdited.class, this::handleStopEdited);
         handlerMap.put(TourEdited.class, this::handleTourEdited);
         handlerMap.put(TourListEdited.class, this::handleTourListEdited);
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

   private void handleTourStarted(Event e)
   {
      // no fulib
      TourStarted event = (TourStarted) e;
      handleDemoTourStarted(event);
   }

   private void handleUniversityEdited(Event e)
   {
      UniversityEdited event = (UniversityEdited) e;
      University object = model.getOrCreateUniversity(event.getIncrement());
      for (String name : stripBrackets(event.getRooms()).split("\\s+")) {
      if (name.equals("")) continue;
         object.withRooms(model.getOrCreateRoom(name));
      }
   }

   private void handleRoomEdited(Event e)
   {
      RoomEdited event = (RoomEdited) e;
      Room object = model.getOrCreateRoom(event.getIncrement());
      object.setCredits(event.getCredits());
      object.setUni(model.getOrCreateUniversity(event.getUni()));
      for (String name : stripBrackets(event.getDoors()).split("\\s+")) {
      if (name.equals("")) continue;
         object.withDoors(model.getOrCreateRoom(name));
      }
   }

   private void handleStopEdited(Event e)
   {
      StopEdited event = (StopEdited) e;
      Stop object = model.getOrCreateStop(event.getIncrement());
      object.setMotivation(event.getMotivation());
      object.setRoom(event.getRoom());
      object.setPreviousStop(model.getOrCreateStop(event.getPreviousStop()));
   }

   private void handleRoomSelected(Event e)
   {
      // no fulib
      RoomSelected event = (RoomSelected) e;

      if (event.getId().startsWith(idPrefix)) {
         // always create a stop
         Room room = model.getOrCreateRoom(event.getRoom());
         Stop previousStop = model.getOrCreateStop(event.getPreviousStop());
         String motivation = previousStop.getMotivation();
         String credits = room.getCredits();
         int newMotivation = Integer.parseInt(motivation) - Integer.parseInt(credits);

         StopEdited newStopEvent = new StopEdited();
         newStopEvent.setId(newEventId());
         String newStopId = "stop" + idNumber;
         newStopEvent.setIncrement(newStopId);
         newStopEvent.setRoom(event.getRoom());
         newStopEvent.setMotivation("" + newMotivation);
         newStopEvent.setPreviousStop(event.getPreviousStop());
         apply(newStopEvent);

         // on negative motivation, stop searching
         if (newMotivation < 0) {
            return;
         }

         if (newMotivation == 0 && event.getRoom().equals("exam")) {
            // success, add to smallMap tour list
            TourListEdited allToursEvent = new TourListEdited();
            allToursEvent.setId(idPrefix + "TourList");
            allToursEvent.setIncrement(idPrefix + "TourList");
            apply(allToursEvent);

            TourList tourList = model.getOrCreateTourList(idPrefix + "TourList");
            String tourId = "tour" + tourList.getAlternatives().size();
            String stopList = "";

            Stop currentStop = model.getOrCreateStop(newStopId);
            while (currentStop.getPreviousStop() != null) {
               stopList = currentStop.getRoom() + " " + stopList;
               TourEdited tour1Event = new TourEdited();
               tour1Event.setId(newEventId());
               tour1Event.setIncrement(tourId);
               tour1Event.setStops(stopList);
               tour1Event.setTourList(idPrefix + "TourList");
               apply(tour1Event);

               currentStop = currentStop.getPreviousStop();
            }
            return;
         }

         // continue with neighbors
         for (Room neighbor : room.getDoors()) {
            RoomSelected roomSelected = new RoomSelected();
            roomSelected.setId(newEventId());
            roomSelected.setRoom(neighbor.getId());
            roomSelected.setPreviousStop(newStopId);
            apply(roomSelected);
         }
         return;
      }

      handleDemoRoomSelected(event);
   }

   private void handleDemoTourStarted(TourStarted event)
   {
      if (event.getId().equals("12:01")) {
         StopEdited s01Event = new StopEdited();
         s01Event.setId("12:01:01");
         s01Event.setIncrement("s01");
         s01Event.setMotivation("77");
         apply(s01Event);


         RoomSelected e1202 = new RoomSelected();

         e1202.setId("12:02");
         e1202.setEvent("room selected 12:02");
         e1202.setRoom("math");
         e1202.setPreviousStop("s01");
         apply(e1202);
      }
   }

   private void handleDemoRoomSelected(RoomSelected event)
   {
      if (event.getId().equals("12:02")) {
         StopEdited s02Event = new StopEdited();
         s02Event.setId("12:02:01");
         s02Event.setIncrement("s02");
         s02Event.setRoom("math");
         s02Event.setMotivation("54");
         s02Event.setPreviousStop("s01");
         apply(s02Event);


         RoomSelected e1203 = new RoomSelected();

         e1203.setId("12:03");
         e1203.setEvent("room selected 12:03");
         e1203.setRoom("algebra");
         e1203.setPreviousStop("s02");
         apply(e1203);

         RoomSelected e1204 = new RoomSelected();

         e1204.setId("12:04");
         e1204.setEvent("room selected 12:04");
         e1204.setRoom("modeling");
         e1204.setPreviousStop("s02");
         apply(e1204);
      }
      if (event.getId().equals("12:03")) {
         StopEdited s03Event = new StopEdited();
         s03Event.setId("12:03:01");
         s03Event.setIncrement("s03");
         s03Event.setRoom("algebra");
         s03Event.setPreviousStop("s02");
         s03Event.setMotivation("42");
         apply(s03Event);


         RoomSelected e1205 = new RoomSelected();

         e1205.setId("12:05");
         e1205.setEvent("room selected 12:05");
         e1205.setPreviousStop("s03");
         e1205.setRoom("modeling");
         apply(e1205);

         RoomSelected e1206 = new RoomSelected();

         e1206.setId("12:06");
         e1206.setEvent("room selected 12:06");
         e1206.setPreviousStop("s03");
         e1206.setRoom("math");
         apply(e1206);
      }
      if (event.getId().equals("12:05")) {
         StopEdited s05Event = new StopEdited();
         s05Event.setId("12:05:01");
         s05Event.setIncrement("s05");
         s05Event.setRoom("modeling");
         s05Event.setPreviousStop("s03");
         s05Event.setMotivation("0");
         apply(s05Event);


         RoomSelected e1207 = new RoomSelected();

         e1207.setId("12:07");
         e1207.setEvent("room selected 12:07");
         e1207.setPreviousStop("s05");
         e1207.setRoom("math");
         apply(e1207);

         RoomSelected e1208 = new RoomSelected();

         e1208.setId("12:08");
         e1208.setEvent("room selected 12:08");
         e1208.setPreviousStop("s05");
         e1208.setRoom("exam");
         apply(e1208);
      }
      if (event.getId().equals("12:07")) {
         StopEdited s07Event = new StopEdited();
         s07Event.setId("12:07:01");
         s07Event.setIncrement("s07");
         s07Event.setRoom("math");
         s07Event.setPreviousStop("s05");
         s07Event.setMotivation("-23");
         apply(s07Event);

      }
      if (event.getId().equals("12:08")) {
         StopEdited s08Event = new StopEdited();
         s08Event.setId("12:08:01");
         s08Event.setIncrement("s08");
         s08Event.setRoom("exam");
         s08Event.setPreviousStop("s05");
         s08Event.setMotivation("0");
         apply(s08Event);

         TourEdited tour1Event = new TourEdited();
         tour1Event.setId("12:08:02");
         tour1Event.setIncrement("tour1");
         tour1Event.setStops("exam");
         apply(tour1Event);

         TourListEdited allToursEvent = new TourListEdited();
         allToursEvent.setId("12:08:03");
         allToursEvent.setIncrement("allTours");
         allToursEvent.setAlternatives("[tour1]");
         apply(allToursEvent);


         TourEndFound e1209 = new TourEndFound();

         e1209.setId("12:09");
         e1209.setEvent("tour end found 12:09");
         e1209.setStop("s08");
         e1209.setTour("tour1");
         e1209.setTourList("allTours");
         apply(e1209);
      }
   }

   private void handleDemoRoomsLoaded(RoomsLoaded event)
   {
      if (event.getId().equals("12:00")) {
         UniversityEdited studyRightEvent = new UniversityEdited();
         studyRightEvent.setId("12:00:00");
         studyRightEvent.setIncrement("StudyRight");
         studyRightEvent.setRooms("[math exam]");
         apply(studyRightEvent);

         RoomEdited mathEvent = new RoomEdited();
         mathEvent.setId("12:00:01");
         mathEvent.setIncrement("math");
         mathEvent.setCredits("23");
         mathEvent.setUni("StudyRight");
         mathEvent.setDoors("[modeling algebra]");
         apply(mathEvent);

         RoomEdited modelingEvent = new RoomEdited();
         modelingEvent.setId("12:00:02");
         modelingEvent.setIncrement("modeling");
         modelingEvent.setUni("StudyRight");
         modelingEvent.setCredits("42");
         modelingEvent.setDoors("[math algebra exam]");
         apply(modelingEvent);

         RoomEdited algebraEvent = new RoomEdited();
         algebraEvent.setId("12:00:03");
         algebraEvent.setIncrement("algebra");
         algebraEvent.setUni("StudyRight");
         algebraEvent.setCredits("12");
         apply(algebraEvent);

         RoomEdited examEvent = new RoomEdited();
         examEvent.setId("12:00:04");
         examEvent.setIncrement("exam");
         examEvent.setCredits("0");
         examEvent.setUni("StudyRight");
         apply(examEvent);


         TourStarted e1201 = new TourStarted();

         e1201.setId("12:01");
         e1201.setEvent("tour started 12:01");
         apply(e1201);
      }
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

   private void handleTourEndFound(Event e)
   {
      // no fulib
      TourEndFound event = (TourEndFound) e;
      handleDemoTourEndFound(event);
   }

   private void handleDemoTourEndFound(TourEndFound event)
   {
      if (event.getId().equals("12:09")) {
         TourEdited tour1Event = new TourEdited();
         tour1Event.setId("12:09:01");
         tour1Event.setIncrement("tour1");
         tour1Event.setStops("modeling exam");
         tour1Event.setTourList("allTours");
         apply(tour1Event);


         TourEndFound e1210 = new TourEndFound();

         e1210.setId("12:10");
         e1210.setEvent("tour end found 12:10");
         e1210.setStop("s05");
         e1210.setTour("tour1");
         apply(e1210);
      }
      if (event.getId().equals("12:10")) {
         TourEdited tour1Event = new TourEdited();
         tour1Event.setId("12:10:01");
         tour1Event.setIncrement("tour1");
         tour1Event.setStops("algebra modeling exam");
         apply(tour1Event);


         TourEndFound e1211 = new TourEndFound();

         e1211.setId("12:11");
         e1211.setEvent("tour end found 12:11");
         e1211.setStop("s03");
         e1211.setTour("tour1");
         apply(e1211);
      }
      if (event.getId().equals("12:11")) {
         TourEdited tour1Event = new TourEdited();
         tour1Event.setId("12:11:01");
         tour1Event.setIncrement("tour1");
         tour1Event.setStops("math algebra modeling exam");
         apply(tour1Event);


         TourEndFound e1212 = new TourEndFound();

         e1212.setId("12:12");
         e1212.setEvent("tour end found 12:12");
         e1212.setStop("s02");
         e1212.setTour("tour1");
         apply(e1212);
      }
   }

   private void handleTourEdited(Event e)
   {
      TourEdited event = (TourEdited) e;
      Tour object = model.getOrCreateTour(event.getIncrement());
      object.setStops(event.getStops());
      object.setTourList(model.getOrCreateTourList(event.getTourList()));
   }

   private void handleTourListEdited(Event e)
   {
      TourListEdited event = (TourListEdited) e;
      TourList object = model.getOrCreateTourList(event.getIncrement());
      for (String name : stripBrackets(event.getAlternatives()).split("\\s+")) {
      if (name.equals("")) continue;
         object.withAlternatives(model.getOrCreateTour(name));
      }
   }
}
