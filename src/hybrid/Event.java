package hybrid;

public class Event implements Runnable{
	
	private final EventTask curentTask;
	private final Runnable runnable;
	
	
	public Event(EventTask fromtask, EventTask mytask, Runnable r) {
		curentTask = mytask;
		runnable = r;
	}

	
	@Override
	public void run() {
		if(!curentTask.killed()) {
			EventTask.setCurrentTask(curentTask);
			this.runnable.run();
		}
	}
	
}
