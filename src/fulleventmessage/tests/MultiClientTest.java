package fulleventmessage.tests;

import fulleventchannel.ClientCompletionTracker;
import fulleventchannel.EventPump;
import fulleventchannel.EventTask;
import fulleventmessage.EventQueueBroker;

public class MultiClientTest {

    private EchoClient clientRunnable;
    private EchoServer serverRunnable;
    private EventTask client1;
    private EventTask client2;
    private EventTask client3;
    private EventTask server;

    private void setup() {
        EventQueueBroker serverBroker = new EventQueueBroker("serverBroker");
        EventQueueBroker clientBroker = new EventQueueBroker("clientBroker");

        clientRunnable = new EchoClient(clientBroker);
        serverRunnable = new EchoServer(serverBroker);

        server = new EventTask();
        client1 = new EventTask();
        client2 = new EventTask();
        client3 = new EventTask();

        // Set the total number of clients dynamically
        int totalClients = 3;
        ClientCompletionTracker.setTotalClients(totalClients);
    }

    public static void main(String[] args) {

        MultiClientTest test = new MultiClientTest();
        test.setup();

        // Start server
        test.server.post(test.serverRunnable);

        // Start clients
        test.client1.post(test.clientRunnable);
        test.client2.post(test.clientRunnable);
        test.client3.post(test.clientRunnable);

        EventPump.getInstance().run();
        System.out.println("TEST PASSED");
    }
}
