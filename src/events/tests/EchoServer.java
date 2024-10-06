package events.tests;

import events.*;

public class EchoServer {

    private final Task serverTask;

    public EchoServer(String name, int port) {
        // Create a server task using the QueueBroker
        serverTask = new Task(name);

        // Bind the server to a port and set up the accept listener
        serverTask.queueBroker.bind(port, queue -> {
            System.out.println("Server: Client connected.");

            // Set a listener to echo messages back to the client
            queue.setListener(new MessageQueue.Listener() {
                @Override
                public void received(byte[] msg) {
                    System.out.println("Server received: " + new String(msg));

                    // Echo the received message back to the client
                    Message message = new Message(msg, 0, msg.length);
                    queue.send(message);
                }

                @Override
                public void sent(Message msg) {
                    System.out.println("Server echoed: " + new String(msg.bytes));
                }

                @Override
                public void closed() {
                    System.out.println("Server: Client connection closed.");
                }
            });
        });
    }
}
