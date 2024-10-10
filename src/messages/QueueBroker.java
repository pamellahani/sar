package messages;

import channels.Broker;

public abstract class QueueBroker {
    
    protected Broker broker;
    protected String name; 
    
    public QueueBroker(Broker broker) {
        this.broker = broker;
    }

    public abstract MessageQueueImpl accept(int port);
    public abstract MessageQueueImpl connect(String name, int port);
}


