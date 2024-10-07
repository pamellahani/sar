package messages.tests;

import java.util.UUID;

import channels.Broker;
import channels.Task;
import messages.MessageQueue;
import messages.QueueBroker;

public class EchoClient extends Task{

	
	public EchoClient(QueueBroker b) {
		super(b, () ->{
			EchoClient client = (EchoClient) EchoClient.getTask();
			
			QueueBroker broker = client.getQueueBroker();
			
			String string = UUID.randomUUID().toString().repeat(10);
			byte[] message = string.getBytes();
	
			MessageQueue messageQueue = broker.connect("serveur", 80);
			
			messageQueue.send(message, 0, message.length);
			
			byte[] response = messageQueue.receive();
	
			messageQueue.close();
	
	
			//Tests
			for(int i = 0; i < message.length; i++){
				assert(response[i] == message[i]) : "Data recieved different from the one sent : " + i;
			}	
	
			assert(messageQueue != null) : "Client Channel not initialized";
			assert(messageQueue.closed() == true) : "Client Channel not disconnected";
		});
	}
}
