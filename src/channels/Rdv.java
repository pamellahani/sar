package channels;

/**
 * The {@code Rdv} class represents a rendezvous point for two {@code Broker} instances.
 * It provides methods for accepting and connecting brokers, and creating a communication channel between them.
 */

public class Rdv {
    private Broker brokerAcceptor;
    private Broker brokerConnector;
    private SimpleChannel accepting_channel;  // Store the channel once it is created
    private SimpleChannel connecting_channel;  // Store the channel once it is created
    private final Object lock = new Object();

    private void waitForBrokers() {
        synchronized (lock) {
            try {
                while (brokerAcceptor == null || brokerConnector == null) {
                    lock.wait();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Registers a broker as the acceptor and creates or returns an existing channel for the port.
     */
    public SimpleChannel accept(Broker broker, int port) {
        synchronized (lock) {
            if (this.brokerAcceptor != null) {
                throw new IllegalStateException("Broker acceptor is already registered for port " + port);
            }

            this.brokerAcceptor = broker;

            if (this.brokerConnector == null) {
                // Wait for the connector to be registered
                try {
                    lock.wait(); // Wait for the brokerConnector to be set
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new IllegalStateException("Thread was interrupted while waiting for connector.", e);
                }
            }

            // If the channel is already created, return it
            if (accepting_channel != null) {
                return accepting_channel;
            }

            // Create a new SimpleChannel if not created yet
            accepting_channel = new SimpleChannel(this.brokerAcceptor, port);
            return accepting_channel;
        }
    }

    /**
     * Registers a broker as the connector. If the broker acceptor is already registered, creates a channel between the two brokers, else waits for the acceptor to be registered.
     */
    public SimpleChannel connect(Broker broker, int port) {
        synchronized (lock) {
            if (this.brokerConnector != null) {
                throw new IllegalStateException("Broker connector is already registered for port " + port);
            }

            this.brokerConnector = broker;

            // If the channel is already created, return it
            if (connecting_channel != null) {
                return connecting_channel;
            }

            // If the acceptor is already registered, create a SimpleChannel
            if (brokerAcceptor != null) {
                connecting_channel = new SimpleChannel(this.brokerConnector, port);
                return connecting_channel;
            }

            // Wait for the acceptor to be registered
            waitForBrokers();

            // Create a SimpleChannel once the acceptor is registered
            connecting_channel = new SimpleChannel(this.brokerConnector, port);
            return connecting_channel;
        }
    }
}
