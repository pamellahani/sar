package channels.tests;

import channels.*;

public class SimpleTaskTest {
    public static void main(String[] args) throws InterruptedException {
        // Test 1: Running a task
        Broker broker = new SimpleBroker("TestBroker", new BrokerManager());
        Runnable task = () -> System.out.println("Task is running");
        SimpleTask simpleTask = new SimpleTask(broker, task);
        simpleTask.start();
        simpleTask.join();  // Ensure the task completes

        // Test 2: Retrieve broker from task
        Broker retrievedBroker = SimpleTask.getBroker(simpleTask);
        assert retrievedBroker.getName().equals("TestBroker") : "Broker name should be 'TestBroker'";
    }
}
