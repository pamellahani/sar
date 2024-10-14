package hybrid.tests.message_listeners;

import hybrid.EventPump;
import hybrid.Message;
import hybrid.MessageQueue;
import hybrid.MessageQueue.Listener;

public class EchoClientMessageListener implements Listener{

	private MessageQueue private_queue;
	private Message private_message;
	//private static int cpt = 0;


	public EchoClientMessageListener(MessageQueue queue, Message message) {
		private_queue = queue;
		private_message = message;
	}


    @Override
    public void received(byte[] msg) {
        // Validate that the received message is identical to the sent one
        for (int i = 0; i < private_message.getLength(); i++) {
            assert (msg[i] == private_message.getByteAt(i)) : "Data received different from the one sent: " + i;
        }

        System.out.println("Client received:"+ new String(msg));

        // Stop the EventPump after receiving the response
        EventPump.getInstance().stopPump();
    }


	@Override
	public void sent(Message msg) {
		System.out.println("Message sent: " + new String(msg.getBytes(), msg.getOffset(), msg.getLength()));
	}


    @Override
    public void closed() {
        private_queue.close();
    }
    
}
