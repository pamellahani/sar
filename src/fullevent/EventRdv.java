package fullevent;

import channels.Broker;
import hybrid.EventTask;

public class EventRdv {

    private Broker brokerAcceptor;
    private Broker brokerConnector;
    private EventChannel acceptingChannel;
    private EventChannel connectingChannel;
    private BrokerListener brokerListener;

    public EventRdv(boolean isAcceptor, Broker broker, int port, BrokerListener listener) {
        if (isAcceptor) {
            this.brokerAcceptor = broker;
        } else {
            this.brokerConnector = broker;
        }
        this.brokerListener = listener;
    }

    public void accept(Broker broker, int port) {
        this.brokerAcceptor = broker;
        this.acceptingChannel = new EventChannel(1024, port, brokerAcceptor);

        // Non-blocking connection check using EventTask
        EventTask task = new EventTask();
        task.post(() -> {
            if (this.brokerConnector != null) {
                connectChannels();
            } else {
                brokerListener.onWait(this.brokerAcceptor, this.brokerConnector);
            }
        });
    }

    public void connect(Broker broker, int port) {
        this.brokerConnector = broker;
        this.connectingChannel = new EventChannel(1024, port, brokerConnector);

        // Non-blocking connection check using EventTask
        EventTask task = new EventTask();
        task.post(() -> {
            if (brokerAcceptor != null) {
                connectChannels();
            } else {
                brokerListener.onWait(this.brokerAcceptor, this.brokerConnector);
            }
        });
    }

    private void connectChannels() {
        // Post a task to EventPump for connecting channels
        EventTask task = new EventTask();
        task.post(() -> {
            connectingChannel.connectChannels(acceptingChannel, brokerAcceptor.getName());
            acceptingChannel.connectChannels(connectingChannel, brokerConnector.getName());
            System.out.println("Channels successfully connected between client and server.");
            brokerListener.onConnect(connectingChannel, acceptingChannel);
        });
    }

    public interface BrokerListener {
        void onWait(Broker acceptorBroker, Broker connectorBroker);
        void onConnect(EventChannel connectingChannel, EventChannel acceptingChannel);
    }
}
