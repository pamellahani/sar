package hybrid.tests;

import channels.Task;
import hybrid.*;
import hybrid.Message;
import hybrid.MessageQueue;
import hybrid.QueueBroker; 

public class EchoServer {
    private final int port;
   // private final MixedQueueBroker broker;
    private final EventTask serverTask; 

    public EchoServer(int port) {
        this.port = port;
        this.serverTask = new EventTask("BrokerServer"); 
        //this.broker = new MixedQueueBroker("EchoServer");
    }

    public void start() {
        serverTask.post(() -> {
            serverTask.queueBroker.bind(port, new QueueBroker.AcceptListener() {


                @Override
                public void accepted(MessageQueue queue) {
                    System.out.println("Server: Client connected.");
                    queue.setListener(new MessageQueue.Listener() {
                        @Override
                        public void received(byte[] msg) {
                            String message = new String(msg);
                            System.out.println("Server received: " + message);
                            queue.send(new Message(msg, 0, msg.length));
                        }

                        @Override
                        public void sent(Message msg) {
                            System.out.println("Server: Echoed back the message.");
                        }

                        @Override
                        public void closed() {
                            System.out.println("Server: Client connection closed.");
                            stop();
                        }
                    });
                }
            });
        });
        serverTask.start();  // Start the task's event processing
    }

    public void stop() {
        System.out.println("Server: Shutting down.");
        serverTask.kill(); 
    }
}
