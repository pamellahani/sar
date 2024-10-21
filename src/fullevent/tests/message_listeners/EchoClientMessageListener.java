package fullevent.tests.message_listeners;

import fullevent.MessageQueue;
import fullevent.MessageQueue.MessageListener;
import hybrid.EventPump;
import hybrid.Message;


public class EchoClientMessageListener implements MessageListener{

	private MessageQueue private_queue;
	private Message private_message;
	//private static int cpt = 0;


	public EchoClientMessageListener(MessageQueue messageQueue, Message message) {
		private_queue = messageQueue;
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
