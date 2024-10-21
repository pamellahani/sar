package fullevent.tests;

import fullevent.EventQueueBroker;
import hybrid.EventPump;
import hybrid.EventTask;
import hybrid.QueueBroker;

public class OneClientTest {

    private EventTask clientTask; 
    private EventTask serverTask;

    private EchoClient runnableClient;
    private EchoServer runnableServer;

    private QueueBroker serverBroker;
    private QueueBroker clientBroker;

    private void setup() {
        // Set up Event Tasks
        serverTask = new EventTask();
        clientTask = new EventTask();

        // Initialize QueueBrokers
        serverBroker = new EventQueueBroker("serverBroker");
        clientBroker = new EventQueueBroker("clientBroker");

        // Create Server and Client Runnables
        runnableClient = new EchoClient(clientBroker);
        runnableServer = new EchoServer(serverBroker);

        // Start the Event Pump
        EventPump.getInstance().start();

        // Post the server task first
        serverTask.post(() -> {
            System.out.println("Starting server...");
            runnableServer.run();
        });

        // Register a listener for when the server is bound, then post the client task
        ((EventQueueBroker) serverBroker).setOnBoundListener(() -> {
            System.out.println("Server bound. Starting client...");
            clientTask.post(() -> {
                runnableClient.run();
            });
        });
    }

    public static void main(String[] args) {
        OneClientTest fullEvent = new OneClientTest();
        fullEvent.setup();

        // Wait for the event pump to complete
        try {
            EventPump.getInstance().join();
            System.out.println("TEST PASSED!");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
