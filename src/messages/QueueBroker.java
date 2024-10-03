package messages;

import channels.Broker;

public abstract class QueueBroker {
    
    protected Broker broker;
    protected String name; 
    
    public QueueBroker(Broker broker) {
        this.broker = broker;
    }

    public abstract MessageQueue accept(int port);
    public abstract MessageQueue connect(String name, int port);
}


