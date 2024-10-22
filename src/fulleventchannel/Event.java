package fulleventchannel;

public class Event implements Runnable{

    private final EventTask currentTask;
    private final Runnable runnable;

    public Event(EventTask fromtask, EventTask mytask, Runnable r) {
        currentTask = mytask;
        runnable = r;
        //System.out.println("New Event created for task: " + mytask.toString());
    }

    @Override
    public void run() {
        //System.out.println("Event running for task: " + currentTask);
        this.runnable.run();
    }

    public EventTask getTask() {
        return currentTask;
    }
}
