package hybrid.tests.message_listeners;


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
		task.post(new Runnable() {
			
			@Override
			public void run() {
				message_queue.send(new Message(bytes,0,bytes.length));
				
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
		
		assert(message_queue != null) : "Server queue not initialized";
		assert(message_queue.closed() == true) : "Server queue not disconnected";
		
		if(cpt++ >= 2) {
			System.out.println("Server passed");
		}

	}
}
