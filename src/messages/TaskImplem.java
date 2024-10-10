package messages;

public class TaskImplem extends Task {


    public TaskImplem(QueueBroker b, Runnable r) {
        super(b, r);
    }

    @Override
    public void run() {
        task.run();
    }

    @Override
    public QueueBroker getQueueBroker() {
        return this.qbroker;
    }
    
}
