package uks.debuggen.microshop.MicroShop;
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
import uks.debuggen.microshop.events.*;
import java.util.Objects;
import java.beans.PropertyChangeSupport;

public class MicroShopService
{
   public static final String PROPERTY_HISTORY = "history";
   public static final String PROPERTY_PORT = "port";
   public static final String PROPERTY_SPARK = "spark";
   public static final String PROPERTY_MODEL = "model";
   public static final String PROPERTY_BUSINESS_LOGIC = "businessLogic";
   public static final String PROPERTY_BUILDER = "builder";
   private LinkedHashMap<String, Event> history = new LinkedHashMap<>();
   private int port = 42002;
   private Service spark;
   private MicroShopModel model;
   private MicroShopBusinessLogic businessLogic;
   private MicroShopBuilder builder;
   protected PropertyChangeSupport listeners;

   public LinkedHashMap<String, Event> getHistory()
   {
      return this.history;
   }

   public MicroShopService setHistory(LinkedHashMap<String, Event> value)
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

   public MicroShopService setPort(int value)
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

   public MicroShopService setSpark(Service value)
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

   public MicroShopModel getModel()
   {
      return this.model;
   }

   public MicroShopService setModel(MicroShopModel value)
   {
      if (Objects.equals(value, this.model))
      {
         return this;
      }

      final MicroShopModel oldValue = this.model;
      this.model = value;
      this.firePropertyChange(PROPERTY_MODEL, oldValue, value);
      return this;
   }

   public MicroShopBusinessLogic getBusinessLogic()
   {
      return this.businessLogic;
   }

