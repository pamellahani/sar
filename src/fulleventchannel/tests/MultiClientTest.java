package fulleventchannel.tests;

import fulleventchannel.ClientCompletionTracker;
import fulleventchannel.EventBroker;
import fulleventchannel.EventPump;
import fulleventchannel.EventTask;

public class MultiClientTest {

    private EchoClient clientRunnable;
    private EchoServer serverRunnable;
    private EventTask client1;
    private EventTask client2;
    private EventTask client3;
    private EventTask client4;
    private EventTask server;

    private void setup() {

        EventBroker serverBroker = new EventBroker("serverBroker");
        EventBroker clientBroker = new EventBroker("clientBroker");

        clientRunnable = new EchoClient(clientBroker);
        serverRunnable = new EchoServer(serverBroker);

        server = new EventTask();
        client1 = new EventTask();
        client2 = new EventTask();
        client3 = new EventTask();
        client4 = new EventTask();


        // Set the total number of clients dynamically
        int totalClients = 4; // Here, 4 clients are being created
        ClientCompletionTracker.setTotalClients(totalClients);
    }

    public static void main(String[] args) {

        MultiClientTest test = new MultiClientTest();

        test.setup();

        test.server.post(test.serverRunnable);

        // Post client tasks
        test.client1.post(test.clientRunnable);
        test.client2.post(test.clientRunnable);
        test.client3.post(test.clientRunnable);
        test.client4.post(test.clientRunnable);

        EventPump.getInstance().run();

        System.out.println("TEST PASSED");
    }
}