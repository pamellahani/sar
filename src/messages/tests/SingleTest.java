package messages.tests;

import channels.Broker;
import channels.SimpleBroker;
import messages.QueueBroker;
import messages.QueueBrokerImpl;


//Test for a single client and server

public class SingleTest {

    private EchoClient client; 
	private EchoServer server;
	
	private void setup() {

		//Create a broker manager to manage brokers
        //BrokerManager manager = new BrokerManager();
		
		Broker brokerClient = new SimpleBroker("client"); 
		Broker brokerServer = new SimpleBroker("server");
		
		QueueBroker queueBrokerClient = new QueueBrokerImpl(brokerClient);
		QueueBroker queueBrokerServer = new QueueBrokerImpl(brokerServer);
		
		//create and initialize the server with the server-side broker and the client with the client-side broker
        this.server = new EchoServer(queueBrokerServer);
		this.client = new EchoClient(queueBrokerClient);
	
		
	}
	
	public static void main(String[] args) {
		
		SingleTest test = new SingleTest();
		test.setup();
		
		test.client.start();
		test.server.start();
		
		//wait for the client and server to finish execution
		try{
			test.server.join();
			test.client.join();
		
		}catch(InterruptedException e){
			e.printStackTrace();
		}
		
		System.out.println("TEST PASSED");
	
	}
    
}
