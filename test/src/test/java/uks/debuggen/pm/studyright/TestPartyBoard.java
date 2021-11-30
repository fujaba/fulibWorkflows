package uks.debuggen.pm.studyright;
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

      // create links

      FulibTools.objectDiagrams().dumpSVG("tmp/PartyBoardStart.svg", );

      logic.getParty14:01(SE BBQFridayUni);

      FulibTools.objectDiagrams().dumpSVG("tmp/PartyBoardEnd.svg", );


      System.err.println();
   }
}
