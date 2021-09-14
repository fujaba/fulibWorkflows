package uks.debuggen.pm;
import java.util.LinkedHashMap;
import org.junit.Test;
import uks.debuggen.pm.Routing.*;

import static org.assertj.core.api.Assertions.assertThat;

public class TestSomeEventStorming
{

   @Test
   public void testSomeEventStorming()
   {
      RoutingBusinessLogic logic = new RoutingBusinessLogic();
      RoutingModel model = new RoutingModel();

      Route route1 = model.getOrCreateRoute("route1");
      route1.setStart(model.getOrCreateStop("Kassel"));
      route1.setEnd(model.getOrCreateStop("Frankfurt"));

      Stop kassel = model.getOrCreateStop("Kassel");
      kassel.setDeparture(model.getOrCreateLeg("leg1"));

      Leg leg1 = model.getOrCreateLeg("leg1");
      leg1.setTo(model.getOrCreateStop("Fulda"));
      leg1.setVia("A7");
      leg1.setLength("104 km");

      Stop fulda = model.getOrCreateStop("Fulda");
      fulda.setDeparture(model.getOrCreateLeg("leg2"));

      Leg leg2 = model.getOrCreateLeg("leg2");
      leg2.setTo(model.getOrCreateStop("Frankfurt"));
      leg2.setVia("A66");
      leg2.setLength("71 km");

      Stop frankfurt = model.getOrCreateStop("Frankfurt");
      logic.computeLength(route1);
      assertThat(model.getOrCreateRoute("route1").getLength()).isEqualTo("175 km");

      System.out.println();
   }
}