   public MicroShopService setBusinessLogic(MicroShopBusinessLogic value)
   {
      if (this.businessLogic == value)
      {
         return this;
      }

      final MicroShopBusinessLogic oldValue = this.businessLogic;
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

   public MicroShopBuilder getBuilder()
   {
      return this.builder;
   }

   public MicroShopService setBuilder(MicroShopBuilder value)
   {
      if (this.builder == value)
      {
         return this;
      }

      final MicroShopBuilder oldValue = this.builder;
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
      model = new MicroShopModel();
      setBuilder(new MicroShopBuilder().setModel(model));
      setBusinessLogic(new MicroShopBusinessLogic());
      businessLogic.setBuilder(getBuilder());
      businessLogic.setModel(model);
      ExecutorService executor = Executors.newSingleThreadExecutor();
      spark = Service.ignite();
      spark.port(port);
      spark.get("/page/:id", (req, res) -> executor.submit(() -> this.getPage(req, res)).get());
      spark.get("/", (req, res) -> executor.submit(() -> this.getHello(req, res)).get());
      spark.post("/apply", (req, res) -> executor.submit(() -> this.postApply(req, res)).get());
      spark.init();
      executor.submit(this::subscribeAndLoadOldEvents);
      Logger.getGlobal().info("MicroShop service is up and running on port " + port);
   }

   public void stop()
   {
      spark.stop();
   }

   private String getHello(Request req, Response res)
   {
      try {
         String events = Yaml.encodeSimple(getHistory().values().toArray());
         String objects = Yaml.encodeSimple(model.getModelMap().values().toArray());
         return "<p id='MicroShop'>This is the MicroShop service. </p>\n" +
               "<pre id=\"history\">" + events + "</pre>\n" +
               "<pre id=\"data\">" + objects + "</pre>\n" +
               "";
      }
      catch (Exception e) {
         Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
         return "MicroShop Error " + e.getMessage();
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

      if ("add offer 12:14:42".equals(event)) {

         // create AddOfferCommand: add offer 12:14:42
         AddOfferCommand e121442 = new AddOfferCommand();
         e121442.setId("12:14:42");
         e121442.setProduct(request.queryParams("product"));
         e121442.setPrice(request.queryParams("price"));
         apply(e121442);
      }

      if ("add offer".equals(event)) {

         // create AddCommand: add offer
         AddCommand e121701 = new AddCommand();
         e121701.setId("12:17:01");
         e121701.setProduct(request.queryParams("product"));
         e121701.setPrice(request.queryParams("price"));
         apply(e121701);
      }

      if ("place order".equals(event)) {

         // create PlaceCommand: place order
         PlaceCommand e122101 = new PlaceCommand();
         e122101.setId("12:21:01");
         e122101.setProduct(request.queryParams("product"));
         e122101.setCustomer(request.queryParams("customer"));
         e122101.setAddress(request.queryParams("address"));
         apply(e122101);
      }



      // 12:12
      if (id.equals("12_12")) {
         html.append("<form action=\"/page/12_13\" method=\"get\">\n");
         // MicroShop offers 12:12
         html.append("   <p>Offers overview</p>\n");
         html.append("   <p><input id=\"add\" name=\"button\" type=\"submit\" value=\"add\"></p>\n");
         html.append("   <p>no offers yet</p>\n");
         html.append("</form>\n");
         return html.toString();
      }

      // 12:13
      if (id.equals("12_13")) {
         html.append("<form action=\"/page/12_16\" method=\"get\">\n");
         // MicroShop add offer 12:13
         html.append("   <p>make new offer</p>\n");
         html.append("   <p><input id=\"product\" name=\"product\" placeholder=\"product?\"></p>\n");
         html.append("   <p><input id=\"price\" name=\"price\" placeholder=\"price?\"></p>\n");
         html.append("   <p><input id=\"event\" name=\"event\" type=\"hidden\" value=\"add offer 12:14:42\"></p>\n");
         html.append("   <p><input id=\"ok\" name=\"button\" type=\"submit\" value=\"ok\"></p>\n");
         html.append("</form>\n");
         return html.toString();
      }

      // 12:16
      if (id.equals("12_16")) {
         html.append("<form action=\"/page/12_17\" method=\"get\">\n");
         // MicroShop offers 12:16
         html.append("   <p>Offers overview</p>\n");
         html.append("   <p><input id=\"add\" name=\"button\" type=\"submit\" value=\"add\"></p>\n");
         html.append("   <p>red_shoes, $42</p>\n");
         html.append("</form>\n");
         return html.toString();
      }

      // 12:17
      if (id.equals("12_17")) {
         html.append("<form action=\"/page/12_19\" method=\"get\">\n");
         // MicroShop add offer 12:17
         html.append("   <p>make new offer</p>\n");
         html.append("   <p><input id=\"product\" name=\"product\" placeholder=\"product?\"></p>\n");
         html.append("   <p><input id=\"price\" name=\"price\" placeholder=\"price?\"></p>\n");
         html.append("   <p><input id=\"event\" name=\"event\" type=\"hidden\" value=\"add offer\"></p>\n");
         html.append("   <p><input id=\"ok\" name=\"button\" type=\"submit\" value=\"ok\"></p>\n");
         html.append("</form>\n");
         return html.toString();
      }

      // 12:19
      if (id.equals("12_19")) {
         html.append("<form action=\"/page/next_page\" method=\"get\">\n");
         // MicroShop offers 12:19
         html.append("   <p>Offers overview</p>\n");
         html.append("   <p><input id=\"add\" name=\"button\" type=\"submit\" value=\"add\"></p>\n");
         html.append("   <p>red_shoes, $42</p>\n");
         html.append("   <p>blue_jeans, $63</p>\n");
         html.append("</form>\n");
         return html.toString();
      }

      // 12:20
      if (id.equals("12_20")) {
         html.append("<form action=\"/page/12_21\" method=\"get\">\n");
         // MicroShop offers 12:20
         html.append("   <p>Welcome to our micro shop</p>\n");
         html.append("   <p>We have</p>\n");
         html.append("   <p>red_shoes for $42</p>\n");
         html.append("   <p>blue_jeans for $63</p>\n");
         html.append("   <p><input id=\"order\" name=\"button\" type=\"submit\" value=\"order\"></p>\n");
         html.append("</form>\n");
         return html.toString();
      }

      // 12:21
      if (id.equals("12_21")) {
         html.append("<form action=\"/page/12_24\" method=\"get\">\n");
         // MicroShop buy 12:21
         html.append("   <p>Welcome to our micro shop</p>\n");
         html.append("   <p><input id=\"product\" name=\"product\" placeholder=\"product?\"></p>\n");
         html.append("   <p><input id=\"customer\" name=\"customer\" placeholder=\"customer?\"></p>\n");
         html.append("   <p><input id=\"address\" name=\"address\" placeholder=\"address?\"></p>\n");
         html.append("   <p><input id=\"event\" name=\"event\" type=\"hidden\" value=\"place order\"></p>\n");
         html.append("   <p><input id=\"buy\" name=\"button\" type=\"submit\" value=\"buy\"></p>\n");
         html.append("</form>\n");
         return html.toString();
      }

      // 12:24
      if (id.equals("12_24")) {
         html.append("<form action=\"/page/next_page\" method=\"get\">\n");
         // MicroShop Carli overview 12:24
         html.append("   <p>Welcome Carli</p>\n");
         html.append("   <p>Your orders are</p>\n");
         html.append("   <p>red_shoes for $42, picking</p>\n");
         html.append("   <p><input id=\"order\" name=\"button\" type=\"submit\" value=\"order\"></p>\n");
         html.append("</form>\n");
         return html.toString();
      }



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
}
