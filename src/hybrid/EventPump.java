package hybrid;

import java.util.LinkedList;
import java.util.Queue;
public class EventPump extends Thread{
	
	private static final EventPump INSTANCE = new EventPump();

	private Queue<Runnable> runnable_queue;
	private static Runnable currentRunnable;
	private boolean isRunning;
	
	private EventPump() {
		runnable_queue = new LinkedList<Runnable>();
		isRunning = true;
	}
	
	public static EventPump getInstance() {
        return INSTANCE;
    }

	synchronized public void post(Runnable runnable) {
		runnable_queue.add(runnable);
		this.notifyAll();  // Ensure that the waiting thread is notified when a new runnable is added
	}

	synchronized public void unpost(Runnable runnable) {
		runnable_queue.remove(runnable);
	}
	
    synchronized private Runnable getNext() {
    	currentRunnable= runnable_queue.poll();
    	return currentRunnable;
    }
    
    public static Runnable getCurrentRunnable() {
    	return currentRunnable;
    }
    
    synchronized private boolean isRunnableEmpty() {
    	return runnable_queue.isEmpty();
    }
    
    synchronized public void stopPump() {
    	this.isRunning = false;
    	this.notifyAll();
    }
    
	@Override
public void run() {
    while (isRunning || !isRunnableEmpty()) {
        synchronized (this) {
            while (isRunnableEmpty() && isRunning) {
                try {
                    System.out.println("EventPump waiting...");
                    wait();  // Wait until notified of a new task or stop signal
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }

        if (!isRunning && isRunnableEmpty()) {
            System.out.println("EventPump stopping...");
            return;
        }

        Runnable nextRunnable = getNext();
        if (nextRunnable != null) {
            try {
                System.out.println("EventPump executing runnable...");
                nextRunnable.run();
                synchronized (this) {
                    runnable_queue.remove(nextRunnable);  // Remove the task after execution
                }
                if (!isRunning ) {
                    System.out.println("All tasks are done, stopping EventPump...");
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if (isRunnableEmpty()){
            System.out.println("No runnable to execute...");    
            return; 
        }
    }
}


    // @Override
    // public void run() {

    //     while (isRunning) {
    // 		while (this.isRunnableEmpty() && isRunning) {
    // 			synchronized (this) {
    //                 try {
    //                     wait(); //TODO: process stuck here. No one is notifying this thread. Debug to track stopPump() method
    //                 } catch (InterruptedException e) {
    //                     e.printStackTrace();
    //                     return;
    //                 }
    //             }
	// 		}
        	
    //     	if(!isRunning) {
    //     		return;
    //     	}
            		
    //     	this.getNext();
        
    //     	try {
    //     		currentRunnable.run();
    //         } catch (Exception e) {
            	
    //         }

            	 
    //     }
        
    // }
}