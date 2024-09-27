package channels.tests;


import java.util.ArrayList;
import java.util.List;

import channels.*; 

public class MultiClientTest {

    public static void main(String[] args) throws InterruptedException {
        BrokerManager brokerManager = new BrokerManager();

        // Create and register server broker
        SimpleBroker serverBroker = new SimpleBroker("ServerBroker", brokerManager);
        
        // Create server task (this will accept multiple client connections)
        Task serverTask = new SimpleTask(serverBroker, () -> {
            try {
                // The server will listen and accept multiple clients
                while (true) {
                    System.out.println("Server waiting for connection...");
                    Channel channel = serverBroker.accept(8080);
                    if (channel == null) {
                        throw new IllegalStateException("Channel is null");
                    }

                    // Read data from the client
                    byte[] buffer = new byte[1024];
                    int bytesRead = channel.read(buffer, 0, buffer.length);
                    if (bytesRead > 0) {
                        String receivedMessage = new String(buffer, 0, bytesRead);
                        System.out.println("Server received: " + receivedMessage);
                    }

                    // Close the connection
                    channel.disconnect();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // Create multiple client tasks and connect them to the server
        int numClients = 5; // Example: 5 clients
        List<Task> clientTasks = new ArrayList<>();

        for (int i = 1; i <= numClients; i++) {
            String clientName = "ClientBroker" + i;
            SimpleBroker clientBroker = new SimpleBroker(clientName, brokerManager);

            Task clientTask = new SimpleTask(clientBroker, () -> {
                try {
                    System.out.println(clientName + " connecting to server...");
                    Channel channel = clientBroker.connect("ServerBroker", 8080);

                    // Send a message from the client
                    String message = clientName + " says Hello!";
                    byte[] messageBytes = message.getBytes();
                    int bytesSent = channel.write(messageBytes, 0, messageBytes.length);
                    System.out.println(clientName + " sent bytes: " + bytesSent);

                    // Close the connection
                    channel.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            clientTasks.add(clientTask);
        }

        // Start the server in a separate thread
        serverTask.start();

        // Start each client in separate threads
        for (Task clientTask : clientTasks) {
            clientTask.start();
        }

        // Wait for all clients to finish their tasks
        for (Task clientTask : clientTasks) {
            clientTask.join();
        }

        System.out.println("All clients have finished their communication.");
    }
}
