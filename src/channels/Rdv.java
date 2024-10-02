package channels;

public class Rdv {
    private Broker brokerAcceptor;
    private Broker brokerConnector;
    private SimpleChannel acceptingChannel;  // Single shared channel instance to simplify the interaction
    private SimpleChannel connectingChannel;  // Single shared channel instance to simplify the interaction


    public Rdv(boolean isAcceptor, Broker broker, int port) {
        if (isAcceptor) {
            this.brokerAcceptor = broker;
        } else {
            this.brokerConnector = broker;
        }
    }

    public synchronized Channel accept(Broker broker, int port) {
        this.brokerAcceptor = broker;
        this.acceptingChannel = new SimpleChannel(port, brokerAcceptor);  // Create the channel

        // If connectingChannel is already created, connect both channels immediately
        if (this.brokerConnector != null) {
            connectChannels();
            notifyAll();  // Notify waiting connect call
        } else {
            waitForBroker(this.brokerAcceptor,this.brokerConnector);  // Wait for the connecting channel if not yet initialized
        }

        return acceptingChannel;
    }

    public Channel connect(Broker broker, int port) {
        this.brokerConnector = broker;
        this.connectingChannel = new SimpleChannel(port, brokerConnector);  // Create the channel

        // If acceptingChannel is already created, connect both channels immediately
        if (brokerAcceptor != null) {
            connectChannels();
            notifyAll();  // Notify waiting accept call
        } else {
            waitForBroker(this.brokerAcceptor,this.brokerConnector);  // Wait for the accepting channel if not yet initialized
        }

        return connectingChannel;
    }

    private void waitForBroker(Broker ab, Broker cb) {
        try {
            if (ab == null || cb == null) {
                wait(500);  // Wait for the other channel to be created
            }
        } catch (InterruptedException e) {
            //Thread.currentThread().interrupt(); // Handle interruption
        }
    }

    // Method to connect channels when both channels are available
    private synchronized void connectChannels() {
        connectingChannel.connectChannels(acceptingChannel, brokerAcceptor.getName());
        acceptingChannel.connectChannels(connectingChannel, brokerConnector.getName());
        System.out.println("Channels successfully connected between client and server.");
        notifyAll();
    }
}
