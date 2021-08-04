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
      spark.get("/page/:id", (req, res) -> executor.submit(() -> this.getPage(req, res)).get());
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
         handlerMap.put(OrderBuilt.class, this::handleOrderBuilt);
         handlerMap.put(CustomerBuilt.class, this::handleCustomerBuilt);
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
      // no fulib
      OrderRegistered event = (OrderRegistered) e;
      handleDemoOrderRegistered(event);
   }

   private void handleOrderPicked(Event e)
   {
      // no fulib
      OrderPicked event = (OrderPicked) e;
      handleDemoOrderPicked(event);
   }

   private void handleOrderApproved(Event e)
   {
      // no fulib
      OrderApproved event = (OrderApproved) e;
      handleDemoOrderApproved(event);
   }

   private void handleOrderDeclined(Event e)
   {
      // no fulib
      OrderDeclined event = (OrderDeclined) e;
      handleDemoOrderDeclined(event);
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

   private void handleDemoOrderRegistered(OrderRegistered event)
   {
      if (event.getId().equals("13:01")) {
         OrderBuilt order1300Event = new OrderBuilt();
         order1300Event.setId("13:02");
         order1300Event.setBlockId("order1300");
         order1300Event.setProduct("shoes");
         order1300Event.setCustomer("Alice");
         order1300Event.setAddress("Wonderland 1");
         order1300Event.setState("pending");
         apply(order1300Event);

         CustomerBuilt aliceEvent = new CustomerBuilt();
         aliceEvent.setId("13:03");
         aliceEvent.setBlockId("Alice");
         aliceEvent.setOrders("[ order1300 ]");
         apply(aliceEvent);

      }
      if (event.getId().equals("13:11")) {
         OrderBuilt order1310Event = new OrderBuilt();
         order1310Event.setId("13:12");
         order1310Event.setBlockId("order1310");
         order1310Event.setProduct("tshirt");
         order1310Event.setCustomer("Alice");
         order1310Event.setAddress("Wonderland 1");
         order1310Event.setState("pending");
         apply(order1310Event);

         CustomerBuilt aliceEvent = new CustomerBuilt();
         aliceEvent.setId("13:13");
         aliceEvent.setBlockId("Alice");
         aliceEvent.setOrders("[ order1300 order1310 ]");
         apply(aliceEvent);

      }
   }

   private void handleDemoOrderApproved(OrderApproved event)
   {
      if (event.getId().equals("13:05")) {
         OrderBuilt order1300Event = new OrderBuilt();
         order1300Event.setId("13:06");
         order1300Event.setBlockId("order1300");
         order1300Event.setState("picking");
         apply(order1300Event);

      }
   }

   private void handleDemoOrderPicked(OrderPicked event)
   {
      if (event.getId().equals("14:00")) {
         OrderBuilt order1300Event = new OrderBuilt();
         order1300Event.setId("14:03");
         order1300Event.setBlockId("order1300");
         order1300Event.setState("shipping");
         apply(order1300Event);

      }
   }

   private void handleDemoOrderDeclined(OrderDeclined event)
   {
      if (event.getId().equals("13:14")) {
         OrderBuilt order1310Event = new OrderBuilt();
         order1310Event.setId("13:12");
         order1310Event.setBlockId("order1310");
         order1310Event.setState("out of stock");
         apply(order1310Event);

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

   private void handleOrderBuilt(Event e)
   {
      OrderBuilt event = (OrderBuilt) e;
      Order object = model.getOrCreateOrder(event.getBlockId());
      object.setProduct(event.getProduct());
      object.setCustomer(event.getCustomer());
      object.setAddress(event.getAddress());
      object.setState(event.getState());
   }

   private void handleCustomerBuilt(Event e)
   {
      CustomerBuilt event = (CustomerBuilt) e;
      Customer object = model.getOrCreateCustomer(event.getBlockId());
      object.setOrders(event.getOrders());
   }
}
