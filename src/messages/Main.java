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
        SimpleQueueBroker serverQueueBroker = new SimpleQueueBroker(serverBroker);
        SimpleQueueBroker clientQueueBroker = new SimpleQueueBroker(clientBroker);


        // Server task setup
        MessageQueue serverQueue = serverQueueBroker.accept(5000);
        System.out.println("Server queue created.");
        MessageQueue clientQueue = clientQueueBroker.connect("ServerBroker", 5000);


        Task server = new SimpleTask(serverBroker, () -> {
            try {
                System.out.println("Server is waiting for messages...");
                byte[] received = serverQueue.receive();
                System.out.println("Server received: " + new String(received));
            } catch (DisconnectedException e) {
                System.out.println("Server disconnected.");
            }
        });

        // Client task setup
        Task client = new SimpleTask(clientBroker, () -> {
            try {
                String message = "Hello from client!";
                clientQueue.send(message.getBytes(), 0, message.getBytes().length);
                System.out.println("Client sent message.");
            } catch (DisconnectedException e) {
                System.out.println("Client disconnected.");
            }
        });

        // Start both tasks
        server.start();
        client.start();

        // Wait for both to finish
        server.join();
        client.join();
    }
}
