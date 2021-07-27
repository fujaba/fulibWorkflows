package uks.debuggen.page;
import com.codeborne.selenide.SelenideElement;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.fulib.yaml.Yaml;
import org.junit.Test;
import uks.debuggen.page.Shop.ShopService;
import uks.debuggen.page.events.*;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import java.util.Objects;
import java.beans.PropertyChangeSupport;

public class TestSomeEventStorming
{
   public static final String PROPERTY_EVENT_BROKER = "eventBroker";
   private EventBroker eventBroker;
   protected PropertyChangeSupport listeners;

   public EventBroker getEventBroker()
   {
      return this.eventBroker;
   }

   public TestSomeEventStorming setEventBroker(EventBroker value)
   {
      if (Objects.equals(value, this.eventBroker))
      {
         return this;
      }

      final EventBroker oldValue = this.eventBroker;
      this.eventBroker = value;
      this.firePropertyChange(PROPERTY_EVENT_BROKER, oldValue, value);
      return this;
   }

   @Test
   public void uitest()
   {
      // start the event broker
      eventBroker = new EventBroker();
      eventBroker.start();

      // start service
      ShopService shop = new ShopService();
      shop.start();

      open("http://localhost:42000");
      $("body").shouldHave(text("event broker"));

      SelenideElement pre = $("pre");
      pre.shouldHave(text("http://localhost:42001/apply"));

      // workflow working smoothly
      // page 12:50
      open("http://localhost:42001/page/12_50");
      pre = $("#shoes");
      pre.click();

      // create ShopShoesSelected: Shop shoes selected 12:51
      ShopShoesSelected e1251 = new ShopShoesSelected();
      e1251.setId("12:51");
      publish(e1251);
      open("http://localhost:42000");
      pre = $("#history");
      pre.shouldHave(text("- 12_51:"));

      // create OrderRegistered: order registered 13:00
      OrderRegistered e1300 = new OrderRegistered();
      e1300.setId("13:00");
      publish(e1300);
      open("http://localhost:42000");
      pre = $("#history");
      pre.shouldHave(text("- 13_00:"));

      System.out.println();
   }

   @Test
   public void SomeEventStorming()
   {
      // start the event broker
      eventBroker = new EventBroker();
      eventBroker.start();

      // start service
      ShopService shop = new ShopService();
      shop.start();

      open("http://localhost:42000");
      $("body").shouldHave(text("event broker"));

      SelenideElement pre = $("pre");
      pre.shouldHave(text("http://localhost:42001/apply"));

      // workflow working smoothly
      // page 12:50
      open("http://localhost:42001/page/12_50");
      $("#shoes").click();

      // create ShopShoesSelected: Shop shoes selected 12:51
      ShopShoesSelected e1251 = new ShopShoesSelected();
      e1251.setId("12:51");
      publish(e1251);
      open("http://localhost:42000");
      pre = $("#history");
      pre.shouldHave(text("- 12_51:"));

      // page 12:55
      open("http://localhost:42001/page/12_55");
      $("#name").setValue("Alice");
      $("#address").setValue("Wonderland 1");
      $("#ok").click();

      // create OrderRegistered: order registered 13:00
      OrderRegistered e1300 = new OrderRegistered();
      e1300.setId("13:00");
      publish(e1300);
      open("http://localhost:42000");
      pre = $("#history");
      pre.shouldHave(text("- 13_00:"));

      // page 13:07
      open("http://localhost:42001/page/13_07");
      $("#tshirt").click();

      // create ShopTshirtSelected: Shop tshirt selected 13:10
      ShopTshirtSelected e1310 = new ShopTshirtSelected();
      e1310.setId("13:10");
      publish(e1310);
      open("http://localhost:42000");
      pre = $("#history");
      pre.shouldHave(text("- 13_10:"));

      System.out.println();
   }

   public void publish(Event event)
   {
      String yaml = Yaml.encode(event);

      try {
         HttpResponse<String> response = Unirest.post("http://localhost:42000/publish")
               .body(yaml)
               .asString();
      }
      catch (UnirestException e) {
         e.printStackTrace();
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
}
