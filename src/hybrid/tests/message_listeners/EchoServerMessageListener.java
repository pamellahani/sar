package hybrid.tests.message_listeners;


import hybrid.EventTask;
import hybrid.Message;
import hybrid.MessageQueue;
import hybrid.MessageQueue.Listener;

public class EchoServerMessageListener implements Listener{
	
	private MessageQueue _queue;
	private static int cpt = 0;
	
	public EchoServerMessageListener(MessageQueue queue) {
		_queue = queue;
	}

    @Override
	public void received(byte[] bytes) {
		
		EventTask task = new EventTask();
		task.post(new Runnable() {
			
			@Override
			public void run() {
				_queue.send(new Message(bytes,0,bytes.length));
				
			}
		});
		
	}

	@Override
	public void closed() {
		_queue.close();
	}

	@Override
	public void sent(Message message) {
		_queue.close();
		
		assert(_queue != null) : "Server queue not initialized";
		assert(_queue.closed() == true) : "Server queue not disconnected";
		
		if(cpt++ >= 2) {
			System.out.println("Server passed");
		}

	}
}
