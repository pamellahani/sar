package channels;

import messages.QueueBroker;

/**
 * The {@code Task} class represents an abstract task that extends the {@code Thread} class.
 * It is designed to be used with a {@code Broker} and a {@code Runnable} task.
 * 
 * <p>This class provides the following functionalities:
 * <ul>
 *   <li>Associates a {@code Broker} with a {@code Runnable} task.</li>
 *   <li>Overrides the {@code run} method to execute the {@code Runnable} task.</li>
 *   <li>Provides a static method to retrieve the associated {@code Broker} from a {@code Task} instance.</li>
 * </ul>
 * </p>
 * @see Broker
 * @see Runnable
 */
public abstract class Task extends Thread{

    Broker broker;
    Runnable task;
    QueueBroker queueBroker;

    public Task(Broker b, Runnable r) {
        this.broker = b;
        this.task = r;
    }

    public Task(QueueBroker b, Runnable r) {
        this.queueBroker = b;
        this.task = r;
    }

    public static Broker getBroker(Task task) {
        return task.broker;
    }

    public QueueBroker getQueueBroker(Task task) {
        return task.queueBroker;
    }

    @Override
    public void run() {
        task.run();
    }
}
