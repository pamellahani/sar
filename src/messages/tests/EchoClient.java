package messages.tests;

import java.util.UUID;

import messages.Task;
import messages.MessageQueue;
import messages.QueueBroker;

public class EchoClient extends Task{

	
	public EchoClient(QueueBroker b) {
		super(b, () ->{

			EchoClient client = (EchoClient) EchoClient.getTask();
			QueueBroker qbroker = client.getQueueBroker();
			
			String message = UUID.randomUUID().toString();
			byte[] messageBytes = message.getBytes();
	
			MessageQueue messageQueue = qbroker.connect("server", 8080);
		
			messageQueue.send(messageBytes, 0, messageBytes.length);

            //System.out.println("Client sent: " + string);
			
			byte[] response = messageQueue.receive();
			messageQueue.close();
	
			for(int i = 0; i < messageBytes.length; i++){
				assert(response[i] == messageBytes[i]);
			}	
	
			assert(messageQueue != null); 
			assert(messageQueue.closed() == true); 
		});
	}

    @Override
    public void run() {
       this.task.run();
    }

    @Override
    protected QueueBroker getQueueBroker() {
        return this.qbroker;
    }
}
