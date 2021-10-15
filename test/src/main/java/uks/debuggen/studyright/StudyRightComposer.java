package uks.debuggen.studyright;

import uks.debuggen.studyright.StudyRight.StudyRightService;
import uks.debuggen.studyright.events.EventBroker;

public class StudyRightComposer
{
   public static void main(String[] args)
   {
      // start the event broker
      EventBroker eventBroker = new EventBroker();
      eventBroker.start();

      // start service
      StudyRightService studyRight = new StudyRightService();
      studyRight.start();

      System.err.println("StudyRight system is up and running ");
   }
}
