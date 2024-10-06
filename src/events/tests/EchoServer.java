package events.tests;

import events.QueueBroker;
import events.MixedQueueBroker;
import events.MessageQueue;

public class EchoServer {
    private final int port;
    private final MixedQueueBroker broker;

    public EchoServer(int port) {
        this.port = port;
        this.broker = new MixedQueueBroker("EchoServer");
    }

    public void start() {
        broker.bind(port, new QueueBroker.AcceptListener() {
            @Override
            public void accepted(MessageQueue queue) {
                System.out.println("Server: Client connected.");
                queue.setListener(new MessageQueue.Listener() {
                    @Override
                    public void received(byte[] msg) {
                        String message = new String(msg);
                        System.out.println("Server received: " + message);
                        // Echo the message back to the client
                        queue.send(new events.Message(msg, 0, msg.length));
                    }

                    @Override
                    public void sent(events.Message msg) {
                        System.out.println("Server: Echoed back the message.");
                    }

                    @Override
                    public void closed() {
                        System.out.println("Server: Client connection closed.");
                        stop();  // Stop the server when the client disconnects
                    }
                });
            }
        });
    }

    public void stop() {
        System.out.println("Server: Shutting down.");
        // Add any additional cleanup logic here if necessary
    }
}
