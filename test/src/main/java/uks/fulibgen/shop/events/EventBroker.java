package uks.fulibgen.shop.events;

import java.beans.PropertyChangeSupport;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.fulib.yaml.Yaml;
import spark.Request;
import spark.Response;
import spark.Service;

public class EventBroker
{
   public static final String PROPERTY_HISTORY = "history";
   private LinkedHashMap<String, Event> history;
   protected PropertyChangeSupport listeners;
   private Service spark;
   private int port = 42000;
   private LinkedHashMap<String, ServiceSubscribed> subscribersMap = new LinkedHashMap<>();

   public static void main(String[] args)
   {
      new EventBroker().start();
   }

   public void start()
   {
      ExecutorService executor = Executors.newSingleThreadExecutor();
      spark = Service.ignite();
      spark.port(port);
      spark.get("/", (req, res) -> executor.submit(() -> this.getHello(req, res)).get());
      spark.post("/subscribe", (req, res) -> executor.submit(() -> this.postSubscribe(req, res)).get());
      spark.post("/publish", (req, res) -> executor.submit(() -> this.postPublish(req, res)).get());

      Logger.getGlobal().info("Event Broker is up and running on port " + port);
   }

   private String getHello(Request req, Response res)
   {
      StringBuilder buf = new StringBuilder();
      buf.append("This is the event broker. <br>\n" +
            "post to /subscribe a ServiceSubcribed event. <br>\n" +
            "post to /publish any event you want other services to know about<br>\n");

      String yaml = Yaml.encode(subscribersMap.values().toArray());
      String pre = String.format("<pre>%s</pre>", yaml);
      buf.append(pre);

      yaml = Yaml.encode(getHistory().values().toArray());
      pre = String.format("<pre id=\"history\">%s</pre>", yaml);
      buf.append(pre);
      return buf.toString();
   }

   private String postSubscribe(Request req, Response res)
   {
      try {
         String body = req.body();
         Map<String, Object> map = Yaml.decode(body);

         for (Object value : map.values()) {
            ServiceSubscribed serviceSubscribed = (ServiceSubscribed) value;
            subscribersMap.put(serviceSubscribed.getServiceUrl(), serviceSubscribed);

            // reply with list of all events
            Collection<Event> values = getHistory().values();
            String yaml = Yaml.encode(values.toArray());
            return yaml;
         }
      }
      catch (Exception e) {
         e.printStackTrace();
      }

      return "ups";
   }

   public void stop() {
      spark.stop();
   }


   private String postPublish(Request req, Response res)
   {
      try {
         String body = req.body();

         Map<String, Object> newEvents = Yaml.decode(body);

         for (Object obj : newEvents.values()) {
            Event newEvent = (Event) obj;
            if (getHistory().get(newEvent.getId()) == null) {
               getHistory().put(newEvent.getId(), newEvent);
               publisher.execute(() -> publish(newEvent));
            }
         }
      }
      catch (Exception e) {
         e.printStackTrace();
      }
      return "eventbroker roger";
   }

   Executor publisher = Executors.newSingleThreadExecutor();

   private void publish(Event newEvent)
   {
      Unirest.setTimeouts(3*60*1000, 3*60*1000);
      String yaml = Yaml.encode(newEvent);
      for (ServiceSubscribed service : subscribersMap.values()) {
         try {
            HttpResponse<String> response = Unirest.post(service.getServiceUrl())
                  .body(yaml)
                  .asString();
            // System.out.println(response.getBody());
            // System.out.println();
         }
         catch (UnirestException e) {
            e.printStackTrace();
         }
      }
   }

   public LinkedHashMap<String, Event> getHistory() // no fulib
   {
      if (history == null) {
         history = new LinkedHashMap<>();
      }
      return this.history;
   }

   public EventBroker setHistory(LinkedHashMap<String, Event> value)
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
   }
}
