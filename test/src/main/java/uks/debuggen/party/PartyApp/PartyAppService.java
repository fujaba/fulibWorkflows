package uks.debuggen.party.PartyApp;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.fulib.workflows.QueryNote;
import org.fulib.yaml.Yaml;
import spark.Request;
import spark.Response;
import spark.Service;
import uks.debuggen.party.events.*;

import java.util.Objects;
import java.beans.PropertyChangeSupport;

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
         String events = Yaml.encode(getHistory().values().toArray());
         String objects = Yaml.encode(model.getModelMap().values().toArray());
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
            .setServiceUrl("http://localhost:42001/apply");
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
         StringBuilder html = new StringBuilder();

         if (id.equals("getUserName")) {
            pageGetUserName(html);
            return html.toString();
         }

         if (id.equals("withUserName")) {
            String name = request.queryParams("name");
            Query query = new Query().setKey(name);
            query(query);

            if (query.getResults().size() == 0) {
               // getEmail
               pageGetEmail(html, name);
            }
            else {
               // getPassword
               pageGetPassword(html, name, "");
            }

            return html.toString();
         }

         if (id.equals("withEmail")) {
            String name = request.queryParams("name");
            String email = request.queryParams("email");

            Object object = model.getModelMap().get(name);
            if (object != null) {
               User user = (User) object;
               if (user.getEmail().equals(email)) {
                  pageGetPassword(html, name, email);
               }
               else {
                  pageGetEmail(html, name);
                  html.append("Please use original email");
               }
            }
            else {
               pageGetPassword(html, name, email);
            }

            return html.toString();
         }

         if (id.equals("withPassword")) {
            String name = request.queryParams("name");
            String email = request.queryParams("email");
            String password = request.queryParams("password");
            String button = request.queryParams("button");

            if (button.equals("change password")) {
               pageGetEmail(html, name);
               return html.toString();
            }

            if (email.equals("")) {
               // check password
               Object obj = model.getModelMap().get(name);
               if (obj == null || !(obj instanceof User)) {
                  pageGetEmail(html, name);
                  return html.toString();
               }

               User user = (User) obj;
               if (!user.getPassword().equals(password)) {
                  pageGetPassword(html, name, email);
                  html.append("Please try again");
                  return html.toString();
               }

               return "choose party";
            }
            else {
               // create user
               UserBuilt userBuilt = new UserBuilt();
               userBuilt.setId(DateTimeFormatter.ISO_INSTANT.format(Instant.now()));
               userBuilt.setBlockId(name);
               userBuilt.setName(name)
                     .setEmail(email)
                     .setPassword(password);
               apply(userBuilt);

               return "choose party";
            }
         }
         return getDemoPage(request, response);
      }
      catch (Exception e) {
         e.printStackTrace();
         return "exception " + e;
      }
   }

   private void pageGetPassword(StringBuilder html, String name, String email)
   {
      html.append("<form action=\"/page/withPassword\" method=\"get\">\n");
      html.append(String.format("   <p>Welcome %s</p>\n", name));
      html.append("   <p>Provide password</p>\n");
      html.append("   <p><input id=\"password\" name=\"password\" type=\"password\" placeholder=\"password?\"></p>\n");
      html.append(String.format("   <p><input id=\"name\" name=\"name\" type=\"hidden\" value=\"%s\"></p>\n", name));
      html.append(String.format("   <p><input id=\"email\" name=\"email\" type=\"hidden\" value=\"%s\"></p>\n", email));
      html.append("   <p><input id=\"ok\" name=\"button\" type=\"submit\" value=\"ok\"></p>\n");
      html.append("   <p><input id=\"changepassword\" name=\"button\" type=\"submit\" value=\"change password\"></p>\n");
      html.append("</form>\n");
   }

   private void pageGetEmail(StringBuilder html, String name)
   {
      html.append("<form action=\"/page/withEmail\" method=\"get\">\n");
      // PartyApp 12:05
      html.append(String.format("   <p>Welcome %s</p>\n", name));
      html.append("   <p>You are new to parties</p>\n");
      html.append("   <p>We need your email</p>\n");
      html.append("   <p><input id=\"email\" name=\"email\" placeholder=\"email?\"></p>\n");
      html.append(String.format("   <p><input id=\"name\" name=\"name\" type=\"hidden\" value=\"%s\"></p>\n", name));
      html.append("   <p><input id=\"ok\" name=\"button\" type=\"submit\" value=\"ok\"></p>\n");
      html.append("</form>\n");
   }

   private void pageGetUserName(StringBuilder html)
   {
      html.append("<form action=\"/page/withUserName\" method=\"get\">\n");
      // PartyApp 12:00
      html.append("   <p>Welcome to the parties</p>\n");
      html.append("   <p>What's your name?</p>\n");
      html.append("   <p><input id=\"name\" name=\"name\" placeholder=\"name?\"></p>\n");
      html.append("   <p><input id=\"ok\" name=\"button\" type=\"submit\" value=\"ok\"></p>\n");
      html.append("</form>\n");
   }

   public String getDemoPage(Request request, Response response)
   {
      StringBuilder html = new StringBuilder();
      String id = request.params("id");
      String event = request.queryParams("event");

      if ("get user name 12:01".equals(event)) {

         // create GetUserNameCommand: get user name 12:01
         GetUserNameCommand e1201 = new GetUserNameCommand();
         e1201.setId("12:01");
         e1201.setName(request.queryParams("name"));
         apply(e1201);
      }

      if ("get email 12:04".equals(event)) {

         // create GetEmailCommand: get email 12:04
         GetEmailCommand e1204 = new GetEmailCommand();
         e1204.setId("12:04");
         e1204.setEmail(request.queryParams("email"));
         apply(e1204);
      }

      if ("get password 12:07".equals(event)) {

         // create GetPasswordCommand: get password 12:07
         GetPasswordCommand e1207 = new GetPasswordCommand();
         e1207.setId("12:07");
         apply(e1207);
      }

      if ("get party 12:12".equals(event)) {

         // create GetPartyCommand: get party 12:12
         GetPartyCommand e1212 = new GetPartyCommand();
         e1212.setId("12:12");
         e1212.setParty(request.queryParams("party"));
         apply(e1212);
      }

      if ("get user name 13:01".equals(event)) {

         // create GetUserNameCommand: get user name 13:01
         GetUserNameCommand e1301 = new GetUserNameCommand();
         e1301.setId("13:01");
         e1301.setName(request.queryParams("name"));
         apply(e1301);
      }

      if ("get password 13:07".equals(event)) {

         // create GetPasswordCommand: get password 13:07
         GetPasswordCommand e1307 = new GetPasswordCommand();
         e1307.setId("13:07");
         apply(e1307);
      }

      if ("get party 13:12".equals(event)) {

         // create GetPartyCommand: get party 13:12
         GetPartyCommand e1312 = new GetPartyCommand();
         e1312.setId("13:12");
         e1312.setParty(request.queryParams("party"));
         apply(e1312);
      }

      if ("get party 14:01".equals(event)) {

         // create GetPartyCommand: get party 14:01
         GetPartyCommand e1401 = new GetPartyCommand();
         e1401.setId("14:01");
         e1401.setParty(request.queryParams("party"));
         e1401.setLocation(request.queryParams("location"));
         apply(e1401);
      }

      if ("add item 14:11".equals(event)) {

         // create AddItemCommand: add item 14:11
         AddItemCommand e1411 = new AddItemCommand();
         e1411.setId("14:11");
         apply(e1411);
      }

      if ("build item 14:13".equals(event)) {

         // create BuildItemCommand: build item 14:13
         BuildItemCommand e1413 = new BuildItemCommand();
         e1413.setId("14:13");
         e1413.setItem(request.queryParams("item"));
         e1413.setPrice(request.queryParams("price"));
         e1413.setBuyer(request.queryParams("buyer"));
         apply(e1413);
      }

      if ("add item 14:21".equals(event)) {

         // create AddItemCommand: add item 14:21
         AddItemCommand e1421 = new AddItemCommand();
         e1421.setId("14:21");
         apply(e1421);
      }

      if ("build item 14:23".equals(event)) {

         // create BuildItemCommand: build item 14:23
         BuildItemCommand e1423 = new BuildItemCommand();
         e1423.setId("14:23");
         e1423.setItem(request.queryParams("item"));
         e1423.setPrice(request.queryParams("price"));
         e1423.setBuyer(request.queryParams("buyer"));
         apply(e1423);
      }



      // 12:00
      if (id.equals("12_00")) {
         html.append("<form action=\"/page/12_05\" method=\"get\">\n");
         // PartyApp 12:00
         html.append("   <p>Welcome to the parties</p>\n");
         html.append("   <p>What's your name?</p>\n");
         html.append("   <p><input id=\"name\" name=\"name\" placeholder=\"name?\"></p>\n");
         html.append("   <p><input id=\"event\" name=\"event\" type=\"hidden\" value=\"get user name 12:01\"></p>\n");
         html.append("   <p><input id=\"ok\" name=\"button\" type=\"submit\" value=\"ok\"></p>\n");
         html.append("</form>\n");
         return html.toString();
      }

      // 12:05
      if (id.equals("12_05")) {
         html.append("<form action=\"/page/12_06\" method=\"get\">\n");
         // PartyApp 12:05
         html.append("   <p>Welcome Alice</p>\n");
         html.append("   <p>You are new to parties</p>\n");
         html.append("   <p>We need your email</p>\n");
         html.append("   <p><input id=\"email\" name=\"email\" placeholder=\"email?\"></p>\n");
         html.append("   <p><input id=\"event\" name=\"event\" type=\"hidden\" value=\"get email 12:04\"></p>\n");
         html.append("   <p><input id=\"ok\" name=\"button\" type=\"submit\" value=\"ok\"></p>\n");
         html.append("</form>\n");
         return html.toString();
      }

      // 12:06
      if (id.equals("12_06")) {
         html.append("<form action=\"/page/12_11\" method=\"get\">\n");
         // PartyApp 12:06
         html.append("   <p>Welcome Alice</p>\n");
         html.append("   <p>Choose a password</p>\n");
         html.append("   <p><input id=\"password\" name=\"password\" type=\"password\" placeholder=\"password?\"></p>\n");
         html.append("   <p><input id=\"event\" name=\"event\" type=\"hidden\" value=\"get password 12:07\"></p>\n");
         html.append("   <p><input id=\"ok\" name=\"button\" type=\"submit\" value=\"ok\"></p>\n");
         html.append("</form>\n");
         return html.toString();
      }

      // 12:11
      if (id.equals("12_11")) {
         html.append("<form action=\"/page/13_00\" method=\"get\">\n");
         // PartyApp 12:11
         html.append("   <p>Welcome Alice</p>\n");
         html.append("   <p>Choose a party</p>\n");
         html.append("   <p><input id=\"party\" name=\"party\" placeholder=\"party?\"></p>\n");
         html.append("   <p><input id=\"event\" name=\"event\" type=\"hidden\" value=\"get party 12:12\"></p>\n");
         html.append("   <p><input id=\"ok\" name=\"button\" type=\"submit\" value=\"ok\"></p>\n");
         html.append("</form>\n");
         return html.toString();
      }

      // 13:00
      if (id.equals("13_00")) {
         html.append("<form action=\"/page/13_06\" method=\"get\">\n");
         // PartyApp 13:00
         html.append("   <p>Welcome to the parties</p>\n");
         html.append("   <p>What's your name?</p>\n");
         html.append("   <p><input id=\"name\" name=\"name\" placeholder=\"name?\"></p>\n");
         html.append("   <p><input id=\"event\" name=\"event\" type=\"hidden\" value=\"get user name 13:01\"></p>\n");
         html.append("   <p><input id=\"ok\" name=\"button\" type=\"submit\" value=\"ok\"></p>\n");
         html.append("</form>\n");
         return html.toString();
      }

      // 13:06
      if (id.equals("13_06")) {
         html.append("<form action=\"/page/13_11\" method=\"get\">\n");
         // PartyApp 13:06
         html.append("   <p>Welcome Alice</p>\n");
         html.append("   <p>Provide a password</p>\n");
         html.append("   <p><input id=\"password\" name=\"password\" type=\"password\" placeholder=\"password?\"></p>\n");
         html.append("   <p><input id=\"event\" name=\"event\" type=\"hidden\" value=\"get password 13:07\"></p>\n");
         html.append("   <p><input id=\"ok\" name=\"button\" type=\"submit\" value=\"ok\"></p>\n");
         html.append("</form>\n");
         return html.toString();
      }

      // 13:11
      if (id.equals("13_11")) {
         html.append("<form action=\"/page/next_page\" method=\"get\">\n");
         // PartyApp 13:11
         html.append("   <p>Welcome Alice</p>\n");
         html.append("   <p>Choose a party</p>\n");
         html.append("   <p><input id=\"party\" name=\"party\" placeholder=\"party?\"></p>\n");
         html.append("   <p><input id=\"event\" name=\"event\" type=\"hidden\" value=\"get party 13:12\"></p>\n");
         html.append("   <p><input id=\"ok\" name=\"button\" type=\"submit\" value=\"ok\"></p>\n");
         html.append("</form>\n");
         return html.toString();
      }

      // 14:00
      if (id.equals("14_00")) {
         html.append("<form action=\"/page/14_10\" method=\"get\">\n");
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

      // 14:10
      if (id.equals("14_10")) {
         html.append("<form action=\"/page/14_12\" method=\"get\">\n");
         // PartyApp 14:10
         html.append("   <p>Welcome Alice</p>\n");
         html.append("   <p>Let's do the SE BBQ</p>\n");
         html.append("   <p>no items yet</p>\n");
         html.append("   <p><input id=\"event\" name=\"event\" type=\"hidden\" value=\"add item 14:11\"></p>\n");
         html.append("   <p><input id=\"add\" name=\"button\" type=\"submit\" value=\"add\"></p>\n");
         html.append("</form>\n");
         return html.toString();
      }

      // 14:12
      if (id.equals("14_12")) {
         html.append("<form action=\"/page/14_20\" method=\"get\">\n");
         // PartyApp 14:12
         html.append("   <p>Welcome Alice</p>\n");
         html.append("   <p>Let's do the SE BBQ</p>\n");
         html.append("   <p><input id=\"item\" name=\"item\" placeholder=\"item?\"></p>\n");
         html.append("   <p><input id=\"price\" name=\"price\" placeholder=\"price?\"></p>\n");
         html.append("   <p><input id=\"buyer\" name=\"buyer\" placeholder=\"buyer?\"></p>\n");
         html.append("   <p><input id=\"event\" name=\"event\" type=\"hidden\" value=\"build item 14:13\"></p>\n");
         html.append("   <p><input id=\"ok\" name=\"button\" type=\"submit\" value=\"ok\"></p>\n");
         html.append("</form>\n");
         return html.toString();
      }

      // 14:20
      if (id.equals("14_20")) {
         html.append("<form action=\"/page/14_22\" method=\"get\">\n");
         // PartyApp 14:20
         html.append("   <p>Welcome Alice</p>\n");
         html.append("   <p>Let's do the SE BBQ</p>\n");
         html.append("   <p>beer 12.00 Bob</p>\n");
         html.append("   <p><input id=\"event\" name=\"event\" type=\"hidden\" value=\"add item 14:21\"></p>\n");
         html.append("   <p><input id=\"add\" name=\"button\" type=\"submit\" value=\"add\"></p>\n");
         html.append("   <p>Total 12.00</p>\n");
         html.append("   <p>Share 12.00</p>\n");
         html.append("   <p>Bob 0.00</p>\n");
         html.append("</form>\n");
         return html.toString();
      }

      // 14:22
      if (id.equals("14_22")) {
         html.append("<form action=\"/page/14_30\" method=\"get\">\n");
         // PartyApp 14:22
         html.append("   <p>Welcome Alice</p>\n");
         html.append("   <p>Let's do the SE BBQ</p>\n");
         html.append("   <p><input id=\"item\" name=\"item\" placeholder=\"item?\"></p>\n");
         html.append("   <p><input id=\"price\" name=\"price\" placeholder=\"price?\"></p>\n");
         html.append("   <p><input id=\"buyer\" name=\"buyer\" placeholder=\"buyer?\"></p>\n");
         html.append("   <p><input id=\"event\" name=\"event\" type=\"hidden\" value=\"build item 14:23\"></p>\n");
         html.append("   <p><input id=\"ok\" name=\"button\" type=\"submit\" value=\"ok\"></p>\n");
         html.append("</form>\n");
         return html.toString();
      }

      // 14:30
      if (id.equals("14_30")) {
         html.append("<form action=\"/page/next_page\" method=\"get\">\n");
         // PartyApp 14:30
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

   public void removeYou()
   {
      this.setBusinessLogic(null);
      this.setBuilder(null);
   }
}
