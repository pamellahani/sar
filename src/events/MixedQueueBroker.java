package events;

import channels.*;

public class MixedQueueBroker extends QueueBroker {

    private final Broker broker;
    private final EventPump eventPump;  

    public MixedQueueBroker(String name) {
        this.broker = new SimpleBroker(name); 
        this.eventPump = new EventPump();    
    }

    @Override
    public boolean bind(int port, AcceptListener listener) {
        eventPump.push(() -> {
            Channel channel = broker.accept(port); // happens on server side
            MessageQueue queue = new MixedMessageQueue(channel, eventPump);  // Pass EventPump to MessageQueue
            listener.accepted(queue);  // Notify that a connection has been accepted
        }); 
        return true;
    }

    @Override
    public boolean unbind(int port) {
        return true;
    }

    @Override
    public boolean connect(String name, int port, ConnectListener listener) {
        eventPump.push(() -> {
            Channel channel = broker.connect(name, port); // happens on client side
            if (channel == null) {
                listener.refused();
            } else {
                MessageQueue queue = new MixedMessageQueue(channel, eventPump);  // Pass EventPump to MessageQueue
                listener.connected(queue);  // Notify that a connection has been established
            }
        });
        return true;
    }

    @Override
    public Broker getBroker() {
        return broker;
    }

    public EventPump getEventPump() {
        return eventPump;  
    }
    
}
