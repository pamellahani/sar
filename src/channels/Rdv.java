package channels;

/**
 * The {@code Rdv} class represents a rendezvous point for two {@code Broker} instances.
 * It provides methods for accepting and connecting brokers, and creating a communication channel between them.
 */

public class Rdv {

    private Broker brokerAcceptor;
    private Broker brokerConnector;

    private final Object lock = new Object();

    public void accept(Broker broker) {
        synchronized (lock) {
            this.brokerAcceptor = broker;
            if (brokerConnector != null) {
                createChannel();
                System.out.println("Channel created between acceptor and connector");
            }
        }
    }
    
    public void connect(Broker broker) {
        synchronized (lock) {
            this.brokerConnector = broker;
            if (brokerAcceptor != null) {
                createChannel();
                System.out.println("Channel created between connector and acceptor");
            }
        }
    }
    

    // Create the communication channel and set up CircularBuffers here
    private Channel createChannel() {
        return new SimpleChannel(new CircularBuffer(1024)); // TODO: may need to change buffer size
    }

    public Channel getChannel() {
        if (brokerAcceptor == null || brokerConnector == null) {
            throw new IllegalStateException("Cannot create channel: missing broker");
        }
        return createChannel();
    }

}

