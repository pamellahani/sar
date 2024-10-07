package messages.tests;

import channels.Broker;
import channels.Task;
import messages.MessageQueue;
import messages.QueueBroker;

public class EchoServer extends Task{
	
	public EchoServer(QueueBroker b) {
		super(b, () ->{
			EchoServer client = (EchoServer) EchoServer.getTask();
			
			QueueBroker broker = client.getQueueBroker();
						
			for(int i = 0; i < 3; i++) {
				
				MessageQueue messageQueue = broker.accept(80);
				
				byte[] message = messageQueue.receive();

				messageQueue.send(message, 0, message.length);
						
				messageQueue.close();
		
				assert(messageQueue != null) : "cannot create server channel, no connection";
				assert(messageQueue.closed() == true) : "cannot close server channel, still connected";
				
			}
		});
	}
    
}
