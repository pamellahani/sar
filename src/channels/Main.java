package channels;

public class Main {
    public static void main(String[] args) throws InterruptedException {

        // Register brokers in the broker manager
        BrokerManager brokerManager = new BrokerManager(); 

        // Create the broker for the client and server
        SimpleBroker serverBroker = new SimpleBroker("ServerBroker", brokerManager);
        SimpleBroker clientBroker = new SimpleBroker("ClientBroker", brokerManager);;
        
        brokerManager.registerBroker(serverBroker);
        brokerManager.registerBroker(clientBroker);

        // Server task: Accept connections and read the message from the client
        Task serverTask = new Task(serverBroker, () -> {
            try {
                System.out.println("Server waiting for connection...");
                Channel channel = serverBroker.accept(8080);
                byte[] buffer = new byte[1024];
                int bytesRead = channel.read(buffer, 0, buffer.length);
                if (bytesRead > 0) {
                    String receivedMessage = new String(buffer, 0, bytesRead);
                    System.out.println("Server received: " + receivedMessage);
                    System.out.println("Server received bytes: " + bytesRead);
                }
                channel.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }) {};

        Thread.sleep(1000);  // Ensure server is ready before the client starts

        // Client task: Connect to the server and send a message
        Task clientTask = new Task(clientBroker, () -> {
            try {
                System.out.println("Client connecting to server...");
                Channel channel = clientBroker.connect("ServerBroker", 8080);
                byte[] message = "Hello World".getBytes();
                int bytesSent = channel.write(message, 0, message.length);
                System.out.println("Client sent: Hello World");
                System.out.println("Client sent bytes: " + bytesSent);
                channel.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }) {};

        // Run the server and client tasks in separate threads
        serverTask.start();
        Thread.sleep(1000);  // Ensure server is ready before the client starts
        clientTask.start();

        // Wait for both tasks to complete
        serverTask.join();
        clientTask.join();

        System.out.println("Communication finished.");
    }
}
