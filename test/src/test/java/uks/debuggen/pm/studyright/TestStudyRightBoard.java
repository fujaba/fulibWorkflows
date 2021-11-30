package uks.debuggen.pm.studyright;
import java.util.LinkedHashMap;

import org.fulib.FulibTools;
import org.fulib.mockups.FulibMockups;
import org.junit.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class TestStudyRightBoard
{

   @Test
   public void testStudyRightBoardTableDump()
   {
      StudyGuideBusinessLogic logic = new StudyGuideBusinessLogic();

      Student carli = new Student().setId("carli");
      carli.setMotivation(83);

      Stop stop1 = new Stop().setId("stop1");
      stop1.setMotivation(66);

      Room r1 = new Room().setId("r1");
      r1.setTopic("math");
      r1.setCredits(17);

      Room r2 = new Room().setId("r2");
      r2.setTopic("calculus");
      r2.setCredits(20);

      Room r4 = new Room().setId("r4");
      r4.setTopic("exam");

      Room r5 = new Room().setId("r5");
      r5.setTopic("modeling");
      r5.setCredits(29);

      // create links
      carli.withStops(stop1);
      stop1.setRoom(r1);
      r1.withNeighbors(r2, r5);
      r2.withNeighbors(r1, r5);
      r4.withNeighbors(r5);
      r5.withNeighbors(r1, r2, r4);

      FulibTools.objectDiagrams().dumpSVG("tmp/StudyRightBoardStart.svg", carli, stop1, r1, r2, r4, r5);
      FulibMockups.htmlTool().dumpTables("tmp/StudyRightBoardStart.html", carli, stop1, r1, r2, r4, r5);

      logic.findRoute(carli);
      assertThat(carli.getRoute()).isEqualTo("math -> calculus -> math -> modeling -> exam");

      FulibTools.objectDiagrams().dumpSVG("tmp/StudyRightBoardEnd.svg", carli, stop1, r1, r2, r4, r5);
      FulibMockups.htmlTool().dumpTables("tmp/StudyRightBoardEnd.html", carli, stop1, r1, r2, r4, r5);

      System.err.println();
   }
}
