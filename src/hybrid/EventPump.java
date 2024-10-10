package hybrid;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class EventPump {

    private final BlockingQueue<Runnable> eventQueue = new LinkedBlockingQueue<>();
    private volatile boolean running = true;
    //private final AtomicInteger activeEvents = new AtomicInteger(0);  // Track active events

    public EventPump() {
        // Start the event loop in a new thread
        new Thread(this::eventLoop).start();
    }

    /**
     * Event loop that continuously processes events from the queue.
     */
    private void eventLoop() {
        while (running) {
            try {
                Runnable event = pop();  // Use pop to retrieve and process events
                if (event != null) {
                    event.run();  // Execute the task/event
                }
            } catch (InterruptedException e) {
                
            }
        }
    }

    /**
     * Push a new event to the queue.
     * This acts as the enqueue operation, adding a task to the event queue.
     * @param event the runnable task to push into the queue
     */
    public void push(Runnable event) {
        try {
            eventQueue.put(event);  // Adds the event to the queue
        } catch (InterruptedException e) {
            
        }
    }

    /**
     * Pop an event from the queue.
     * This acts as the dequeue operation, retrieving a task from the event queue.
     * @return the next runnable task from the queue or null if interrupted
     * @throws InterruptedException if the thread is interrupted while waiting
     */
    public Runnable pop() throws InterruptedException {
        // Retrieves and removes the head of the queue, waiting if necessary
        return eventQueue.take();  // Retrieve and remove the next event from the queue
    }

    /**
     * Stop the event pump from processing events.
     * The event loop will finish once all tasks are completed.
     */
    public void stop() {
        running = false;
    }
}
