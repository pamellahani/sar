package hybrid.tests;

import java.util.UUID;

import events.*; 

public class EchoClient {
    private final String host;
    private final int port;
    //private final MixedQueueBroker qbroker;
    private final Task clientTask;  // Add Task to manage client tasks

    public EchoClient(String host, int port) {
        this.host = host;
        this.port = port;
        //this.qbroker = new MixedQueueBroker("clientBroker");
        this.clientTask = new Task("BrokerClient");  // Create a new Task
    }

    public void start() {
        clientTask.post(() -> {
            clientTask.queueBroker.connect(host, port, new QueueBroker.ConnectListener() {


                @Override
                public void connected(MessageQueue queue) {
                    System.out.println("Client: Connected to server.");
                    String message = UUID.randomUUID().toString();
                    queue.send(new Message(message.getBytes(), 0, message.length()));
                    queue.setListener(new MessageQueue.Listener() {
                        @Override
                        public void received(byte[] msg) {
                            String response = new String(msg);
                            System.out.println("Client received: " + response);
                            queue.close();
                            stop();
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
        });
        clientTask.start(); 
    }

    public void stop() {
        System.out.println("Client: Shutting down.");
        clientTask.kill();  
        
    }
}
