package uks.debuggen.shop.Storage;
import uks.debuggen.shop.events.Event;

import java.util.LinkedHashMap;
import java.util.Objects;
import java.beans.PropertyChangeSupport;
import uks.debuggen.shop.events.*;
import spark.Service;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import spark.Request;
import spark.Response;
import org.fulib.yaml.Yaml;
import java.util.logging.Logger;
import java.util.logging.Level;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.util.Map;
import java.util.function.Consumer;

public class StorageService
{
   public static final String PROPERTY_HISTORY = "history";
   public static final String PROPERTY_PORT = "port";
   public static final String PROPERTY_SPARK = "spark";
   public static final String PROPERTY_MODEL = "model";
   public static final String PROPERTY_HANDLER_MAP = "handlerMap";
   private LinkedHashMap<String, Event> history = new LinkedHashMap<>();
   protected PropertyChangeSupport listeners;
   private int port = 42003;
   private Service spark;
   private StorageModel model;
   private LinkedHashMap<Class, Consumer<Event>> handlerMap;

   public LinkedHashMap<String, Event> getHistory()
   {
      return this.history;
   }

   public StorageService setHistory(LinkedHashMap<String, Event> value)
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

   public StorageService setPort(int value)
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

   public StorageService setSpark(Service value)
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

   public StorageModel getModel()
   {
      return this.model;
   }

   public StorageService setModel(StorageModel value)
   {
      if (Objects.equals(value, this.model))
      {
         return this;
      }

      final StorageModel oldValue = this.model;
      this.model = value;
      this.firePropertyChange(PROPERTY_MODEL, oldValue, value);
      return this;
   }

   public LinkedHashMap<Class, Consumer<Event>> getHandlerMap()
   {
      return this.handlerMap;
   }

   public StorageService setHandlerMap(LinkedHashMap<Class, Consumer<Event>> value)
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
      model = new StorageModel();
      ExecutorService executor = Executors.newSingleThreadExecutor();
      spark = Service.ignite();
      spark.port(port);
      spark.get("/page/:id", (req, res) -> executor.submit(() -> this.getPage(req, res)).get());
      spark.get("/", (req, res) -> executor.submit(() -> this.getHello(req, res)).get());
      spark.post("/apply", (req, res) -> executor.submit(() -> this.postApply(req, res)).get());
      executor.submit(this::subscribeAndLoadOldEvents);
      Logger.getGlobal().info("Storage service is up and running on port " + port);
   }

   private String getHello(Request req, Response res)
   {
      try {
         String events = Yaml.encode(getHistory().values().toArray());
         String objects = Yaml.encode(model.getModelMap().values().toArray());
         return "<p id='Storage'>This is the Storage service. </p>\n" +
               "<pre id=\"history\">" + events + "</pre>\n" +
               "<pre id=\"data\">" + objects + "</pre>\n" +
               "";
      }
      catch (Exception e) {
         Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
         return "Storage Error " + e.getMessage();
      }
   }

   private void subscribeAndLoadOldEvents()
   {
      ServiceSubscribed serviceSubscribed = new ServiceSubscribed()
            .setServiceUrl("http://localhost:42003/apply");
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

   private void initEventHandlerMap()
   {
      if (handlerMap == null) {
         handlerMap = new LinkedHashMap<>();
         handlerMap.put(ProductStored.class, this::handleProductStored);
         handlerMap.put(OrderRegistered.class, this::handleOrderRegistered);
         handlerMap.put(OrderPicked.class, this::handleOrderPicked);
         handlerMap.put(BoxBuilt.class, this::handleBoxBuilt);
         handlerMap.put(PickTaskBuilt.class, this::handlePickTaskBuilt);
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

   private void handleOrderPicked(Event e)
   {
      // no fulib
      OrderPicked event = (OrderPicked) e;
      handleDemoOrderPicked(event);
   }

   private void handleOrderRegistered(Event e)
   {
      // no fulib
      OrderRegistered event = (OrderRegistered) e;
      handleDemoOrderRegistered(event);
   }

   private void handleProductStored(Event e)
   {
      // no fulib
      ProductStored event = (ProductStored) e;
      handleDemoProductStored(event);
   }

   public String getPage(Request request, Response response)
   {
      // no fulib
      // add your page handling here
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

   private void handleDemoProductStored(ProductStored event)
   {
      if (event.getId().equals("12:00")) {
         BoxBuilt box23Event = new BoxBuilt();
         box23Event.setId("12:02");
         box23Event.setBlockId("box23");
         box23Event.setProduct("shoes");
         box23Event.setPlace("shelf23");
         apply(box23Event);

      }
   }

   private void handleDemoOrderRegistered(OrderRegistered event)
   {
      if (event.getId().equals("13:01")) {
         PickTaskBuilt pick1300Event = new PickTaskBuilt();
         pick1300Event.setId("13:04");
         pick1300Event.setBlockId("pick1300");
         pick1300Event.setOrder("order1300");
         pick1300Event.setProduct("shoes");
         pick1300Event.setCustomer("Alice");
         pick1300Event.setAddress("Wonderland 1");
         pick1300Event.setState("todo");
         apply(pick1300Event);


         OrderApproved e1305 = new OrderApproved();

         e1305.setId("13:05");
         e1305.setEvent("order approved 13:05");
         e1305.setOrder("order1300");
         apply(e1305);
      }
      if (event.getId().equals("13:11")) {

         OrderDeclined e1314 = new OrderDeclined();

         e1314.setId("13:14");
         e1314.setEvent("order declined 13:14");
         e1314.setOrder("order1310");
         apply(e1314);
      }
   }

   private void handleDemoOrderPicked(OrderPicked event)
   {
      if (event.getId().equals("14:00")) {
         PickTaskBuilt pick1300Event = new PickTaskBuilt();
         pick1300Event.setId("14:01");
         pick1300Event.setBlockId("pick1300");
         pick1300Event.setState("done");
         pick1300Event.setBox("box23");
         apply(pick1300Event);

         BoxBuilt box23Event = new BoxBuilt();
         box23Event.setId("14:02");
         box23Event.setBlockId("box23");
         box23Event.setPlace("shipping");
         apply(box23Event);

      }
   }

   private void handleBoxBuilt(Event e)
   {
      BoxBuilt event = (BoxBuilt) e;
      Box object = model.getOrCreateBox(event.getBlockId());
      object.setProduct(event.getProduct());
      object.setPlace(event.getPlace());
   }

   private void handlePickTaskBuilt(Event e)
   {
      PickTaskBuilt event = (PickTaskBuilt) e;
      PickTask object = model.getOrCreatePickTask(event.getBlockId());
      object.setOrder(event.getOrder());
      object.setProduct(event.getProduct());
      object.setCustomer(event.getCustomer());
      object.setAddress(event.getAddress());
      object.setState(event.getState());
      object.setBox(event.getBox());
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
