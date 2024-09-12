package channels;

abstract class Task extends Thread{

    Broker broker;
    Runnable task;

    public Task(Broker b, Runnable r) {
        this.broker = b;
        this.task = r;
    }

    @Override
    public void run() {
        task.run();
    }

    public static Broker getBroker(Task task) {
        return task.broker;
    }
}
