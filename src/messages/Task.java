package messages;

public abstract class Task extends Thread{

    protected QueueBroker broker;
    protected Runnable task;

    public Task(QueueBroker b, Runnable r) {
        this.broker = b;
        this.task = r;
    }

    public abstract void run(); 

    protected abstract QueueBroker getQueueBroker(); 

    protected static Task getTask() {
        return (Task) Thread.currentThread();
    }


    
}
