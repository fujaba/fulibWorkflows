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
   public static final String PROPERTY_BUSINESS_LOGIC = "businessLogic";
   private LinkedHashMap<String, Event> history = new LinkedHashMap<>();
   private int port = 42400;
   private Service spark;
   private StudyRightModel model;
   protected PropertyChangeSupport listeners;
   private StudyRightBusinessLogic businessLogic;

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

   public StudyRightBusinessLogic getBusinessLogic()
   {
      return this.businessLogic;
   }

   public StudyRightService setBusinessLogic(StudyRightBusinessLogic value)
   {
      if (this.businessLogic == value)
      {
         return this;
      }

      final StudyRightBusinessLogic oldValue = this.businessLogic;
      if (this.businessLogic != null)
      {
         this.businessLogic = null;
         oldValue.setService(null);
      }
      this.businessLogic = value;
      if (value != null)
      {
         value.setService(this);
      }
      this.firePropertyChange(PROPERTY_BUSINESS_LOGIC, oldValue, value);
      return this;
   }

   public void start()
   {
      model = new StudyRightModel();
      setBusinessLogic(new StudyRightBusinessLogic());
      businessLogic.setModel(model);
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
      businessLogic.initEventHandlerMap();
      Consumer<Event> handler = businessLogic.getHandlerMap().computeIfAbsent(event.getClass(), k -> this::ignoreEvent);
      handler.accept(event);
      history.put(event.getId(), event);
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

   public void removeYou()
   {
      this.setBusinessLogic(null);
   }

   private String getTourPage()
   {
      // load demo rooms
      businessLogic.idPrefix = "smallMap";
      businessLogic.idNumber = 0;
      LoadRoomsCommand loadSmallMap = new LoadRoomsCommand();
      loadSmallMap.setId("smallMap");
      apply(loadSmallMap);

      // find tours
      apply(new FindToursCommand().setId(businessLogic.newEventId()));


      TourList tourList = model.getOrCreateTourList(businessLogic.idPrefix + "TourList");

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

      if ("load rooms 12:00".equals(event)) {

         // create LoadRoomsCommand: load rooms 12:00
         LoadRoomsCommand e1200 = new LoadRoomsCommand();
         e1200.setId("12:00");
         apply(e1200);
      }



      // 11:00
      if (id.equals("11_00")) {
         html.append("<form action=\"/page/12_13\" method=\"get\">\n");
         // StudyRight 11:00
         html.append("   <p>Welcome at Study Right</p>\n");
         html.append("   <p>Find your way, start with math</p>\n");
         html.append("   <p><input id=\"event\" name=\"event\" type=\"hidden\" value=\"load rooms 12:00\"></p>\n");
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

}
