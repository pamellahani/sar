package hybrid.tests;

import hybrid.QueueBroker;

public class EchoClient implements Runnable{
	
	private QueueBroker queue_broker;
	
	public EchoClient(QueueBroker qb) {
		queue_broker = qb;
	}

	@Override
	public void run() {
		queue_broker.connect("serverBroker", 80, new EchoConnectListener());		
	}
}