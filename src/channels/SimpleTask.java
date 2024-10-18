package channels;

public class SimpleTask extends Task 
{

    public SimpleTask(Broker b, Runnable r) {
        super(b, r);
    }

    @Override
    public void run() {
        task.run();
    }

    public static Broker getBroker(Task task) {
        return task.broker;
    }

    

    
}
