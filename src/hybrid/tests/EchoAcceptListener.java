package hybrid.tests;

import hybrid.EventPump;
import hybrid.MessageQueue;
import hybrid.QueueBroker;
import hybrid.tests.message_listeners.EchoServerMessageListener;

public class EchoAcceptListener implements QueueBroker.AcceptListener {

    private QueueBroker queue_broker;
	//private int client_counter = 0;
	
	 public EchoAcceptListener(QueueBroker qb) {
		queue_broker = qb;
	}

	@Override
	public void accepted(MessageQueue queue) {
		queue.setListener(new EchoServerMessageListener(queue));
		System.out.println("Client connected.");
	
		// Unbind immediately after accepting one client to prevent multiple connections
		queue_broker.unbind(8080);

		//pump should be stopped after receiving the response
		queue.getMessageListener().received(new byte[0]); 
		
		System.out.println("Server unbound from port 8080 to stop further connections.");

		EventPump.getInstance().stopPump();
	}
	
	
}

