package messages;

import channels.Broker;
import channels.Channel;

public class SimpleQueueBroker extends QueueBroker {


    public SimpleQueueBroker(Broker broker) {
        super(broker);
    
    }

    @Override
    public MessageQueue accept(int port) {
        Channel channel = broker.accept(port);
        return new MessageQueue(channel);
    }

    @Override
    public MessageQueue connect(String name, int port) {
        Channel channel = broker.connect(name, port);
        return new MessageQueue(channel);
    }
}
