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
            }
        }
    }

    public void connect(Broker broker) {
        synchronized (lock) {
            this.brokerConnector = broker;
            if (brokerAcceptor != null) {
                createChannel();
            }
        }
    }

    // Create the communication channel and set up CircularBuffers here
    private void createChannel() {
        //TODO: Implement channel creation here 
    }
}

