package events.tests;

import java.util.List;

public class Test {

    public static void main(String[] args) {
        int port = 8080;
        String serverName = "EchoServer";
        String clientName = "EchoClient";

        // Create and start the Echo Server
        EchoServer server = new EchoServer(serverName, port);

        // Define messages to be sent by the client
        List<String> messages = List.of("Hello", "World", "Echo Test");

        // Create and start the Echo Client
        EchoClient client = new EchoClient(clientName, port, messages);
        System.out.println("client registered");

        
    }
}
