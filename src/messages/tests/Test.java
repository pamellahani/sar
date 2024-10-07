package messages.tests;

import channels.Broker;
import channels.BrokerManager;
import channels.SimpleBroker;
import messages.QueueBroker;
import messages.QueueBrokerImpl;

public class Test {

    private EchoClient client1;
	private EchoClient client2;
	private EchoServer server;
	
	private void setup() {

        BrokerManager manager = new BrokerManager();
		
		Broker brokerClient = new SimpleBroker("client", manager); 
		Broker brokerServer = new SimpleBroker("server", manager);
		
		QueueBroker queueBrokerClient = new QueueBrokerImpl(brokerClient);
		QueueBroker queueBrokerServer = new QueueBrokerImpl(brokerServer);
		
        this.server = new EchoServer(queueBrokerServer);
		this.client1 = new EchoClient(queueBrokerClient);
		this.client2 = new EchoClient(queueBrokerClient);
		
	}
	
	public static void main(String[] args) {
		
		Test test = new Test();
		
		test.setup();
		
		test.client1.start();
		test.client2.start();
		
		test.server.start();
		
		try{
			test.server.join();
			
			test.client1.join();
			test.client2.join();
			
	
		}catch(InterruptedException e){
			e.printStackTrace();
		}
		
		System.out.println("TEST PASSED");
	
	}
    
}
