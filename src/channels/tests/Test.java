package channels.tests;

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
