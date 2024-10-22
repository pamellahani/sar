package fullevent;

import java.util.LinkedList;
import java.util.Queue;


public class EventPump {
	
	private static final EventPump INSTANCE = new EventPump();

	private Queue<Event> eventqueue;
	private static Event currentEvent;
	
	private EventPump() {
		eventqueue = new LinkedList<Event>();
	}
	
	public static EventPump getInstance() {
        return INSTANCE;
    }

	 public void post(Event ev) {
		 eventqueue.add(ev);
	}

	 public void removePost(Event ev) {
		 eventqueue.remove(ev);
	}

    private Event getNextEvent() {
    	currentEvent= eventqueue.poll();
    	return currentEvent;
    }
    
    public static Event getCurrentEvent() {
    	return currentEvent;
    }
    
    
	public void run() {
		while (!eventqueue.isEmpty()) {
			this.getNextEvent();
	
			try {
				currentEvent.run();
				// Stop running the event pump if a ShutdownEvent is processed
				if (currentEvent instanceof ShutdownEvent) {
					System.out.println("ShutdownEvent received, stopping EventPump.");
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		System.out.println("EventPump has finished running.");
	}
	
}