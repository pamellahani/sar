package channels;

public class Rdv {
    private Broker brokerAcceptor;
    private Broker brokerConnector;
    private SimpleChannel acceptingChannel;  // Single shared channel instance to simplify the interaction
    private SimpleChannel connectingChannel;  // Single shared channel instance to simplify the interaction

    public synchronized Channel accept(Broker broker, int port) {
        this.brokerAcceptor = broker;
        this.acceptingChannel = new SimpleChannel(brokerAcceptor, brokerConnector);  // Create the channel now that both brokers are present
        
        if (connectingChannel != null){
            //brokerAcceptor.connect(brokerConnector.getName(), port); 
            connectingChannel.connect(acceptingChannel, brokerAcceptor.getName());
        }

        return acceptingChannel;
    }

    public synchronized Channel connect(Broker broker, int port) {
        this.brokerConnector = broker;
        this.connectingChannel = new SimpleChannel(brokerAcceptor, brokerConnector);  // Create the channel now that both brokers are present

        if (acceptingChannel != null){
            //brokerAcceptor.connect(brokerConnector.getName(), port);
            acceptingChannel.connect(connectingChannel, brokerConnector.getName());
            notifyAll();
        }
        else{
            waitForBroker();
        }
        return connectingChannel;
    }

    private synchronized void waitForBroker() {
        while (acceptingChannel == null||connectingChannel == null) {
            try {
                wait();
            } catch (InterruptedException e) {
            }
        }
    }
}
