package hybrid.tests;

import hybrid.EventPump;
import hybrid.EventTask;
import hybrid.MixedQueueBroker;
import hybrid.QueueBroker;

public class OneClientOneServerTest {
	
	// define client tasks
	private EventTask client; 
	private EventTask server;
	
	private EchoClient runnable_client;
	
	private EchoServer runnable_server;
	
	
	private void setup() {

	    server = new EventTask();

		QueueBroker serverBroker = new MixedQueueBroker("serverBroker");
	    QueueBroker clientBroker = new MixedQueueBroker("clientBroker");
		runnable_client = new EchoClient(clientBroker);
		runnable_server = new EchoServer(serverBroker);
		
	    client = new EventTask();
		
		EventPump.getInstance().start();
		
	}
	
	public static void main(String[] args) {
		
		OneClientOneServerTest hybrid_system = new OneClientOneServerTest();

		hybrid_system.setup();
		
		hybrid_system.client.post(hybrid_system.runnable_client);
		hybrid_system.server.post(hybrid_system.runnable_server);
		
		try {
			EventPump.getInstance().join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		System.out.println("TEST PASSED!");
			
				
	}
}