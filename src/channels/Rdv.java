package channels;

public class Rdv {
    private Broker brokerAcceptor;
    private Broker brokerConnector;
    private SimpleChannel acceptingChannel;  // Single shared channel instance to simplify the interaction
    private SimpleChannel connectingChannel;  // Single shared channel instance to simplify the interaction

    public synchronized Channel accept(Broker broker, int port) {
        this.brokerAcceptor = broker;
        this.acceptingChannel = new SimpleChannel(brokerAcceptor, brokerConnector);  // Create the channel
    
        if (connectingChannel != null) {
            connectingChannel.connect(acceptingChannel, brokerAcceptor.getName());
            notifyAll();  // Notify waiting connect call
        } else {
            waitForBroker(true);  // Wait for the connecting channel if not yet initialized
        }
        
        return acceptingChannel;
    }
    
    public synchronized Channel connect(Broker broker, int port) {
        this.brokerConnector = broker;
        this.connectingChannel = new SimpleChannel(brokerAcceptor, brokerConnector);  // Create the channel
    
        if (acceptingChannel != null) {
            acceptingChannel.connect(connectingChannel, brokerConnector.getName());
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
    
}
