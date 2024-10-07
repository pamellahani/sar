package messages;

import channels.Broker;
import channels.Channel;
import channels.SimpleBroker;

public class QueueBrokerImpl extends QueueBroker {

    private SimpleBroker simpleBroker;

    public QueueBrokerImpl(Broker broker) {
        super(broker);
        this.simpleBroker = (SimpleBroker) broker;
    
    }

    @Override
    public MessageQueueImpl accept(int port) {
        Channel channel = simpleBroker.accept(port);
        return new MessageQueueImpl(channel);
    }

    @Override
    public MessageQueueImpl connect(String name, int port) {
        Channel channel = simpleBroker.connect(name, port);
        return new MessageQueueImpl(channel);
    }
}
