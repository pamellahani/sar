package fullevent.tests;

import fullevent.QueueBroker;

public class EchoServer implements Runnable{
	
    private QueueBroker queue_broker;
        
        public EchoServer(QueueBroker qb) {
            queue_broker = qb;
        }
    
    
        @Override
        public void run() {
            System.out.println("Server is running");
            queue_broker.bind(8080, new EchoAcceptListener(queue_broker));
            
        }
}