package channels;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        
        BrokerManager brokerManager = new BrokerManager();
        Broker serverBroker = new SimpleBroker("EchoServer", brokerManager);
        brokerManager.registerBroker(serverBroker);

        SimpleTask serverTask = new SimpleTask(serverBroker, () -> {
            try {
                startEchoServer(8080, brokerManager);  
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        serverTask.start();  
        
        // Delay the client startup to ensure server is ready
        Thread.sleep(1000);
        
       
        // Create two client tasks
        Broker  client1Broker = new SimpleBroker("ClientBroker1", brokerManager);
        brokerManager.registerBroker(client1Broker);

        // Broker client2Broker = new SimpleBroker("ClientBroker2", brokerManager);
        //brokerManager.registerBroker(client2Broker);

        // Start client task
        SimpleTask clientTask1 = new SimpleTask(client1Broker, () -> {
            try {
                startEchoClient("Client 1", 8080, "Hello from Client 1", brokerManager);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        clientTask1.start();

    }


    public static void startEchoServer(int port, BrokerManager brokerManager) throws InterruptedException {
        Broker serverBroker = brokerManager.getBroker("EchoServer");
        brokerManager.registerBroker(serverBroker);
    
        while (true) {
            System.out.println("Server is waiting for connections on port " + port + "...");
            Channel serverChannel = serverBroker.accept(port);  // Blocking accept
            System.out.println("Server accepted a connection.");
    
            // Handle each client connection with a SimpleTask
            SimpleTask clientHandlerTask = new SimpleTask(serverBroker, () -> {
                try {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
    
                    // Echo loop
    
                    while ((bytesRead = serverChannel.read(buffer, 0, buffer.length)) > 0) {
                        System.out.println("Server received: " + new String(buffer, 0, bytesRead));
                        // Echo the message back to the client
                        serverChannel.write(buffer, 0, bytesRead);
                        System.out.println("Server echoed: " + new String(buffer, 0, bytesRead));
                    }

                    if (serverChannel.disconnected()) {
                        System.out.println("Connection closed by the client.");
                    }

    
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    serverChannel.disconnect();
                    System.out.println("Connection closed by the server.");
                }
            });
    
            // Start the client handler task
            clientHandlerTask.start();
    
            // Add a small delay to avoid rapid reconnect loop
            Thread.sleep(500);  
        }
    }
    

    public static void startEchoClient(String clientName, int port, String message, BrokerManager brokerManager) throws InterruptedException {
        Broker clientBroker = new SimpleBroker(clientName, brokerManager);
        brokerManager.registerBroker(clientBroker);
    
        // Connect to the server
        Channel clientChannel = clientBroker.connect("EchoServer", port);
        System.out.println(clientName + " connected to the server.");
    
        // Send a message to the server
        byte[] messageBytes = message.getBytes();
        clientChannel.write(messageBytes, 0, messageBytes.length);
        System.out.println(clientName + " sent: " + message);
    
        // Wait for the echoed message from the server
        byte[] buffer = new byte[1024];
        int bytesRead = clientChannel.read(buffer, 0, buffer.length);
    
        if (bytesRead > 0) {
            String receivedMessage = new String(buffer, 0, bytesRead);
            System.out.println(clientName + " received echo: " + receivedMessage);
        }
    
        // Sleep briefly before disconnecting
        Thread.sleep(1000);  
     
        // Disconnect the client after communication
        clientChannel.disconnect();
        System.out.println(clientName + " disconnected from the server.");
    }
    
    

}
    

