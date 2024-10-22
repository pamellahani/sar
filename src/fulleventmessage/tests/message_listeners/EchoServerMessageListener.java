package fulleventmessage.tests.message_listeners;

import fulleventmessage.MessageQueue.MessageListener;
import hybrid.Message;

public class EchoServerMessageListener implements MessageListener {

    @Override
    public void received(byte[] bytes) {
        System.out.println("Server received message: " + new String(bytes));
    }

    @Override
    public void sent(Message msg) {
        System.out.println("Server sent message: " + new String(msg.getBytes()));
    }

    @Override
    public void closed() {
        System.out.println("Server queue closed.");
    }
}
