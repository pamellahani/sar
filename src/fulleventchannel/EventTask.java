package fulleventchannel;

import java.util.LinkedList;
import java.util.List;

public class EventTask {

    private List<Event> events;
    private static EventTask currentTask;
    private boolean isKilled;
    private EventPump eventPump;

    public EventTask() {
        eventPump = EventPump.getInstance();
        isKilled = false;
        events = new LinkedList<>();
        //System.out.println("New EventTask created");
    }

    public void post(Runnable r) {
        Event event = new Event(currentTask, this, r);
        events.add(event);
        eventPump.post(event);
       // System.out.println("Runnable posted to task: " + this);
    }

    public void kill() {
        this.isKilled = true;
        System.out.println("Task killed: " + this);
    }

    public boolean killed() {
        return isKilled;
    }

    public static EventTask getTask() {
        return currentTask;
    }

    public static void setCurrentTask(EventTask task) {
        currentTask = task;
        System.out.println("Current task set: " + task);
    }
}