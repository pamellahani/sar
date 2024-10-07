package events.tests;

import events.EventPump;

public class Test {

    public static void main(String[] args) {

        EventPump eventPump = new EventPump();
        // Server creation
        EchoServer server = new EchoServer(8080);
        server.start();

        // Client creation
        EchoClient client = new EchoClient("BrokerServer", 8080);
        client.start();

        client.stop();
        server.stop();

    }
}
