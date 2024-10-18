package hybrid.tests;

import hybrid.EventPump;
import hybrid.EventTask;
import hybrid.MixedQueueBroker;
import hybrid.QueueBroker;

public class MultiClientOneServerTest {
	
	// define client tasks
	private EventTask client1;
	private EventTask client2;
	private EventTask client3; 
	private EventTask server;
	
	private EchoClient runnable_client;
	
	private EchoServer runnable_server;
	
	
	private void setup() {

	    server = new EventTask();

		QueueBroker serverBroker = new MixedQueueBroker("serverBroker");
	    QueueBroker clientBroker = new MixedQueueBroker("clientBroker");
		runnable_client = new EchoClient(clientBroker);
		runnable_server = new EchoServer(serverBroker);
		
	    client1 = new EventTask();
		client2 = new EventTask(); 
		client3 = new EventTask();
		
		EventPump.getInstance().start();
		
	}
	
	public static void main(String[] args) {
		MultiClientOneServerTest hybrid_system = new MultiClientOneServerTest();
		hybrid_system.setup();
		
		// Post client and server runnables to event pump
		hybrid_system.client1.post(hybrid_system.runnable_client);
		hybrid_system.client2.post(hybrid_system.runnable_client);
		hybrid_system.client3.post(hybrid_system.runnable_client);
		hybrid_system.server.post(hybrid_system.runnable_server);
	
		// Wait for the event pump to complete
		try {
			EventPump.getInstance().join();
			System.out.println("TEST PASSED!");
			
		} catch (InterruptedException e) {
			//e.printStackTrace();
		}
	}

}