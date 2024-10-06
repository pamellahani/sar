package events.tests;

import events.QueueBroker;
import events.MixedQueueBroker;
import events.MessageQueue;
import events.Message;

public class EchoClient {
    private final String host;
    private final int port;
    private final MixedQueueBroker broker;

    public EchoClient(String host, int port) {
        this.host = host;
        this.port = port;
        this.broker = new MixedQueueBroker("clientBroker");
    }

    public void start() {
        broker.connect(host, port, new QueueBroker.ConnectListener() {
            @Override
            public void connected(MessageQueue queue) {
                System.out.println("Client: Connected to server.");
                String message = "Hello, Server!";
                queue.send(new Message(message.getBytes(), 0, message.length()));

                queue.setListener(new MessageQueue.Listener() {
                    @Override
                    public void received(byte[] msg) {
                        String response = new String(msg);
                        System.out.println("Client received: " + response);
                        queue.close();  // Close connection after receiving the echo
                        stop();  // Stop the client after closing
                    }

                    @Override
                    public void sent(Message msg) {
                        System.out.println("Client: Sent message to server.");
                    }

                    @Override
                    public void closed() {
                        System.out.println("Client: Connection closed.");
                    }
                });
            }

            @Override
            public void refused() {
                System.out.println("Client: Connection refused.");
            }
        });
    }

    public void stop() {
        System.out.println("Client: Shutting down.");
        // Add any additional cleanup logic here if necessary
    }
}
