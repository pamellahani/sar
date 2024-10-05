package events;

import channels.*;

public class Task {

    private boolean killed = false;
    private EventPump eventPump = new EventPump();  // Integrating EventPump
    QueueBroker queueBroker; 

    public Task(String name) {
        this.eventPump = new EventPump();
        this.queueBroker = new MixedQueueBroker(name);
    }

    /**
     * Post (push) a runnable task to the EventPump.
     * This will push the event to the event queue.
     * @param r the runnable task
     */
    public void post(Runnable r) {
        if (!killed) {
            // Push the task (event) to the EventPump's event queue
            eventPump.push(r);
        }
    }

    /**
     * Start processing the event queue.
     */
    public void start() {
        // The EventPump automatically starts processing in a separate thread
    }

    /**
     * Kill the task.
     * This stops new events from being posted.
     */
    public void kill() {
        killed = true;
        eventPump.stop();  // Stop the event pump from processing further
    }

    /**
     * Check if the task is killed.
     * @return true if the task is killed, false otherwise.
     */
    public boolean killed() {
        return killed;
    }

    /**
     * Link a Task with a Broker
     * @param port
     * @return Broker object
     */
    public Broker getBroker(String name) {
       return queueBroker.getBroker();
    }


}
