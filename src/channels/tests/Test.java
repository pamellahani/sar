package channels.tests;

/*
 * USE CASE: 
 * Multiple EchoClients (here 3) can connect to a single EchoServer, send data, and 
 * receive the same data back (echoed by the server). 
 * 
 * The server listens for client connections and echoes any data it receives. Clients
 * send a byte sequence and verify that the same sequence is echoed back by the server.
 */

public class Test {

    public static void main(String[] args) {
        int serverPort = 8080;

        // Start the Echo server
        EchoServerTest server = new EchoServerTest(serverPort);
        server.startServer();

        // Give the server some time to start (for testing purposes)
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Start multiple clients at the same time and send test messages
        for (int i = 1; i <= 3; i++) {
            String message = "Hello from Client " + i;
            EchoClientTest client = new EchoClientTest(message, serverPort);
            client.startClient();
        }
    }
}
