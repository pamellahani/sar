package hybrid;

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
		events = new LinkedList<Event>();
	}

	public void post(Runnable r) {
		Event event = new Event(currentTask, this, r);
		events.add(event);
		eventPump.post(event);
	}
	
	public void kill() {
		this.isKilled = true;
	}

	public boolean killed() {
		return isKilled;
	}

	public static EventTask getTask() {
		return currentTask;
	}
	
	public static void setCurrentTask(EventTask task) {
		currentTask = task;
	}


}
