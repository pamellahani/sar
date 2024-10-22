package fullevent.tests;

import fullevent.EventBroker;

public class EchoServer implements Runnable{

	private EventBroker eventBroker;
	
	public EchoServer(EventBroker broker) {
		eventBroker = broker;
	}

	@Override
	public void run() {
		eventBroker.bind(8080, new EchoAcceptListener());
		// if (success) {
		// 	System.out.println("Server successfully bound to port 8080");
		// } else {
		// 	System.out.println("Failed to bind server to port 8080");
		// }
	}
	

}