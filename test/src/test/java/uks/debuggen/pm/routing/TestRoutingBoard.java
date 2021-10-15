package uks.debuggen.pm.routing;

import java.util.LinkedHashMap;
import org.junit.Test;
import static org.assertj.core.api.Assertions.assertThat;
import org.fulib.FulibTools;

public class TestRoutingBoard
{

   @Test
   public void testRoutingBoard()
   {
      RoutingBusinessLogic logic = new RoutingBusinessLogic();

      Route route1 = new Route().setId("route1");

      Stop kassel = new Stop().setId("Kassel");

      Leg leg1 = new Leg().setId("leg1");
      leg1.setVia("A7");
      leg1.setLength("104 km");

      Stop fulda = new Stop().setId("Fulda");

      Leg leg2 = new Leg().setId("leg2");
      leg2.setVia("A66");
      leg2.setLength("71 km");

      Stop frankfurt = new Stop().setId("Frankfurt");

      // create links
      route1.setStart(kassel);
      route1.setEnd(frankfurt);
      kassel.setDeparture(leg1);
      leg1.setTo(fulda);
      fulda.setDeparture(leg2);
      leg2.setTo(frankfurt);

      FulibTools.objectDiagrams().dumpSVG("tmp/RoutingBoardStart.svg", route1, kassel, leg1, fulda, leg2, frankfurt);

      logic.computeLength(route1);
      assertThat(route1.getLength()).isEqualTo("175 km");

      FulibTools.objectDiagrams().dumpSVG("tmp/RoutingBoardEnd.svg", route1, kassel, leg1, fulda, leg2, frankfurt);


      System.err.println();
   }
}
