package uks.debuggen.studyright;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.SelenideElement;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.fulib.FulibTools;
import org.fulib.workflows.HtmlGenerator3;
import org.fulib.yaml.Yaml;
import org.junit.Before;
import org.junit.Test;
import uks.debuggen.studyright.StudyRight.StudyRightService;
import uks.debuggen.studyright.events.*;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.beans.PropertyChangeSupport;
import uks.debuggen.studyright.Welcome.WelcomeService;

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

   @Before
   public void setTimeOut() {
      Configuration.timeout = 10 * 60 * 1000;
      Configuration.pageLoadTimeout = Configuration.timeout;
   }

   @Test
   public void testImplentation() throws IOException
   {
      eventBroker = new EventBroker();
      eventBroker.start();

      // start service
      StudyRightService studyRight = new StudyRightService();
      studyRight.start();

      open("http://localhost:42000");
      $("body").shouldHave(text("event broker"));

      SelenideElement pre = $("pre");
      pre.shouldHave(text("http://localhost:42400/apply"));

      // workflow working smoothly
      // page 11:00
      open("http://localhost:42400/page/welcome");
      $("#ok").click();


      String html = new HtmlGenerator3().generateHtml(studyRight.getHistory());

      Files.write(Path.of("tmp/history.html"), html.getBytes(StandardCharsets.UTF_8));

      System.out.println();
   }

   @Test
   public void SomeEventStorming()
   {
      // start the event broker
      eventBroker = new EventBroker();
      eventBroker.start();

      // start service
      StudyRightService studyRight = new StudyRightService();
      studyRight.start();

      open("http://localhost:42000");
      $("body").shouldHave(text("event broker"));

      SelenideElement pre = $("pre");
      pre.shouldHave(text("http://localhost:42400/apply"));

      // workflow working smoothly
      // page 11:00
      open("http://localhost:42400/page/11_00");
      $("#ok").click();

      open("http://localhost:42000");
      pre = $("#history");
      pre.shouldHave(text("- 12_00:"));

      // check StudyRight
      open("http://localhost:42400");
      pre = $("#history");
      pre.shouldHave(text("- 12_00:"));
      // check data note 12:12:01
      pre = $("#data");
      pre.shouldHave(text("- tour1:"));
      pre.shouldHave(text("stops: \"math algebra modeling exam\""));

      // page 12:13
      open("http://localhost:42400/page/12_13");

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
