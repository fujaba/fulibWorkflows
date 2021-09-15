package uks.debuggen.pm;
import java.util.LinkedHashMap;
import org.junit.Test;
import uks.debuggen.pm.Routing.*;

import static org.assertj.core.api.Assertions.assertThat;
import uks.debuggen.pm.someservice.*;

public class TestSomeEventStorming
{

   @Test
   public void testSomeEventStorming()
   {
      someserviceBusinessLogic logic = new someserviceBusinessLogic();
      RoutingModel model = new RoutingModel();
      logic.init();
      assertThat(model.getOrCreateRoute("route1").getStart()).isEqualTo("Kassel");
      assertThat(model.getOrCreateRoute("route1").getStart.back()).isEqualTo("[routeStarts]");
      assertThat(model.getOrCreateRoute("route1").getEnd()).isEqualTo("Frankfurt");
      assertThat(model.getOrCreateRoute("route1").getEnd.back()).isEqualTo("[routeEnds]");
      assertThat(model.getOrCreateStop("Kassel").getDeparture()).isEqualTo("leg1");
      assertThat(model.getOrCreateStop("Kassel").getDeparture.back()).isEqualTo("from");
      assertThat(model.getOrCreateLeg("leg1").getTo()).isEqualTo("Fulda");
      assertThat(model.getOrCreateLeg("leg1").getTo.back()).isEqualTo("arrival");
      assertThat(model.getOrCreateLeg("leg1").getVia()).isEqualTo("A7");
      assertThat(model.getOrCreateLeg("leg1").getLength()).isEqualTo("104 km");
      assertThat(model.getOrCreateStop("Fulda").getDeparture()).isEqualTo("leg2");
      assertThat(model.getOrCreateLeg("leg2").getTo()).isEqualTo("Frankfurt");
      assertThat(model.getOrCreateLeg("leg2").getVia()).isEqualTo("A66");
      assertThat(model.getOrCreateLeg("leg2").getLength()).isEqualTo("71 km");
      logic.computeLength(route1);
      assertThat(model.getOrCreateRoute("route1").getLength()).isEqualTo("175 km");

      System.out.println();
   }
}
