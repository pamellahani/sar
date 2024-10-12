package hybrid.tests;

import hybrid.QueueBroker;

public class EchoServer implements Runnable{
	
    private QueueBroker _broker;
        
        public EchoServer(QueueBroker broker) {
            _broker = broker;
        }
    
    
        @Override
        public void run() {
            _broker.bind(80, new EchoAcceptListener(_broker));
            
        }
}