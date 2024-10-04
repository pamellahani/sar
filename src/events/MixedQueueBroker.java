package events;

import channels.*;

public class MixedQueueBroker extends QueueBroker {

    private final Broker broker;

    public MixedQueueBroker(String name) {
        this.broker = new SimpleBroker(name); // Link QueueBroker to Broker
    }

    @Override
    public boolean bind(int port, AcceptListener listener) {
        // Start a thread to accept connections asynchronously
        new Thread(() -> {
            Channel channel = broker.accept(port); // Accept the connection on the server side
            MessageQueue queue = new MixedMessageQueue(channel);
            listener.accepted(queue);  // Notify that a connection has been accepted
        }).start();
        return true;
    }

    @Override
    public boolean unbind(int port) {
        return true;
    }

    @Override
    public boolean connect(String name, int port, ConnectListener listener) {
        // Start a thread to connect asynchronously
        new Thread(() -> {
            Channel channel = broker.connect(name, port); // Connect to the broker on the client side
            if (channel == null) {
                listener.refused();
            } else {
                MessageQueue queue = new MixedMessageQueue(channel);
                listener.connected(queue); // Notify that a connection has been established
            }
        }).start();
        return true;
    }

    @Override
    public Broker getBroker() {
        return broker;
    }
}
