package events.tests;

import events.*;

import java.util.List;

public class EchoClient {

    private final Task clientTask;

    public EchoClient(String name, int port, List<String> messagesToSend) {
        // Create a client task using the QueueBroker
        clientTask = new Task(name);

        // Connect the client to the server
        clientTask.queueBroker.connect(name, port, new QueueBroker.ConnectListener() {
            @Override
            public void connected(MessageQueue queue) {
                System.out.println("Client: Connected to the server.");

                // Send each string in the list as a message
                for (String message : messagesToSend) {
                    byte[] messageBytes = message.getBytes();
                    Message msg = new Message(messageBytes, 0, messageBytes.length);
                    queue.send(msg);
                    System.out.println("Client sent: " + message);
                }

                // Set a listener to receive echo responses from the server
                queue.setListener(new MessageQueue.Listener() {
                    @Override
                    public void received(byte[] msg) {
                        System.out.println("Client received echo: " + new String(msg));
                    }

                    @Override
                    public void sent(Message msg) {
                        // Client doesn't need to handle sent event specifically
                    }

                    @Override
                    public void closed() {
                        System.out.println("Client: Server connection closed.");
                    }
                });
            }

            @Override
            public void refused() {
                System.out.println("Client: Connection refused by the server.");
            }
        });
    }
}
