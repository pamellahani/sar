package messages.tests;


import messages.Task;
import messages.MessageQueue;
import messages.QueueBroker;

public class EchoServer extends Task{
	
	public EchoServer(QueueBroker b) {
		super(b, () ->{
			EchoServer client = (EchoServer) EchoServer.getTask();
			
			QueueBroker qbroker = client.getQueueBroker();
						
			for(int i = 0; i < 1; i++) {
				
				MessageQueue messageQueue = qbroker.accept(8080);
				
				byte[] message = messageQueue.receive();

               // System.out.println("Server received: " + new String(message));

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
