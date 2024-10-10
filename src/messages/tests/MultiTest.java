package messages.tests;

import channels.Broker;
import channels.BrokerManager;
import channels.SimpleBroker;
import messages.QueueBroker;
import messages.QueueBrokerImpl;

public class MultiTest {

    private int numClients = 5;  // Number of clients
    private EchoClient[] clients = new EchoClient[numClients];  // Array to hold clients
    private EchoServer server;

    // Setup method to initialize the server and clients
    private void setup() {

        // Create a broker manager to manage brokers
        BrokerManager manager = new BrokerManager();

        // Create brokers for server and clients
        Broker brokerServer = new SimpleBroker("server", manager);
        QueueBroker queueBrokerServer = new QueueBrokerImpl(brokerServer);

        // Initialize the server with the server-side broker
        this.server = new EchoServer(queueBrokerServer);

        for (int i = 0; i < numClients; i++) {
            Broker brokerClient = new SimpleBroker("client" + i, manager);
            QueueBroker queueBrokerClient = new QueueBrokerImpl(brokerClient);
            clients[i] = new EchoClient(queueBrokerClient);
        }
    }

    public static void main(String[] args) {
        MultiTest test = new MultiTest();
        test.setup();

        test.server.start();

        for (int i = 0; i < test.numClients; i++) {
            test.clients[i].start();
        }

        try {
            for (int i = 0; i < test.numClients; i++) {
                test.clients[i].join();
            }
            test.server.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("TESTS PASSED!");
    }
}