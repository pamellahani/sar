package hybrid.tests.message_listeners;


import hybrid.EventPump;
import hybrid.EventTask;
import hybrid.Message;
import hybrid.MessageQueue;
import hybrid.MessageQueue.Listener;

public class EchoServerMessageListener implements Listener{
	
	private MessageQueue message_queue;
	private static int cpt = 0;
	
	public EchoServerMessageListener(MessageQueue mq) {
		message_queue = mq;
	}

    @Override
	public void received(byte[] bytes) {
		EventTask task = new EventTask();
		task.post(() -> {
			message_queue.send(new Message(bytes, 0, bytes.length));
			System.out.println("Server received and echoed the message.");

			// Stop the EventPump after processing the message
			EventPump.getInstance().stopPump();
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
