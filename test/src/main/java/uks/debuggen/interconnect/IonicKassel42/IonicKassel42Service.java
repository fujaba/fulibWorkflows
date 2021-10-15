package uks.debuggen.interconnect.IonicKassel42;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.fulib.yaml.Yaml;
import org.fulib.yaml.YamlIdMap;
import spark.Request;
import spark.Response;
import spark.Service;
import uks.debuggen.interconnect.events.*;
import java.util.Objects;
import java.beans.PropertyChangeSupport;

public class IonicKassel42Service
{
   public static final String PROPERTY_HISTORY = "history";
   public static final String PROPERTY_PORT = "port";
   public static final String PROPERTY_SPARK = "spark";
   public static final String PROPERTY_MODEL = "model";
   public static final String PROPERTY_BUSINESS_LOGIC = "businessLogic";
   public static final String PROPERTY_BUILDER = "builder";
   private LinkedHashMap<String, Event> history = new LinkedHashMap<>();
   private int port = 42001;
   private Service spark;
   private IonicKassel42Model model;
   private IonicKassel42BusinessLogic businessLogic;
   private IonicKassel42Builder builder;
   protected PropertyChangeSupport listeners;

   public LinkedHashMap<String, Event> getHistory()
   {
      return this.history;
   }

   public IonicKassel42Service setHistory(LinkedHashMap<String, Event> value)
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

   public IonicKassel42Service setPort(int value)
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

   public IonicKassel42Service setSpark(Service value)
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

   public IonicKassel42Model getModel()
   {
      return this.model;
   }

   public IonicKassel42Service setModel(IonicKassel42Model value)
   {
      if (Objects.equals(value, this.model))
      {
         return this;
      }

      final IonicKassel42Model oldValue = this.model;
      this.model = value;
      this.firePropertyChange(PROPERTY_MODEL, oldValue, value);
      return this;
   }

   public IonicKassel42BusinessLogic getBusinessLogic()
   {
      return this.businessLogic;
   }

   public IonicKassel42Service setBusinessLogic(IonicKassel42BusinessLogic value)
   {
      if (this.businessLogic == value)
      {
         return this;
      }

      final IonicKassel42BusinessLogic oldValue = this.businessLogic;
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

   public IonicKassel42Builder getBuilder()
   {
      return this.builder;
   }

   public IonicKassel42Service setBuilder(IonicKassel42Builder value)
   {
      if (this.builder == value)
      {
         return this;
      }

      final IonicKassel42Builder oldValue = this.builder;
      if (this.builder != null)
      {
         this.builder = null;
         oldValue.setService(null);
      }
      this.builder = value;
      if (value != null)
      {
         value.setService(this);
      }
      this.firePropertyChange(PROPERTY_BUILDER, oldValue, value);
      return this;
   }

   public Query query(Query query)
   {
      DataEvent dataEvent = getBuilder().getEventStore().get(query.getKey());

      if (dataEvent == null) {
         return query;
      }

      if (dataEvent instanceof DataGroup) {
         DataGroup group = (DataGroup) dataEvent;
         query.withResults(group.getElements());
      }
      else {
         query.withResults(dataEvent);
      }

      return query;
   }

   public String isoNow()
   {
      return DateTimeFormatter.ISO_INSTANT.format(Instant.now());
   }

   public void start()
   {
      Unirest.setTimeouts(3*60*1000, 3*60*1000);
      model = new IonicKassel42Model();
      setBuilder(new IonicKassel42Builder().setModel(model));
      setBusinessLogic(new IonicKassel42BusinessLogic());
      businessLogic.setBuilder(getBuilder());
      businessLogic.setModel(model);
      ExecutorService executor = Executors.newSingleThreadExecutor();
      spark = Service.ignite();
      spark.port(port);
      spark.get("/page/:id", (req, res) -> executor.submit(() -> this.getPage(req, res)).get());
      spark.get("/", (req, res) -> executor.submit(() -> this.getHello(req, res)).get());
      spark.post("/apply", (req, res) -> executor.submit(() -> this.postApply(req, res)).get());
      executor.submit(this::subscribeAndLoadOldEvents);
      Logger.getGlobal().info("IonicKassel42 service is up and running on port " + port);
   }

   private String getHello(Request req, Response res)
   {
      try {
         String events = Yaml.encodeSimple(getHistory().values().toArray());
         String objects = Yaml.encodeSimple(model.getModelMap().values().toArray());
         return "<p id='IonicKassel42'>This is the IonicKassel42 service. </p>\n" +
               "<pre id=\"history\">" + events + "</pre>\n" +
               "<pre id=\"data\">" + objects + "</pre>\n" +
               "";
      }
      catch (Exception e) {
         Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
         return "IonicKassel42 Error " + e.getMessage();
      }
   }

   private void subscribeAndLoadOldEvents()
   {
      ServiceSubscribed serviceSubscribed = new ServiceSubscribed()
            .setServiceUrl(String.format("http://localhost:%d/apply", port));
      String json = Yaml.encodeSimple(serviceSubscribed);
      try {
         String url = "http://localhost:42000/subscribe";
         HttpResponse<String> response = Unirest
               .post(url)
               .body(json)
               .asString();
         String body = response.getBody();
         YamlIdMap idMap = new YamlIdMap(Event.class.getPackageName());
         idMap.decode(body);
         Map<String, Object> objectMap = idMap.getObjIdMap();
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
      Consumer<Event> handler = businessLogic.getHandler(event);
      handler.accept(event);
      history.put(event.getId(), event);
      firePropertyChange(PROPERTY_HISTORY, null, event);
      publish(event);
   }

   public String getPage(Request request, Response response)
   {
      // to protect manuel changes to this method insert a 'no' in front of fulib in the next line
      // fulib
      return getDemoPage(request, response);
   }

   public String getDemoPage(Request request, Response response)
   {
      StringBuilder html = new StringBuilder();
      String id = request.params("id");
      String event = request.queryParams("event");



      html.append("This is the Shop Service page " + id + "\n");
      return html.toString();
   }

   public void publish(Event event)
   {
      String json = Yaml.encodeSimple(event);

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
      String body = req.body();
      try {
         YamlIdMap idMap = new YamlIdMap(Event.class.getPackageName());
         idMap.decode(body);
         Map<String, Object> map = idMap.getObjIdMap();
         for (Object value : map.values()) {
            Event event = (Event) value;
            apply(event);
         }
      }
      catch (Exception e) {
         String message = e.getMessage();
         if (message.contains("ReflectorMap could not find class description")) {
            Logger.getGlobal().info("post apply ignores unknown event " + body);
         }
         else {
            Logger.getGlobal().log(Level.SEVERE, "postApply failed", e);
         }
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

   public void removeYou()
   {
      this.setBusinessLogic(null);
      this.setBuilder(null);
   }

   public void stop()
   {
      spark.stop();
   }
}
