package fulleventmessage.tests;

import fulleventmessage.QueueBroker;
import fulleventmessage.tests.message_listeners.EchoClientMessageListener;
import hybrid.Message;
import java.util.UUID;

public class EchoClient implements Runnable {

    private QueueBroker broker;

    public EchoClient(QueueBroker broker) {
        this.broker = broker;
    }

    @Override
    public void run() {
        // Create a random message
        Message msg = new Message(UUID.randomUUID().toString().repeat(10).getBytes(), 0, 100);
        
        // Set message listener
        EchoClientMessageListener messageListener = new EchoClientMessageListener(msg);

        // Connect to server
        EchoConnectListener connectListener = new EchoConnectListener(messageListener, msg);
        broker.connect("serverBroker", 8080, connectListener);
    }
}
