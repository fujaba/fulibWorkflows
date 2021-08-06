package uks.debuggen.shop.Shop;
import java.util.Objects;
import java.beans.PropertyChangeSupport;
import java.util.LinkedHashMap;
import uks.debuggen.shop.events.*;
import spark.Service;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import spark.Request;
import spark.Response;
import org.fulib.yaml.Yaml;

import java.util.logging.Level;
import java.util.logging.Logger;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.util.Map;
import java.util.function.Consumer;

public class ShopService
{
   public static final String PROPERTY_HISTORY = "history";
   public static final String PROPERTY_PORT = "port";
   public static final String PROPERTY_SPARK = "spark";
   public static final String PROPERTY_MODEL = "model";
   public static final String PROPERTY_BUSINESS_LOGIC = "businessLogic";
   private LinkedHashMap<String, Event> history = new LinkedHashMap<>();
   protected PropertyChangeSupport listeners;
   private int port = 42100;
   private Service spark;
   private ShopModel model;
   private ShopBusinessLogic businessLogic;

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

   public void start()
   {
      model = new ShopModel();
      setBusinessLogic(new ShopBusinessLogic());
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

   public String getPage(Request request, Response response)
   {
      // no fulib
      // add your page handling here
      return getDemoPage(request, response);
   }

   private String getHello(Request req, Response res)
   {
      try {
         String events = Yaml.encode(getHistory().values().toArray());
         String objects = Yaml.encode(model.getModelMap().values().toArray());
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
            .setServiceUrl("http://localhost:42100/apply");
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


   public String getDemoPage(Request request, Response response)
   {
      StringBuilder html = new StringBuilder();
      String id = request.params("id");
      String event = request.queryParams("event");


      // 12:50
      if (id.equals("12_50")) {
         html.append("<form action=\"/page/13_00\" method=\"get\">\n");
         // Shop 12:50
         html.append("   <p>Welcome to the event shop</p>\n");
         html.append("   <p>What do you want?</p>\n");
         html.append("   <p><input id=\"shoes\" name=\"button\" type=\"submit\" value=\"shoes\"></p>\n");
         html.append("   <p><input id=\"tshirt\" name=\"button\" type=\"submit\" value=\"tshirt\"></p>\n");
         html.append("</form>\n");
         return html.toString();
      }

      // 13:00
      if (id.equals("13_00")) {
         html.append("<form action=\"/page/next_page\" method=\"get\">\n");
         // Shop 13:00
         html.append("   <p>welcome to the shop</p>\n");
         html.append("   <p><input id=\"product\" name=\"product\" placeholder=\"product?\"></p>\n");
         html.append("   <p><input id=\"name\" name=\"name\" placeholder=\"name?\"></p>\n");
         html.append("   <p><input id=\"address\" name=\"address\" placeholder=\"address?\"></p>\n");
         html.append("   <p><input id=\"OK\" name=\"button\" type=\"submit\" value=\"OK\"></p>\n");
         html.append("</form>\n");
         return html.toString();
      }



      html.append("This is the Shop Service page " + id + "\n");
      return html.toString();
   }

   public void removeYou()
   {
      this.setBusinessLogic(null);
   }
}
