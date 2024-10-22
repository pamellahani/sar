package fulleventmessage.tests.message_listeners;


import fulleventmessage.MessageQueue.MessageListener;
import hybrid.Message;

public class EchoClientMessageListener implements MessageListener {

    @SuppressWarnings("unused")
    private Message message;

    public EchoClientMessageListener(Message msg) {
        this.message = msg;
    }

    @Override
    public void received(byte[] bytes) {
        System.out.println("Client received message: " + new String(bytes));
    }

    @Override
    public void sent(Message msg) {
        System.out.println("Client sent message: " + new String(msg.getBytes()));
    }

    @Override
    public void closed() {
        System.out.println("Client queue closed.");
    }
}
