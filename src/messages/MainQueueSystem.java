package messages;

import channels.BrokerManager;
import channels.SimpleBroker;
import channels.SimpleChannel;
import channels.SimpleTask;
import channels.Task;

public class MainQueueSystem {
    public static void main(String[] args) throws InterruptedException {
        QueueBrokerManager queueBrokerManager = new QueueBrokerManager();
        
        // Create brokers for client and server
        SimpleBroker serverBroker = new SimpleBroker("ServerBroker", new BrokerManager());
        SimpleBroker clientBroker = new SimpleBroker("ClientBroker", new BrokerManager());


        // Server task: Accept connection and read message from request queue, then send response
        Task serverTask = new SimpleTask(serverBroker, () -> {
            try {
                System.out.println("Server waiting for connection...");
                SimpleChannel channel = (SimpleChannel) serverBroker.accept(8080);
                
                // Receive request
                byte[] receivedMessage = channel.getRequestQueue().receive();
                System.out.println("Server received: " + new String(receivedMessage));
                
                // Send response
                byte[] response = "Response from Server".getBytes();
                channel.getResponseQueue().send(response, 0, response.length);
                
                channel.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // Client task: Connect to the server and send a message, then wait for response
        Task clientTask = new SimpleTask(clientBroker, () -> {
            try {
                System.out.println("Client connecting to server...");
                SimpleChannel channel = (SimpleChannel) clientBroker.connect("ServerBroker", 8080);
                
                // Send request
                byte[] message = "Hello from Client".getBytes();
                channel.getRequestQueue().send(message, 0, message.length);
                
                // Receive response
                byte[] response = channel.getResponseQueue().receive();
                System.out.println("Client received: " + new String(response));
                
                channel.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // Start both tasks
        serverTask.start();
        clientTask.start();

        // Wait for both to finish
        serverTask.join();
        clientTask.join();

        System.out.println("Message Queue Communication finished.");
    }
}
