package fullevent.tests;

import fullevent.QueueBroker;

public class EchoClient implements Runnable{
	
	private QueueBroker queue_broker;
	
	public EchoClient(QueueBroker qb) {
		queue_broker = qb;
	}

	@Override
	public void run() {
		queue_broker.connect("serverBroker", 8080, new EchoConnectListener());		
	}
}