package uks.debuggen.party2.PartyApp;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.fulib.yaml.Yaml;
import org.fulib.yaml.YamlIdMap;
import spark.Request;
import spark.Response;
import spark.Service;
import uks.debuggen.party2.events.*;

import java.beans.PropertyChangeSupport;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class PartyAppService
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
   private PartyAppModel model;
   private PartyAppBusinessLogic businessLogic;
   protected PropertyChangeSupport listeners;
   private PartyAppBuilder builder;

   public LinkedHashMap<String, Event> getHistory()
   {
      return this.history;
   }

   public PartyAppService setHistory(LinkedHashMap<String, Event> value)
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

   public PartyAppService setPort(int value)
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

   public PartyAppService setSpark(Service value)
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

   public PartyAppModel getModel()
   {
      return this.model;
   }

   public PartyAppService setModel(PartyAppModel value)
   {
      if (Objects.equals(value, this.model))
      {
         return this;
      }

      final PartyAppModel oldValue = this.model;
      this.model = value;
      this.firePropertyChange(PROPERTY_MODEL, oldValue, value);
      return this;
   }

   public PartyAppBusinessLogic getBusinessLogic()
   {
      return this.businessLogic;
   }

   public PartyAppService setBusinessLogic(PartyAppBusinessLogic value)
   {
      if (this.businessLogic == value)
      {
         return this;
      }

      final PartyAppBusinessLogic oldValue = this.businessLogic;
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

   public PartyAppBuilder getBuilder()
   {
      return this.builder;
   }

   public PartyAppService setBuilder(PartyAppBuilder value)
   {
      if (this.builder == value)
      {
         return this;
      }

      final PartyAppBuilder oldValue = this.builder;
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
      model = new PartyAppModel();
      setBuilder(new PartyAppBuilder().setModel(model));
      setBusinessLogic(new PartyAppBusinessLogic());
      businessLogic.setBuilder(getBuilder());
      businessLogic.setModel(model);
      ExecutorService executor = Executors.newSingleThreadExecutor();
      spark = Service.ignite();
      spark.port(port);
      spark.get("/page/:id", (req, res) -> executor.submit(() -> this.getPage(req, res)).get());
      spark.get("/", (req, res) -> executor.submit(() -> this.getHello(req, res)).get());
      spark.post("/apply", (req, res) -> executor.submit(() -> this.postApply(req, res)).get());
      executor.submit(this::subscribeAndLoadOldEvents);
      Logger.getGlobal().info("PartyApp service is up and running on port " + port);
   }

   private String getHello(Request req, Response res)
   {
      try {
         String events = Yaml.encodeSimple(getHistory().values().toArray());
         String objects = Yaml.encodeSimple(model.getModelMap().values().toArray());
         return "<p id='PartyApp'>This is the PartyApp service. </p>\n" +
               "<pre id=\"history\">" + events + "</pre>\n" +
               "<pre id=\"data\">" + objects + "</pre>\n" +
               "";
      }
      catch (Exception e) {
         Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
         return "PartyApp Error " + e.getMessage();
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

   public void apply(Event event)
   {
      if (history.get(event.getId()) != null) {
         return;
      }
      businessLogic.initEventHandlerMap();
      Consumer<Event> handler = businessLogic.getHandler(event);
      handler.accept(event);
      history.put(event.getId(), event);
      publish(event);
   }

   public String getPage(Request request, Response response)
   {
      // no fulib
      // add your page handling here
      try {

         String id = request.params("id");

         initPageHandlerMap();
         BiFunction<Request, Response, String> handler = pageHandlerMap.get(id);

         if (handler != null) {
            return handler.apply(request, response);
         }

         return getDemoPage(request, response);
      }
      catch (Exception e) {
         e.printStackTrace();
         return "exception " + e;
      }
   }

   LinkedHashMap<String, BiFunction<Request, Response, String>> pageHandlerMap = null;

   private void initPageHandlerMap()
   {
      if (pageHandlerMap == null) {
         pageHandlerMap = new LinkedHashMap<>();
         pageHandlerMap.put("getUserName", this::pageGetUserName);
         pageHandlerMap.put("withUserName", this::pageWithUserName);
         pageHandlerMap.put("getEmail", this::pageGetEmail);
         pageHandlerMap.put("withEmail", this::pageWithEmail);
         pageHandlerMap.put("withPassword", this::pageWithPassword);
         pageHandlerMap.put("withParty", this::pageWithParty);
         pageHandlerMap.put("addItem", this::pageAddItem);
         pageHandlerMap.put("withItem", this::pageWithItem);
      }
   }

   private String pageWithItem(Request request, Response response)
   {
      String name = request.queryParams("name");
      String sessionId = request.queryParams("sessionId");
      String partyName = request.queryParams("party");
      String item = request.queryParams("item");
      String price = request.queryParams("price");
      String buyer = request.queryParams("buyer");

      StringBuilder html = new StringBuilder();
      if (sessionId == null || !validSessionIds.contains(sessionId)) {
         html.append(pageGetUserName(request, response));
         html.append("Invalid session id, please login\n");
         return html.toString();
      }

      Query query = new Query().setKey(partyName);
      query(query);
      if (query.getResults().isEmpty() || !(query.getResults().get(0) instanceof PartyBuilt)) {
         return pageGetParty(request, response, sessionId);
      }

      ItemBuilt itemBuilt = new ItemBuilt();
      itemBuilt.setId(isoNow());
      String itemId = partyName + "#" + item;
      itemBuilt.setBlockId(itemId);
      itemBuilt.setName(item);
      itemBuilt.setPrice(price);
      itemBuilt.setParty(partyName);
      String buyerId = partyName + "#" + buyer;
      itemBuilt.setBuyer(buyerId);
      apply(itemBuilt);

      GuestBuilt guestBuilt = new GuestBuilt();
      guestBuilt.setId(DateTimeFormatter.ISO_INSTANT.format(Instant.ofEpochMilli(System.currentTimeMillis() + 1)));
      guestBuilt.setBlockId(buyerId);
      guestBuilt.setName(buyer);
      guestBuilt.setExpenses("0.00");
      guestBuilt.setParty(partyName);
      apply(guestBuilt);

      return pageGetOverview(request, response);
   }

   private String pageAddItem(Request request, Response response)
   {
      String name = request.queryParams("name");
      String sessionId = request.queryParams("sessionId");
      String partyName = request.queryParams("party");

      StringBuilder html = new StringBuilder();
      if (sessionId == null || !validSessionIds.contains(sessionId)) {
         html.append(pageGetUserName(request, response));
         html.append("Invalid session id, please login\n");
         return html.toString();
      }

      Query query = new Query().setKey(partyName);
      query(query);
      if (query.getResults().isEmpty() || !(query.getResults().get(0) instanceof PartyBuilt)) {
         return pageGetParty(request, response, sessionId);
      }

      PartyBuilt partyBuilt = (PartyBuilt) query.getResults().get(0);
      html.append("<form action=\"/page/withItem\" method=\"get\">\n");
      html.append(String.format("   <p>Welcome %s</p>\n", name));
      html.append(String.format("   <p>Let's do the %s</p>\n", partyBuilt.getName()));

      html.append("   <p><input id=\"item\" name=\"item\" placeholder=\"item?\"></p>\n");
      html.append("   <p><input id=\"price\" name=\"price\" placeholder=\"price?\"></p>\n");
      html.append("   <p><input id=\"buyer\" name=\"buyer\" placeholder=\"buyer?\"></p>\n");

      html.append(String.format("   <p><input id=\"name\" name=\"name\" type=\"hidden\" value=\"%s\"></p>\n", name));
      html.append(String.format("   <p><input id=\"sessionId\" name=\"sessionId\" type=\"hidden\" value=\"%s\"></p>\n", sessionId));
      html.append(String.format("   <p><input id=\"party\" name=\"party\" type=\"hidden\" value=\"%s\"></p>\n", partyBuilt.getName()));
      html.append("   <p><input id=\"ok\" name=\"button\" type=\"submit\" value=\"add\"></p>\n");
      html.append("</form>\n");

      return html.toString();
   }

   private String pageWithParty(Request request, Response response)
   {
      String name = request.queryParams("name");
      String sessionId = request.queryParams("sessionId");
      String partyName = request.queryParams("party");
      String location = request.queryParams("location");

      StringBuilder html = new StringBuilder();
      if (sessionId == null || !validSessionIds.contains(sessionId)) {
         html.append(pageGetUserName(request, response));
         html.append("Invalid session id, please login\n");
         return html.toString();
      }

      Query query = new Query().setKey(partyName);
      query(query);
      if (query.getResults().isEmpty()) {
         PartyBuilt partyBuilt = new PartyBuilt();
         partyBuilt.setId(isoNow());
         partyBuilt.setBlockId(partyName);
         partyBuilt.setName(partyName);
         partyBuilt.setLocation(location);
         apply(partyBuilt);
         return pageGetOverview(request, response);
      }

      if (!(query.getResults().get(0) instanceof PartyBuilt)) {
         html.append(pageGetParty(request, response, sessionId));
         html.append("invalid party name");
         return html.toString();
      }

      PartyBuilt partyBuilt = (PartyBuilt) query.getResults().get(0);
      if (partyBuilt.getLocation().equals(location)) {
         return pageGetOverview(request, response);
      }

      html.append(pageGetParty(request, response, sessionId));
      html.append("invalid location");
      return html.toString();
   }

   private String pageGetOverview(Request request, Response response)
   {
      String name = request.queryParams("name");
      String sessionId = request.queryParams("sessionId");
      String partyName = request.queryParams("party");

      Party party = model.getOrCreateParty(partyName);

      StringBuilder html = new StringBuilder();
      html.append("<form action=\"/page/addItem\" method=\"get\">\n");
      html.append(String.format("   <p>Welcome %s</p>\n", name));
      html.append(String.format("   <p>Let's do the %s</p>\n", party.getName()));

      if (party.getItems().isEmpty()) {
         html.append("   <p>no items yet</p>\n");
      }
      else {
         for (Guest guest : party.getGuests()) {
            guest.setExpenses("0.00");
         }
         // list items
         double total = 0.0;
         for (Item item : party.getItems()) {
            double p = toDouble(item.getPrice());
            total += p;
            Guest g = item.getBuyer();
            double x = toDouble(g.getExpenses()) + p;
            g.setExpenses(toString(x));
            html.append(String.format("   <p>%s %s %s</p>\n",
                  item.getName(), item.getPrice(), item.getBuyer().getName()));
         }

         html.append(String.format("   <p>total %s</p>\n", toString(total)));
         double share = total / party.getGuests().size();
         html.append(String.format("   <p>share %s</p>\n", toString(share)));

         for (Guest guest : party.getGuests()) {
            double saldo = toDouble(guest.getExpenses()) - share;
            html.append(String.format("   <p>%s %s</p>\n", guest.getName(), toString(saldo)));
         }

      }

      html.append(String.format("   <p><input id=\"name\" name=\"name\" type=\"hidden\" value=\"%s\"></p>\n", name));
      html.append(String.format("   <p><input id=\"sessionId\" name=\"sessionId\" type=\"hidden\" value=\"%s\"></p>\n", sessionId));
      html.append(String.format("   <p><input id=\"party\" name=\"party\" type=\"hidden\" value=\"%s\"></p>\n", party.getName()));
      html.append("   <p><input id=\"add\" name=\"button\" type=\"submit\" value=\"add\"></p>\n");
      html.append("</form>\n");

      return html.toString();
   }

   private double toDouble(String price)
   {
      return Double.parseDouble(price);
   }

   private String toString(double price)
   {
      String result = String.format(Locale.ENGLISH, "%.2f", price);
      return result;
   }

   private String isoNow()
   {
      return DateTimeFormatter.ISO_INSTANT.format(Instant.now());
   }

   private LinkedHashSet<String> validSessionIds = new LinkedHashSet<>();

   private String pageWithPassword(Request request, Response response)
   {
      StringBuilder html = new StringBuilder();

      String name = request.queryParams("name");
      String email = request.queryParams("email");
      String password = request.queryParams("password");
      String button = request.queryParams("button");

      if (button.equals("change password")) {
         return pageGetEmail(request, response);
      }


      if (email.equals("null")) {
         // check password
         Query query = new Query().setKey(name);
         query(query);

         if (query.getResults().isEmpty() || !(query.getResults().get(0) instanceof UserBuilt)) {
            return pageGetEmail(request, response);
         }

         UserBuilt user = (UserBuilt) query.getResults().get(0);
         if (!user.getPassword().equals(password)) {
            html.append(pageGetPassword(request, response));
            html.append("Please try again");
            return html.toString();
         }

         String sessionId = name + "#" + isoNow();
         validSessionIds.add(sessionId);
         return pageGetParty(request, response, sessionId);
      }

      // create user
      UserBuilt userBuilt = new UserBuilt();
      userBuilt.setId(isoNow());
      userBuilt.setBlockId(name);
      userBuilt.setName(name)
            .setEmail(email)
            .setPassword(password);
      apply(userBuilt);

      String sessionId = name + "#" + isoNow();
      validSessionIds.add(sessionId);
      return pageGetParty(request, response, sessionId);
   }

   private String pageGetParty(Request request, Response response, String sessionId)
   {
      String name = request.queryParams("name");

      StringBuilder html = new StringBuilder();
      if (!validSessionIds.contains(sessionId)) {
         pageGetUserName(request, response);
         html.append("Invalid session id, please login\n");
         return html.toString();
      }

      html.append("<form action=\"/page/withParty\" method=\"get\">\n");
      html.append(String.format("   <p>Welcome %s</p>\n", name));
      html.append("   <p>Choose a party</p>\n");
      html.append("   <p><input id=\"party\" name=\"party\" placeholder=\"party?\"></p>\n");
      html.append("   <p><input id=\"location\" name=\"location\" placeholder=\"location?\"></p>\n");
      html.append(String.format("   <p><input id=\"name\" name=\"name\" type=\"hidden\" value=\"%s\"></p>\n", name));
      html.append(String.format("   <p><input id=\"sessionId\" name=\"sessionId\" type=\"hidden\" value=\"%s\"></p>\n", sessionId));
      html.append("   <p><input id=\"ok\" name=\"button\" type=\"submit\" value=\"ok\"></p>\n");
      html.append("</form>\n");

      return html.toString();
   }

   private String pageWithEmail(Request request, Response response)
   {
      String name = request.queryParams("name");
      String email = request.queryParams("email");

      StringBuilder html = new StringBuilder();

      Object object = model.getModelMap().get(name);
      if (object != null) {
         User user = (User) object;
         if (user.getEmail().equals(email)) {
            pageGetPassword(request, response);
         }
         else {
            return pageGetEmail(request, response);
         }
      }
      return pageGetPassword(request, response);

   }

   private String pageWithUserName(Request request, Response response)
   {
      StringBuilder html = new StringBuilder();
      String name = request.queryParams("name");

      Query query = new Query().setKey(name);
      query(query);

      if (query.getResults().size() == 0) {
         // getEmail
         return pageGetEmail(request, response);
      }

      // getPassword
      return pageGetPassword(request, response);

   }

   private String pageGetPassword(Request request, Response response)
   {
      StringBuilder html = new StringBuilder();
      String name = request.queryParams("name");
      String email = request.queryParams("email");

      html.append("<form action=\"/page/withPassword\" method=\"get\">\n");
      html.append(String.format("   <p>Welcome %s</p>\n", name));
      html.append("   <p>Provide password</p>\n");
      html.append("   <p><input id=\"password\" name=\"password\" type=\"password\" placeholder=\"password?\"></p>\n");
      html.append(String.format("   <p><input id=\"name\" name=\"name\" type=\"hidden\" value=\"%s\"></p>\n", name));
      html.append(String.format("   <p><input id=\"email\" name=\"email\" type=\"hidden\" value=\"%s\"></p>\n", email));
      html.append("   <p><input id=\"ok\" name=\"button\" type=\"submit\" value=\"ok\"></p>\n");
      html.append("   <p><input id=\"changepassword\" name=\"button\" type=\"submit\" value=\"change password\"></p>\n");
      html.append("</form>\n");

      return html.toString();
   }

   private String pageGetEmail(Request request, Response response)
   {
      String name = request.queryParams("name");
      StringBuilder html = new StringBuilder();

      Query query = new Query().setKey(name);
      query(query);

      html.append("<form action=\"/page/withEmail\" method=\"get\">\n");
      // PartyApp 12:05
      html.append(String.format("   <p>Welcome %s</p>\n", name));
      html.append("   <p>You are new to parties</p>\n");
      html.append("   <p>We need your email</p>\n");
      html.append("   <p><input id=\"email\" name=\"email\" placeholder=\"email?\"></p>\n");
      html.append(String.format("   <p><input id=\"name\" name=\"name\" type=\"hidden\" value=\"%s\"></p>\n", name));
      html.append("   <p><input id=\"ok\" name=\"button\" type=\"submit\" value=\"ok\"></p>\n");
      html.append("</form>\n");

      if (query.getResults().size() > 0) {
         html.append("Please use original email");
      }

      return html.toString();
   }

   private String pageGetUserName(Request request, Response response)
   {
      StringBuilder html = new StringBuilder();
      html.append("<form action=\"/page/withUserName\" method=\"get\">\n");
      // PartyApp 12:00
      html.append("   <p>Welcome to the parties</p>\n");
      html.append("   <p>What's your name?</p>\n");
      html.append("   <p><input id=\"name\" name=\"name\" placeholder=\"name?\"></p>\n");
      html.append("   <p><input id=\"ok\" name=\"button\" type=\"submit\" value=\"ok\"></p>\n");
      html.append("</form>\n");
      return html.toString();
   }

   public String getDemoPage(Request request, Response response)
   {
      StringBuilder html = new StringBuilder();
      String id = request.params("id");
      String event = request.queryParams("event");

      if ("check name 12:01".equals(event)) {

         // create CheckNameCommand: check name 12:01
         CheckNameCommand e1201 = new CheckNameCommand();
         e1201.setId("12:01");
         e1201.setName(request.queryParams("name"));
         apply(e1201);
      }

      if ("check email 12:03".equals(event)) {

         // create CheckEmailCommand: check email 12:03
         CheckEmailCommand e1203 = new CheckEmailCommand();
         e1203.setId("12:03");
         e1203.setEmail(request.queryParams("email"));
         apply(e1203);
      }

      if ("check password 12:05".equals(event)) {

         // create CheckPasswordCommand: check password 12:05
         CheckPasswordCommand e1205 = new CheckPasswordCommand();
         e1205.setId("12:05");
         apply(e1205);
      }

      if ("check party 12:07".equals(event)) {

         // create CheckPartyCommand: check party 12:07
         CheckPartyCommand e1207 = new CheckPartyCommand();
         e1207.setId("12:07");
         e1207.setParty(request.queryParams("party"));
         apply(e1207);
      }

      if ("check name 13:01".equals(event)) {

         // create CheckNameCommand: check name 13:01
         CheckNameCommand e1301 = new CheckNameCommand();
         e1301.setId("13:01");
         e1301.setName(request.queryParams("name"));
         apply(e1301);
      }

      if ("check password 13:03".equals(event)) {

         // create CheckPasswordCommand: check password 13:03
         CheckPasswordCommand e1303 = new CheckPasswordCommand();
         e1303.setId("13:03");
         apply(e1303);
      }

      if ("get party 13:05".equals(event)) {

         // create GetPartyCommand: get party 13:05
         GetPartyCommand e1305 = new GetPartyCommand();
         e1305.setId("13:05");
         e1305.setParty(request.queryParams("party"));
         apply(e1305);
      }

      if ("get party 14:01".equals(event)) {

         // create GetPartyCommand: get party 14:01
         GetPartyCommand e1401 = new GetPartyCommand();
         e1401.setId("14:01");
         e1401.setParty(request.queryParams("party"));
         e1401.setLocation(request.queryParams("location"));
         apply(e1401);
      }

      if ("add item 14:03".equals(event)) {

         // create AddItemCommand: add item 14:03
         AddItemCommand e1403 = new AddItemCommand();
         e1403.setId("14:03");
         apply(e1403);
      }

      if ("build item 14:05".equals(event)) {

         // create BuildItemCommand: build item 14:05
         BuildItemCommand e1405 = new BuildItemCommand();
         e1405.setId("14:05");
         e1405.setItem(request.queryParams("item"));
         e1405.setPrice(request.queryParams("price"));
         e1405.setBuyer(request.queryParams("buyer"));
         apply(e1405);
      }

      if ("add item 14:07".equals(event)) {

         // create AddItemCommand: add item 14:07
         AddItemCommand e1407 = new AddItemCommand();
         e1407.setId("14:07");
         apply(e1407);
      }

      if ("build item 14:09".equals(event)) {

         // create BuildItemCommand: build item 14:09
         BuildItemCommand e1409 = new BuildItemCommand();
         e1409.setId("14:09");
         e1409.setItem(request.queryParams("item"));
         e1409.setPrice(request.queryParams("price"));
         e1409.setBuyer(request.queryParams("buyer"));
         apply(e1409);
      }



      // 12:00
      if (id.equals("12_00")) {
         html.append("<form action=\"/page/12_02\" method=\"get\">\n");
         // PartyApp 12:00
         html.append("   <p>Welcome to the parties</p>\n");
         html.append("   <p>What's your name?</p>\n");
         html.append("   <p><input id=\"name\" name=\"name\" placeholder=\"name?\"></p>\n");
         html.append("   <p><input id=\"event\" name=\"event\" type=\"hidden\" value=\"check name 12:01\"></p>\n");
         html.append("   <p><input id=\"ok\" name=\"button\" type=\"submit\" value=\"ok\"></p>\n");
         html.append("</form>\n");
         return html.toString();
      }

      // 12:02
      if (id.equals("12_02")) {
         html.append("<form action=\"/page/12_04\" method=\"get\">\n");
         // PartyApp 12:02
         html.append("   <p>Welcome Alice</p>\n");
         html.append("   <p>You are new to parties</p>\n");
         html.append("   <p>We need your email</p>\n");
         html.append("   <p><input id=\"email\" name=\"email\" placeholder=\"email?\"></p>\n");
         html.append("   <p><input id=\"event\" name=\"event\" type=\"hidden\" value=\"check email 12:03\"></p>\n");
         html.append("   <p><input id=\"ok\" name=\"button\" type=\"submit\" value=\"ok\"></p>\n");
         html.append("</form>\n");
         return html.toString();
      }

      // 12:04
      if (id.equals("12_04")) {
         html.append("<form action=\"/page/12_06\" method=\"get\">\n");
         // PartyApp 12:04
         html.append("   <p>Welcome Alice</p>\n");
         html.append("   <p>Choose a password</p>\n");
         html.append("   <p><input id=\"password\" name=\"password\" type=\"password\" placeholder=\"password?\"></p>\n");
         html.append("   <p><input id=\"event\" name=\"event\" type=\"hidden\" value=\"check password 12:05\"></p>\n");
         html.append("   <p><input id=\"ok\" name=\"button\" type=\"submit\" value=\"ok\"></p>\n");
         html.append("</form>\n");
         return html.toString();
      }

      // 12:06
      if (id.equals("12_06")) {
         html.append("<form action=\"/page/13_00\" method=\"get\">\n");
         // PartyApp 12:06
         html.append("   <p>Welcome Alice</p>\n");
         html.append("   <p>Choose a party</p>\n");
         html.append("   <p><input id=\"party\" name=\"party\" placeholder=\"party?\"></p>\n");
         html.append("   <p><input id=\"event\" name=\"event\" type=\"hidden\" value=\"check party 12:07\"></p>\n");
         html.append("   <p><input id=\"ok\" name=\"button\" type=\"submit\" value=\"ok\"></p>\n");
         html.append("</form>\n");
         return html.toString();
      }

      // 13:00
      if (id.equals("13_00")) {
         html.append("<form action=\"/page/13_02\" method=\"get\">\n");
         // PartyApp 13:00
         html.append("   <p>Welcome to the parties</p>\n");
         html.append("   <p>What's your name?</p>\n");
         html.append("   <p><input id=\"name\" name=\"name\" placeholder=\"name?\"></p>\n");
         html.append("   <p><input id=\"event\" name=\"event\" type=\"hidden\" value=\"check name 13:01\"></p>\n");
         html.append("   <p><input id=\"ok\" name=\"button\" type=\"submit\" value=\"ok\"></p>\n");
         html.append("</form>\n");
         return html.toString();
      }

      // 13:02
      if (id.equals("13_02")) {
         html.append("<form action=\"/page/13_04\" method=\"get\">\n");
         // PartyApp 13:02
         html.append("   <p>Welcome Alice</p>\n");
         html.append("   <p>Provide a password</p>\n");
         html.append("   <p><input id=\"password\" name=\"password\" type=\"password\" placeholder=\"password?\"></p>\n");
         html.append("   <p><input id=\"event\" name=\"event\" type=\"hidden\" value=\"check password 13:03\"></p>\n");
         html.append("   <p><input id=\"ok\" name=\"button\" type=\"submit\" value=\"ok\"></p>\n");
         html.append("</form>\n");
         return html.toString();
      }

      // 13:04
      if (id.equals("13_04")) {
         html.append("<form action=\"/page/next_page\" method=\"get\">\n");
         // PartyApp 13:04
         html.append("   <p>Welcome Alice</p>\n");
         html.append("   <p>Choose a party</p>\n");
         html.append("   <p><input id=\"party\" name=\"party\" placeholder=\"party?\"></p>\n");
         html.append("   <p><input id=\"event\" name=\"event\" type=\"hidden\" value=\"get party 13:05\"></p>\n");
         html.append("   <p><input id=\"ok\" name=\"button\" type=\"submit\" value=\"ok\"></p>\n");
         html.append("</form>\n");
         return html.toString();
      }

      // 14:00
      if (id.equals("14_00")) {
         html.append("<form action=\"/page/14_02\" method=\"get\">\n");
         // PartyApp 14:00
         html.append("   <p>Welcome Alice</p>\n");
         html.append("   <p>Choose a party</p>\n");
         html.append("   <p><input id=\"party\" name=\"party\" placeholder=\"party?\"></p>\n");
         html.append("   <p><input id=\"location\" name=\"location\" placeholder=\"location?\"></p>\n");
         html.append("   <p><input id=\"event\" name=\"event\" type=\"hidden\" value=\"get party 14:01\"></p>\n");
         html.append("   <p><input id=\"ok\" name=\"button\" type=\"submit\" value=\"ok\"></p>\n");
         html.append("</form>\n");
         return html.toString();
      }

      // 14:02
      if (id.equals("14_02")) {
         html.append("<form action=\"/page/14_04\" method=\"get\">\n");
         // PartyApp 14:02
         html.append("   <p>Welcome Alice</p>\n");
         html.append("   <p>Let's do the SE BBQ</p>\n");
         html.append("   <p>no items yet</p>\n");
         html.append("   <p><input id=\"event\" name=\"event\" type=\"hidden\" value=\"add item 14:03\"></p>\n");
         html.append("   <p><input id=\"add\" name=\"button\" type=\"submit\" value=\"add\"></p>\n");
         html.append("</form>\n");
         return html.toString();
      }

      // 14:04
      if (id.equals("14_04")) {
         html.append("<form action=\"/page/14_06\" method=\"get\">\n");
         // PartyApp 14:04
         html.append("   <p>Welcome Alice</p>\n");
         html.append("   <p>Let's do the SE BBQ</p>\n");
         html.append("   <p><input id=\"item\" name=\"item\" placeholder=\"item?\"></p>\n");
         html.append("   <p><input id=\"price\" name=\"price\" placeholder=\"price?\"></p>\n");
         html.append("   <p><input id=\"buyer\" name=\"buyer\" placeholder=\"buyer?\"></p>\n");
         html.append("   <p><input id=\"event\" name=\"event\" type=\"hidden\" value=\"build item 14:05\"></p>\n");
         html.append("   <p><input id=\"ok\" name=\"button\" type=\"submit\" value=\"ok\"></p>\n");
         html.append("</form>\n");
         return html.toString();
      }

      // 14:06
      if (id.equals("14_06")) {
         html.append("<form action=\"/page/14_08\" method=\"get\">\n");
         // PartyApp 14:06
         html.append("   <p>Welcome Alice</p>\n");
         html.append("   <p>Let's do the SE BBQ</p>\n");
         html.append("   <p>beer 12.00 Bob</p>\n");
         html.append("   <p><input id=\"event\" name=\"event\" type=\"hidden\" value=\"add item 14:07\"></p>\n");
         html.append("   <p><input id=\"add\" name=\"button\" type=\"submit\" value=\"add\"></p>\n");
         html.append("   <p>Total 12.00</p>\n");
         html.append("   <p>Share 12.00</p>\n");
         html.append("   <p>Bob 0.00</p>\n");
         html.append("</form>\n");
         return html.toString();
      }

      // 14:08
      if (id.equals("14_08")) {
         html.append("<form action=\"/page/14_10\" method=\"get\">\n");
         // PartyApp 14:08
         html.append("   <p>Welcome Alice</p>\n");
         html.append("   <p>Let's do the SE BBQ</p>\n");
         html.append("   <p><input id=\"item\" name=\"item\" placeholder=\"item?\"></p>\n");
         html.append("   <p><input id=\"price\" name=\"price\" placeholder=\"price?\"></p>\n");
         html.append("   <p><input id=\"buyer\" name=\"buyer\" placeholder=\"buyer?\"></p>\n");
         html.append("   <p><input id=\"event\" name=\"event\" type=\"hidden\" value=\"build item 14:09\"></p>\n");
         html.append("   <p><input id=\"ok\" name=\"button\" type=\"submit\" value=\"ok\"></p>\n");
         html.append("</form>\n");
         return html.toString();
      }

      // 14:10
      if (id.equals("14_10")) {
         html.append("<form action=\"/page/next_page\" method=\"get\">\n");
         // PartyApp 14:10
         html.append("   <p>Welcome Alice</p>\n");
         html.append("   <p>Let's do the SE BBQ</p>\n");
         html.append("   <p>beer 12.00 Bob</p>\n");
         html.append("   <p>meat 21.00 Alice</p>\n");
         html.append("   <p><input id=\"add\" name=\"button\" type=\"submit\" value=\"add\"></p>\n");
         html.append("   <p>Total 33.00</p>\n");
         html.append("   <p>Share 16.50</p>\n");
         html.append("   <p>Bob -4.50</p>\n");
         html.append("   <p>Alice +4.50</p>\n");
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
      try {
         String body = req.body();
         YamlIdMap idMap = new YamlIdMap(Event.class.getPackageName());
         idMap.decode(body);
         Map<String, Object> map = idMap.getObjIdMap();
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

   public void removeYou()
   {
      this.setBusinessLogic(null);
      this.setBuilder(null);
   }
}
