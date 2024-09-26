package channels;

public class Rdv {
    private Broker brokerAcceptor;
    private Broker brokerConnector;
    private SimpleChannel acceptingChannel;  // Single shared channel instance to simplify the interaction
    private SimpleChannel connectingChannel;  // Single shared channel instance to simplify the interaction
    private boolean connected = false;

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
        if (connectingChannel != null) {
            connectChannels();
            notifyAll();  // Notify waiting connect call
        } else {
            waitForBroker(true);  // Wait for the connecting channel if not yet initialized
        }

        return acceptingChannel;
    }

    public synchronized boolean isConnected() {
        return connected;
    }

    public synchronized void setConnected(boolean connected) {
        this.connected = connected;
    }

    public synchronized Channel connect(Broker broker, int port) {
        this.brokerConnector = broker;
        this.connectingChannel = new SimpleChannel(port, brokerConnector);  // Create the channel

        // If acceptingChannel is already created, connect both channels immediately
        if (acceptingChannel != null) {
            connectChannels();
            notifyAll();  // Notify waiting accept call
        } else {
            waitForBroker(false);  // Wait for the accepting channel if not yet initialized
        }

        return connectingChannel;
    }

    private synchronized void waitForBroker(boolean isAcceptor) {
        try {
            if (isAcceptor) {
                while (acceptingChannel == null) {
                    wait();  // Wait for the accepting channel to be initialized
                }
            } else {
                while (connectingChannel == null) {
                    wait();  // Wait for the connecting channel to be initialized
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Method to connect channels when both channels are available
    private synchronized void connectChannels() {
        connectingChannel.connectChannels(acceptingChannel, brokerAcceptor.getName());
        acceptingChannel.connectChannels(connectingChannel, brokerConnector.getName());
        System.out.println("Channels successfully connected between client and server.");
    }
}
