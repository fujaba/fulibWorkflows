package uks.debuggen.page.Shop;

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
import uks.debuggen.page.events.*;

import java.util.Objects;
import java.beans.PropertyChangeSupport;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import org.fulib.yaml.YamlIdMap;

public class ShopService
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
   private ShopModel model;
   protected PropertyChangeSupport listeners;
   private ShopBusinessLogic businessLogic;
   private ShopBuilder builder;

   public LinkedHashMap<String, Event> getHistory()
   {
      return this.history;
   }

   public ShopService setHistory(LinkedHashMap<String, Event> value)
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

   public ShopService setPort(int value)
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

   public ShopService setSpark(Service value)
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

   public ShopModel getModel()
   {
      return this.model;
   }

   public ShopService setModel(ShopModel value)
   {
      if (Objects.equals(value, this.model))
      {
         return this;
      }

      final ShopModel oldValue = this.model;
      this.model = value;
      this.firePropertyChange(PROPERTY_MODEL, oldValue, value);
      return this;
   }

   public ShopBusinessLogic getBusinessLogic()
   {
      return this.businessLogic;
   }

   public ShopService setBusinessLogic(ShopBusinessLogic value)
   {
      if (this.businessLogic == value)
      {
         return this;
      }

      final ShopBusinessLogic oldValue = this.businessLogic;
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

   public ShopBuilder getBuilder()
   {
      return this.builder;
   }

   public ShopService setBuilder(ShopBuilder value)
   {
      if (this.builder == value)
      {
         return this;
      }

      final ShopBuilder oldValue = this.builder;
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

   public void start()
   {
      model = new ShopModel();
      setBuilder(new ShopBuilder().setModel(model));
      setBusinessLogic(new ShopBusinessLogic());
      businessLogic.setBuilder(getBuilder());
      businessLogic.setModel(model);
      ExecutorService executor = Executors.newSingleThreadExecutor();
      spark = Service.ignite();
      spark.port(port);
      spark.get("/page/:id", (req, res) -> executor.submit(() -> this.getPage(req, res)).get());
      spark.get("/", (req, res) -> executor.submit(() -> this.getHello(req, res)).get());
      spark.post("/apply", (req, res) -> executor.submit(() -> this.postApply(req, res)).get());
      executor.submit(this::subscribeAndLoadOldEvents);
      Logger.getGlobal().info("Shop service is up and running on port " + port);
   }

   private String getHello(Request req, Response res)
   {
      try {
         String events = Yaml.encodeSimple(getHistory().values().toArray());
         String objects = Yaml.encodeSimple(model.getModelMap().values().toArray());
         return "<p id='Shop'>This is the Shop service. </p>\n" +
               "<pre id=\"history\">" + events + "</pre>\n" +
               "<pre id=\"data\">" + objects + "</pre>\n" +
               "";
      }
      catch (Exception e) {
         Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
         return "Shop Error " + e.getMessage();
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

   public String getDemoPage(Request request, Response response)
   {
      StringBuilder html = new StringBuilder();
      String id = request.params("id");
      String event = request.queryParams("event");

      if ("product selected 12:51".equals(event)) {

         // create ProductSelectedCommand: product selected 12:51
         ProductSelectedCommand e1251 = new ProductSelectedCommand();
         e1251.setId("12:51");
         apply(e1251);
      }

      if ("order registered 13:00".equals(event)) {

         // create OrderRegisteredCommand: order registered 13:00
         OrderRegisteredCommand e1300 = new OrderRegisteredCommand();
         e1300.setId("13:00");
         e1300.setCount(request.queryParams("count"));
         e1300.setName(request.queryParams("name"));
         e1300.setAddress(request.queryParams("address"));
         apply(e1300);
      }

      if ("product selected 13:10".equals(event)) {

         // create ProductSelectedCommand: product selected 13:10
         ProductSelectedCommand e1310 = new ProductSelectedCommand();
         e1310.setId("13:10");
         apply(e1310);
      }



      // 12:50
      if (id.equals("12_50")) {
         html.append("<form action=\"/page/12_55\" method=\"get\">\n");
         // Shop 12:50
         html.append("   <p>Welcome to the event shop</p>\n");
         html.append("   <p>What do you want?</p>\n");
         html.append("   <p><input id=\"event\" name=\"event\" type=\"hidden\" value=\"product selected 12:51\"></p>\n");
         html.append("   <p><input id=\"shoes\" name=\"button\" type=\"submit\" value=\"shoes\"></p>\n");
         html.append("   <p><input id=\"tshirt\" name=\"button\" type=\"submit\" value=\"tshirt\"></p>\n");
         html.append("</form>\n");
         return html.toString();
      }

      // 12:55
      if (id.equals("12_55")) {
         html.append("<form action=\"/page/13_07\" method=\"get\">\n");
         // Shop 12:55
         html.append("   <p>Shoes order</p>\n");
         html.append("   <p><input id=\"count\" name=\"count\" placeholder=\"count?\"></p>\n");
         html.append("   <p><input id=\"name\" name=\"name\" placeholder=\"name?\"></p>\n");
         html.append("   <p><input id=\"address\" name=\"address\" placeholder=\"address?\"></p>\n");
         html.append("   <p><input id=\"event\" name=\"event\" type=\"hidden\" value=\"order registered 13:00\"></p>\n");
         html.append("   <p><input id=\"ok\" name=\"button\" type=\"submit\" value=\"ok\"></p>\n");
         html.append("</form>\n");
         return html.toString();
      }

      // 13:07
      if (id.equals("13_07")) {
         html.append("<form action=\"/page/next_page\" method=\"get\">\n");
         // Shop 13:07
         html.append("   <p>Your order</p>\n");
         html.append("   <p>order1300 shoes is pending</p>\n");
         html.append("   <p>What else do you want</p>\n");
         html.append("   <p><input id=\"shoes\" name=\"button\" type=\"submit\" value=\"shoes\"></p>\n");
         html.append("   <p><input id=\"event\" name=\"event\" type=\"hidden\" value=\"product selected 13:10\"></p>\n");
         html.append("   <p><input id=\"tshirt\" name=\"button\" type=\"submit\" value=\"tshirt\"></p>\n");
         html.append("</form>\n");
         return html.toString();
      }



      html.append("This is the Shop Service page " + id + "\n");
      return html.toString();
   }

   public void removeYou()
   {
      this.setBusinessLogic(null);
      this.setBuilder(null);
   }
}
