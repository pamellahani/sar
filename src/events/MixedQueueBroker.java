package events;

import channels.*;

public class MixedQueueBroker extends QueueBroker {

    private final Broker broker;
    private final EventPump eventPump;  // EventPump associated with this instance

    public MixedQueueBroker(String name) {
        this.broker = new SimpleBroker(name); // Link QueueBroker to Broker
        this.eventPump = new EventPump();     // Initialize the EventPump
    }

    @Override
    public boolean bind(int port, AcceptListener listener) {
        eventPump.push(() -> {
            Channel channel = broker.accept(port); // Accept the connection on the server side
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
            Channel channel = broker.connect(name, port); // Connect to the broker on the client side
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
        return eventPump;  // Provide access to the EventPump for other components
    }
}
