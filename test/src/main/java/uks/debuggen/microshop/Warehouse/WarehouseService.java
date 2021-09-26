package uks.debuggen.microshop.Warehouse;
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

public class WarehouseService
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
   private WarehouseModel model;
   private WarehouseBusinessLogic businessLogic;
   private WarehouseBuilder builder;
   protected PropertyChangeSupport listeners;

   public LinkedHashMap<String, Event> getHistory()
   {
      return this.history;
   }

   public WarehouseService setHistory(LinkedHashMap<String, Event> value)
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

   public WarehouseService setPort(int value)
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

   public WarehouseService setSpark(Service value)
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

   public WarehouseModel getModel()
   {
      return this.model;
   }

   public WarehouseService setModel(WarehouseModel value)
   {
      if (Objects.equals(value, this.model))
      {
         return this;
      }

      final WarehouseModel oldValue = this.model;
      this.model = value;
      this.firePropertyChange(PROPERTY_MODEL, oldValue, value);
      return this;
   }

   public WarehouseBusinessLogic getBusinessLogic()
   {
      return this.businessLogic;
   }

   public WarehouseService setBusinessLogic(WarehouseBusinessLogic value)
   {
      if (this.businessLogic == value)
      {
         return this;
      }

      final WarehouseBusinessLogic oldValue = this.businessLogic;
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

   public WarehouseBuilder getBuilder()
   {
      return this.builder;
   }

   public WarehouseService setBuilder(WarehouseBuilder value)
   {
      if (this.builder == value)
      {
         return this;
      }

      final WarehouseBuilder oldValue = this.builder;
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
      model = new WarehouseModel();
      setBuilder(new WarehouseBuilder().setModel(model));
      setBusinessLogic(new WarehouseBusinessLogic());
      businessLogic.setBuilder(getBuilder());
      businessLogic.setModel(model);
      ExecutorService executor = Executors.newSingleThreadExecutor();
      spark = Service.ignite();
      spark.port(port);
      spark.get("/page/:id", (req, res) -> executor.submit(() -> this.getPage(req, res)).get());
      spark.get("/", (req, res) -> executor.submit(() -> this.getHello(req, res)).get());
      spark.post("/apply", (req, res) -> executor.submit(() -> this.postApply(req, res)).get());
      executor.submit(this::subscribeAndLoadOldEvents);
      Logger.getGlobal().info("Warehouse service is up and running on port " + port);
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
         return "<p id='Warehouse'>This is the Warehouse service. </p>\n" +
               "<pre id=\"history\">" + events + "</pre>\n" +
               "<pre id=\"data\">" + objects + "</pre>\n" +
               "";
      }
      catch (Exception e) {
         Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
         return "Warehouse Error " + e.getMessage();
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

      if ("store product".equals(event)) {

         // create StoreCommand: store product
         StoreCommand e120201 = new StoreCommand();
         e120201.setId("12:02:01");
         e120201.setBarcode(request.queryParams("barcode"));
         e120201.setType(request.queryParams("type"));
         e120201.setLocation(request.queryParams("location"));
         apply(e120201);
      }

      if ("store product".equals(event)) {

         // create StoreCommand: store product
         StoreCommand e120501 = new StoreCommand();
         e120501.setId("12:05:01");
         e120501.setBarcode(request.queryParams("barcode"));
         e120501.setType(request.queryParams("type"));
         e120501.setLocation(request.queryParams("location"));
         apply(e120501);
      }

      if ("store product".equals(event)) {

         // create StoreCommand: store product
         StoreCommand e120801 = new StoreCommand();
         e120801.setId("12:08:01");
         e120801.setBarcode(request.queryParams("barcode"));
         e120801.setType(request.queryParams("type"));
         e120801.setLocation(request.queryParams("location"));
         apply(e120801);
      }

      if ("Pick".equals(event)) {

         // create Command: Pick
         Command e122601 = new Command();
         e122601.setId("12:26:01");
         e122601.setTask(request.queryParams("task"));
         e122601.setShelf(request.queryParams("shelf"));
         apply(e122601);
      }

      if ("deliver order".equals(event)) {

         // create DeliverCommand: deliver order
         DeliverCommand e123001 = new DeliverCommand();
         e123001.setId("12:30:01");
         e123001.setOrder(request.queryParams("order"));
         apply(e123001);
      }



      // 12:01
      if (id.equals("12_01")) {
         html.append("<form action=\"/page/12_02\" method=\"get\">\n");
         // Warehouse overview 12:01
         html.append("   <p>Warehouse boxes</p>\n");
         html.append("   <p><input id=\"add\" name=\"button\" type=\"submit\" value=\"add\"></p>\n");
         html.append("</form>\n");
         return html.toString();
      }

      // 12:02
      if (id.equals("12_02")) {
         html.append("<form action=\"/page/12_04\" method=\"get\">\n");
         // Warehouse add box 12:02
         html.append("   <p>Store new box</p>\n");
         html.append("   <p><input id=\"barcode\" name=\"barcode\" placeholder=\"barcode?\"></p>\n");
         html.append("   <p><input id=\"type\" name=\"type\" placeholder=\"type?\"></p>\n");
         html.append("   <p><input id=\"location\" name=\"location\" placeholder=\"location?\"></p>\n");
         html.append("   <p><input id=\"event\" name=\"event\" type=\"hidden\" value=\"store product\"></p>\n");
         html.append("   <p><input id=\"ok\" name=\"button\" type=\"submit\" value=\"ok\"></p>\n");
         html.append("</form>\n");
         return html.toString();
      }

      // 12:04
      if (id.equals("12_04")) {
         html.append("<form action=\"/page/12_05\" method=\"get\">\n");
         // Warehouse overview 12:04
         html.append("   <p>Warehouse boxes</p>\n");
         html.append("   <p><input id=\"add\" name=\"button\" type=\"submit\" value=\"add\"></p>\n");
         html.append("   <p>b001, red shoes, shelf 42</p>\n");
         html.append("</form>\n");
         return html.toString();
      }

      // 12:05
      if (id.equals("12_05")) {
         html.append("<form action=\"/page/12_07\" method=\"get\">\n");
         // Warehouse add box 12:05
         html.append("   <p>Store new box</p>\n");
         html.append("   <p><input id=\"barcode\" name=\"barcode\" placeholder=\"barcode?\"></p>\n");
         html.append("   <p><input id=\"type\" name=\"type\" placeholder=\"type?\"></p>\n");
         html.append("   <p><input id=\"location\" name=\"location\" placeholder=\"location?\"></p>\n");
         html.append("   <p><input id=\"event\" name=\"event\" type=\"hidden\" value=\"store product\"></p>\n");
         html.append("   <p><input id=\"ok\" name=\"button\" type=\"submit\" value=\"ok\"></p>\n");
         html.append("</form>\n");
         return html.toString();
      }

      // 12:07
      if (id.equals("12_07")) {
         html.append("<form action=\"/page/12_08\" method=\"get\">\n");
         // Warehouse overview 12:07
         html.append("   <p>Warehouse boxes</p>\n");
         html.append("   <p><input id=\"add\" name=\"button\" type=\"submit\" value=\"add\"></p>\n");
         html.append("   <p>b002, red shoes, shelf 23</p>\n");
         html.append("   <p>b001, red shoes, shelf 42</p>\n");
         html.append("</form>\n");
         return html.toString();
      }

      // 12:08
      if (id.equals("12_08")) {
         html.append("<form action=\"/page/12_10\" method=\"get\">\n");
         // Warehouse add box 12:08
         html.append("   <p>Store new box</p>\n");
         html.append("   <p><input id=\"barcode\" name=\"barcode\" placeholder=\"barcode?\"></p>\n");
         html.append("   <p><input id=\"type\" name=\"type\" placeholder=\"type?\"></p>\n");
         html.append("   <p><input id=\"location\" name=\"location\" placeholder=\"location?\"></p>\n");
         html.append("   <p><input id=\"event\" name=\"event\" type=\"hidden\" value=\"store product\"></p>\n");
         html.append("   <p><input id=\"ok\" name=\"button\" type=\"submit\" value=\"ok\"></p>\n");
         html.append("</form>\n");
         return html.toString();
      }

      // 12:10
      if (id.equals("12_10")) {
         html.append("<form action=\"/page/12_25\" method=\"get\">\n");
         // Warehouse overview 12:10
         html.append("   <p>Warehouse boxes</p>\n");
         html.append("   <p><input id=\"add\" name=\"button\" type=\"submit\" value=\"add\"></p>\n");
         html.append("   <p>b003, blue jeans, shelf 1337</p>\n");
         html.append("   <p>b002, red shoes, shelf 23</p>\n");
         html.append("   <p>b001, red shoes, shelf 42</p>\n");
         html.append("</form>\n");
         return html.toString();
      }

      // 12:25
      if (id.equals("12_25")) {
         html.append("<form action=\"/page/12_26\" method=\"get\">\n");
         // Warehouse pick tasks 12:25
         html.append("   <p>Pick tasks overview</p>\n");
         html.append("   <p><input id=\"pt_o0925_1, red shoes, shelf 42, shelf 23\" name=\"button\" type=\"submit\" value=\"pt_o0925_1, red shoes, shelf 42, shelf 23\"></p>\n");
         html.append("</form>\n");
         return html.toString();
      }

      // 12:26
      if (id.equals("12_26")) {
         html.append("<form action=\"/page/12_28\" method=\"get\">\n");
         // Warehouse pick one 12:26
         html.append("   <p>Pick one</p>\n");
         html.append("   <p><input id=\"task\" name=\"task\" placeholder=\"task?\"></p>\n");
         html.append("   <p><input id=\"shelf\" name=\"shelf\" placeholder=\"shelf?\"></p>\n");
         html.append("   <p><input id=\"event\" name=\"event\" type=\"hidden\" value=\"Pick\"></p>\n");
         html.append("   <p><input id=\"done\" name=\"button\" type=\"submit\" value=\"done\"></p>\n");
         html.append("</form>\n");
         return html.toString();
      }

      // 12:28
      if (id.equals("12_28")) {
         html.append("<form action=\"/page/next_page\" method=\"get\">\n");
         // Warehouse pick tasks 12:28
         html.append("   <p>Pick tasks overview</p>\n");
         html.append("   <p>no tasks, have a break</p>\n");
         html.append("</form>\n");
         return html.toString();
      }

      // 12:29
      if (id.equals("12_29")) {
         html.append("<form action=\"/page/12_30\" method=\"get\">\n");
         // Warehouse delivery tasks 12:29
         html.append("   <p>Delivery tasks overview</p>\n");
         html.append("   <p><input id=\"red shoes, Wonderland 1\" name=\"button\" type=\"submit\" value=\"red shoes, Wonderland 1\"></p>\n");
         html.append("</form>\n");
         return html.toString();
      }

      // 12:30
      if (id.equals("12_30")) {
         html.append("<form action=\"/page/12_32\" method=\"get\">\n");
         // Warehouse deliver 12:30
         html.append("   <p>Delivering</p>\n");
         html.append("   <p><input id=\"order\" name=\"order\" placeholder=\"order?\"></p>\n");
         html.append("   <p><input id=\"event\" name=\"event\" type=\"hidden\" value=\"deliver order\"></p>\n");
         html.append("   <p><input id=\"done\" name=\"button\" type=\"submit\" value=\"done\"></p>\n");
         html.append("</form>\n");
         return html.toString();
      }

      // 12:32
      if (id.equals("12_32")) {
         html.append("<form action=\"/page/next_page\" method=\"get\">\n");
         // Warehouse delivery tasks 12:32
         html.append("   <p>Delivery tasks overview</p>\n");
         html.append("   <p>everything delivered, you are a hero</p>\n");
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
