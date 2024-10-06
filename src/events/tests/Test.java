package events.tests;

public class Test {

    public static void main(String[] args) {
        // Server creation
        EchoServer server = new EchoServer(8080);
        server.start();

        // Client creation
        EchoClient client = new EchoClient("EchoServer", 8080);
        client.start();

        client.stop();
        server.stop();

    }
}
