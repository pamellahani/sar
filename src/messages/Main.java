package messages;

import channels.*;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        // Initialize the broker manager
        BrokerManager brokerManager = new BrokerManager();
        
        // Register brokers in the broker manager for server and client
        SimpleBroker serverBroker = new SimpleBroker("ServerBroker", brokerManager);
        SimpleBroker clientBroker = new SimpleBroker("ClientBroker", brokerManager);

        // Initialize queue brokers for server and client using their respective brokers
        QueueBrokerImpl serverQueueBroker = new QueueBrokerImpl(serverBroker);
        QueueBrokerImpl clientQueueBroker = new QueueBrokerImpl(clientBroker);


        // Server task setup
        MessageQueueImpl serverQueue = serverQueueBroker.accept(5000);
        System.out.println("Server queue created.");
        MessageQueueImpl clientQueue = clientQueueBroker.connect("ServerBroker", 5000);


        Task server = new SimpleTask(serverBroker, () -> {
            System.out.println("Server is waiting for messages...");
            byte[] received = serverQueue.receive();
            System.out.println("Server received: " + new String(received));
        });

        // Client task setup
        Task client = new SimpleTask(clientBroker, () -> {
            String message = "Hello from client!";
            clientQueue.send(message.getBytes(), 0, message.getBytes().length);
            System.out.println("Client sent message.");
        });

        // Start both tasks
        server.start();
        client.start();

        // Wait for both to finish
        server.join();
        client.join();
    }
}
