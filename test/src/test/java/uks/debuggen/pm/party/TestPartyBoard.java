package uks.debuggen.pm.party;
import java.util.LinkedHashMap;
import org.fulib.FulibTools;
import org.junit.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class TestPartyBoard
{

   @Test
   public void testPartyBoard()
   {
      PartyAppBusinessLogic logic = new PartyAppBusinessLogic();

      PartyApp theApp = new PartyApp().setId("theApp");

      Guest alice = new Guest().setId("Alice");
      alice.setName("Alice");

      // create links
      alice.setApp(theApp);

      FulibTools.objectDiagrams().dumpSVG("../event-models/Party/partyApp12_00_00.svg", theApp, alice);

      logic.computeSaldo(theApp);

      FulibTools.objectDiagrams().dumpSVG("../event-models/Party/PartyBoardEnd.svg", theApp, alice);


      System.err.println();
   }
}
