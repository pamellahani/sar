package hybrid.tests;

import hybrid.QueueBroker;

public class EchoServer implements Runnable{
	
    private QueueBroker queue_broker;
        
        public EchoServer(QueueBroker qb) {
            queue_broker = qb;
        }
    
    
        @Override
        public void run() {
            queue_broker.bind(80, new EchoAcceptListener(queue_broker));
            
        }
}