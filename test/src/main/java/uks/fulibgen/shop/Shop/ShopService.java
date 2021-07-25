package uks.fulibgen.shop.Shop;
import java.util.LinkedHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.fulib.yaml.Yaml;
import spark.Request;
import spark.Response;
import spark.Service;
import uks.fulibgen.shop.events.*;
import java.util.Objects;
import java.beans.PropertyChangeSupport;
import java.util.logging.Logger;
import java.util.logging.Level;
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
   public static final String PROPERTY_HANDLER_MAP = "handlerMap";
   private LinkedHashMap<String, Event> history = new LinkedHashMap<>();
   private int port = 42100;
   private Service spark;
   private ShopModel model;
   protected PropertyChangeSupport listeners;
   private LinkedHashMap<Class, Consumer<Event>> handlerMap;

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

   public LinkedHashMap<Class, Consumer<Event>> getHandlerMap()
   {
      return this.handlerMap;
   }

   public ShopService setHandlerMap(LinkedHashMap<Class, Consumer<Event>> value)
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
      model = new ShopModel();
      ExecutorService executor = Executors.newSingleThreadExecutor();
      spark = Service.ignite();
      spark.port(port);
      spark.get("/", (req, res) -> executor.submit(() -> this.getHello(req, res)).get());
      spark.post("/apply", (req, res) -> executor.submit(() -> this.postApply(req, res)).get());
      executor.submit(this::subscribeAndLoadOldEvents);
      Logger.getGlobal().info("Shop service is up and running on port " + port);
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
         handlerMap.put(OrderRegistered.class, this::handleOrderRegistered);
         handlerMap.put(OrderApproved.class, this::handleOrderApproved);
         handlerMap.put(OrderPicked.class, this::handleOrderPicked);
         handlerMap.put(OrderDeclined.class, this::handleOrderDeclined);
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

   private void handleOrderRegistered(Event e)
   {
      OrderRegistered event = (OrderRegistered) e;
      if (event.getId().equals("13:01")) {

         Order order1300 = model.getOrCreateOrder("order1300");
         order1300.setProduct("shoes");
         order1300.setCustomer("Alice");
         order1300.setAddress("Wonderland 1");
         order1300.setState("pending");

         Customer alice = model.getOrCreateCustomer("Alice");
         alice.setOrders("[order1300]");
      }
      if (event.getId().equals("13:11")) {

         Order order1310 = model.getOrCreateOrder("order1310");
         order1310.setProduct("tshirt");
         order1310.setCustomer("Alice");
         order1310.setAddress("Wonderland 1");
         order1310.setState("pending");

         Customer alice = model.getOrCreateCustomer("Alice");
         alice.setOrders("[order1300 order1310]");
      }
   }

   private void handleOrderPicked(Event e)
   {
      OrderPicked event = (OrderPicked) e;
      if (event.getId().equals("14:00")) {

         Order order1300 = model.getOrCreateOrder("order1300");
         order1300.setState("shipping");
      }
   }

   private void handleOrderApproved(Event e)
   {
      OrderApproved event = (OrderApproved) e;
      if (event.getId().equals("13:05")) {

         Order order1300 = model.getOrCreateOrder("order1300");
         order1300.setState("picking");
      }
   }

   private void handleOrderDeclined(Event e)
   {
      OrderDeclined event = (OrderDeclined) e;
      if (event.getId().equals("13:14")) {

         Order order1310 = model.getOrCreateOrder("order1310");
         order1310.setState("out of stock");
      }
   }
}
