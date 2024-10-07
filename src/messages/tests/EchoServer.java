package messages.tests;


import messages.Task;
import messages.MessageQueue;
import messages.QueueBroker;

public class EchoServer extends Task{
	
	public EchoServer(QueueBroker b) {
		super(b, () ->{
			EchoServer client = (EchoServer) EchoServer.getTask();
			
			QueueBroker qbroker = client.getQueueBroker();
						
			for(int i = 0; i < 2; i++) {
				
				MessageQueue messageQueue = qbroker.accept(80);
				
				byte[] message = messageQueue.receive();

				messageQueue.send(message, 0, message.length);
						
				messageQueue.close();
		
				assert(messageQueue != null) : "cannot create server channel, no connection";
				assert(messageQueue.closed() == true) : "cannot close server channel, still connected";
				
			}
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
