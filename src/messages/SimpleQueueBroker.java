package messages;

import channels.*;

public class SimpleQueueBroker extends QueueBroker {

    

    public SimpleQueueBroker(Broker broker) {
        super(broker);
    }

    

    @Override
    public MessageQueue accept(int port) {
        SimpleChannel channel = (SimpleChannel) broker.accept(port);
        return channel.getRequestQueue();  // Returns the RequestQueue for communication
    }

    @Override
    public MessageQueue connect(String name, int port) {
        SimpleChannel channel = (SimpleChannel) broker.connect(name, port);
        return channel.getResponseQueue();  // Returns the ResponseQueue for communication
    }
}
