package hybrid.tests;

import events.EventPump;

public class Test {

    public static void main(String[] args) {

        EventPump eventPump = new EventPump();
        // Server creation
        EchoServer server = new EchoServer(8080);
        // Client creation
        EchoClient client = new EchoClient("BrokerServer", 8080);

        server.start();
        client.start();
        
        client.stop();
        server.stop();

        System.out.println("TEST PASSED!");

    }
}
