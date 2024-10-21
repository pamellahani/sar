package hybrid.tests.message_listeners;


import hybrid.MessageQueue;
import hybrid.MessageQueue.MessageListener;
import hybrid.EventPump;
import hybrid.EventTask;
import hybrid.Message;

public class EchoServerMessageListener implements MessageListener{
	
	private MessageQueue message_queue;
	//private static int cpt = 0;
	
	public EchoServerMessageListener(MessageQueue queue) {
		message_queue = queue;
	}

    @Override
	public void received(byte[] bytes) {
		EventTask task = new EventTask();
		task.post(() -> {
			message_queue.send(new Message(bytes, 0, bytes.length));
			System.out.println("Server received and echoed the message.");

			// Stop the EventPump only after ensuring message has been processed
			if (message_queue.closed()) {
				EventPump.getInstance().stopPump();
			}
		});
	}


	@Override
	public void closed() {
		message_queue.close();
	}

	@Override
	public void sent(Message message) {
		message_queue.close();
	
		assert (message_queue != null) : "Server queue not initialized";
		assert (message_queue.closed() == true) : "Server queue not disconnected";
	
		System.out.println("Server sent message back successfully.");
	
		// Stop the pump after the message is sent back to the client
		EventPump.getInstance().stopPump();
	}
	
}
