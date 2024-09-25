package channels;

/**
 * The {@code Rdv} class represents a rendezvous point for two {@code Broker} instances.
 * It provides methods for accepting and connecting brokers, and creating a communication channel between them.
 */

public class Rdv {
    private Broker brokerAcceptor;
    private Broker brokerConnector;
    private SimpleChannel acceptingChannel;  // Store the channel once it is created
    private SimpleChannel connectingChannel;  // Store the channel once it is created
    private final Object lock = new Object();

    private void waitForBrokers() {
        synchronized (lock) {
            try {
                while (brokerAcceptor == null || brokerConnector == null) {
                    System.out.println("Waiting for both brokers to be registered...");
                    lock.wait();  // Wait for both brokers to be registered
                }
                System.err.println("Both brokers are now registered.");
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();  // Interrupt current thread if needed
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
            System.out.println("Acceptor broker registered for port " + port);

            // Notify all waiting threads that the acceptor has been registered
            lock.notifyAll();

            // If the brokerConnector is not yet registered, wait for it
            if (this.brokerConnector == null) {
                try {
                    lock.wait();  // Wait for the brokerConnector to be set
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new IllegalStateException("Thread was interrupted while waiting for connector.", e);
                }
            }

            // If the channel is already created, return it
            if (acceptingChannel != null) {
                return acceptingChannel;
            }

            // Create a new SimpleChannel if not created yet
            acceptingChannel = new SimpleChannel(this.brokerAcceptor, this.brokerConnector);
            return acceptingChannel;
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
            System.out.println("Connector broker registered for port " + port);

            // Notify all waiting threads that the connector has been registered
            lock.notifyAll();

            // If the brokerAcceptor is not yet registered, wait for it
            if (brokerAcceptor == null) {
                waitForBrokers();
            }

            // If the channel is already created, return it
            if (connectingChannel != null) {
                return connectingChannel;
            }

            // Create a SimpleChannel once the acceptor is registered
            connectingChannel = new SimpleChannel(this.brokerConnector, this.brokerAcceptor);
            return connectingChannel;
        }
    }
}
