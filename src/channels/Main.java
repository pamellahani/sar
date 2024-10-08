package channels;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        //BrokerManager brokerManager = new BrokerManager();
        
        // Register brokers in the broker manager
        SimpleBroker serverBroker = new SimpleBroker("ServerBroker");
        SimpleBroker clientBroker = new SimpleBroker("ClientBroker");
        
        // Server task: Accept connection and read message from the client
        Task serverTask = new SimpleTask(serverBroker, () -> {
            try {
                System.out.println("Server waiting for connection...");
                Channel channel = serverBroker.accept(8080);
                if (channel == null) {
                    throw new IllegalStateException("Channel is null");
                }
                byte[] buffer = new byte[1024];
                //Thread.sleep(500);  // Wait for client to send data
                int bytesRead = channel.read(buffer, 0, buffer.length);
                System.out.println("Server received bytes: " + bytesRead);
                if (bytesRead > 0) {
                    String receivedMessage = new String(buffer, 0, bytesRead);
                    System.out.println("Server received: " + receivedMessage);
                    System.out.println("Server received bytes: " + bytesRead);
                }
                //Thread.sleep(500);  // Ensure all data is processed before disconnect
                channel.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // Client task: Connect to the server and send a message
        Task clientTask = new SimpleTask(clientBroker, () -> {
            try {
                //Thread.sleep(1000);  // Ensure server is ready to accept connection
                System.out.println("Client connecting to server...");
                Channel channel = clientBroker.connect("ServerBroker", 8080);
                byte[] message = "Hello World".getBytes();
                int bytesSent = channel.write(message, 0, message.length);
                System.out.println("Client sent bytes: " + bytesSent);
                System.out.println("Client sent: " + new String(message, 0, message.length));
                //Thread.sleep(500);  // Wait to ensure server has time to process data
                channel.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // Start both tasks in separate threads
        serverTask.start();
        clientTask.start();

        // Wait for both tasks to complete
         clientTask.join();
         serverTask.join();

        System.out.println("Communication finished.");
    }
}
