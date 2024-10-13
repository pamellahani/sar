package hybrid.tests;

import hybrid.MessageQueue;
import hybrid.QueueBroker;
import hybrid.tests.message_listeners.EchoServerMessageListener;

public class EchoAcceptListener implements QueueBroker.AcceptListener {

    private QueueBroker queue_broker;
	private int client_counter = 0;
	
	 public EchoAcceptListener(QueueBroker qb) {
		queue_broker = qb;
	}

	@Override
	public void accepted(MessageQueue queue) {
		queue.setListener(new EchoServerMessageListener(queue));
		System.out.println("Client connected.");
	
		// Allow only one client and unbind immediately after
		queue_broker.unbind(8080);
		System.out.println("Server unbound from port 8080 to stop further connections.");
	}
	
}

