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

   public void apply(Event event)
   {
      if (history.get(event.getId()) != null) {
         return;
      }
      initEventHandlerMap();
      Consumer<Event> handler = handlerMap.computeIfAbsent(event.getClass(), k -> this::ignoreEvent);
      handler.accept(event);
      history.put(event.getId(), event);
      publish(event);
   }

   public String getPage(Request request, Response response)
   {
      // no fulib
      try {
         // add your page handling here
         StringBuilder html = new StringBuilder();
         String id = request.params("id");
         String event = request.queryParams("event");
         if (id.equals("welcome")) {
            html.append("<form action=\"/page/tour\" method=\"get\">\n");
            // StudyRight 11:00
            html.append("   <p>Welcome at Study Right</p>\n");
            html.append("   <p>Find your way, start with math</p>\n");
            html.append("   <p><input id=\"event\" name=\"event\" type=\"hidden\" value=\"rooms loaded 12:00\"></p>\n");
            html.append("   <p><input id=\"ok\" name=\"button\" type=\"submit\" value=\"ok\"></p>\n");
            html.append("</form>\n");
            return html.toString();
         }
         return getDemoPage(request, response);
      }
      catch (Exception e) {
         e.printStackTrace();
      }
      return "Exception raised";
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
         html.append("<form action=\"/page/next_page\" method=\"get\">\n");
         // StudyRight 11:00
         html.append("   <p>Welcome at Study Right</p>\n");
         html.append("   <p>Find your way, start with math</p>\n");
         html.append("   <p><input id=\"event\" name=\"event\" type=\"hidden\" value=\"rooms loaded 12:00\"></p>\n");
         html.append("   <p><input id=\"ok\" name=\"button\" type=\"submit\" value=\"ok\"></p>\n");
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

      handleDemoRoomsLoaded(event);
   }

   private void initEventHandlerMap()
   {
      if (handlerMap == null) {
         handlerMap = new LinkedHashMap<>();
         handlerMap.put(RoomsLoaded.class, this::handleRoomsLoaded);
         handlerMap.put(TourStarted.class, this::handleTourStarted);
         handlerMap.put(RoomSelected.class, this::handleRoomSelected);
         handlerMap.put(UniversityEdited.class, this::handleUniversityEdited);
         handlerMap.put(RoomEdited.class, this::handleRoomEdited);
         handlerMap.put(StopEdited.class, this::handleStopEdited);
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
         e1203.setRoom("modeling");
         e1203.setPreviousStop("s02");
         apply(e1203);

         RoomSelected e1204 = new RoomSelected();

         e1204.setId("12:04");
         e1204.setEvent("room selected 12:04");
         e1204.setRoom("algebra");
         e1204.setPreviousStop("s02");
         apply(e1204);
      }
      if (event.getId().equals("12:03")) {
         StopEdited s03Event = new StopEdited();
         s03Event.setId("12:03:01");
         s03Event.setIncrement("s03");
         s03Event.setRoom("modeling");
         s03Event.setPreviousStop("s02");
         s03Event.setMotivation("12");
         apply(s03Event);

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
         modelingEvent.setCredits("42");
         modelingEvent.setDoors("[math algebra exam]");
         apply(modelingEvent);

         RoomEdited algebraEvent = new RoomEdited();
         algebraEvent.setId("12:00:03");
         algebraEvent.setIncrement("algebra");
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
}
