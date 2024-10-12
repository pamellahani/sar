package hybrid.tests;

import java.util.UUID;

import hybrid.Message;
import hybrid.MessageQueue;
import hybrid.QueueBroker;
import hybrid.tests.message_listeners.EchoClientMessageListener;

public class EchoConnectListener implements QueueBroker.ConnectListener {

@Override
	public void connected(MessageQueue messageQueue) {
		
		String msg = "Hello  Server";
        Message message = new Message(msg.getBytes(), 0, msg.length());
		
		messageQueue.setListener(new EchoClientMessageListener(messageQueue, message));
		
		messageQueue.send(message);
	}

	@Override
	public void refused() {
		System.out.println("Server: Connection refused by the server.");
	}
}
