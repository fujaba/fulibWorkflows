package uks.debuggen.pm.studyright;

import java.util.LinkedList;
import java.util.Queue;

public class StudyGuideBusinessLogic
{

    private LinkedList<Stop> todo;
    private Student student;

    public void findRoute(Student student) {
        this.student = student;
        Stop stop = student.getStops().get(0);
        todo = new LinkedList<>();
        todo.push(stop);
        while ( ! todo.isEmpty()) {
            stop = todo.pollFirst();
            Room room = stop.getRoom();
            for (Room neighbor : room.getNeighbors()) {
                stopIn(neighbor, stop);
            }
        }

    }

    private void stopIn(Room room, Stop previous) {
        int newMotivation = previous.getMotivation() - room.getCredits();
        if (newMotivation < 0) {
            return;
        }

        Stop newStop = new Stop();
        newStop.setRoom(room)
            .setMotivation(newMotivation)
            .setPrev(previous);
        todo.push(newStop);

        if (newMotivation == 0 && room.getTopic().equals("exam")) {
            // found valid route
            String validRoute = "exam";
            while (true) {
                newStop = newStop.getPrev();
                if (newStop == null) {
                    student.setRoute(validRoute);
                    break;
                }

                validRoute = newStop.getRoom().getTopic() + " -> " + validRoute;

            }
        }
    }
}
