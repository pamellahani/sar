
package fullevent;

import fullevent.EventBroker.BrokerListener;
import hybrid.EventTask;

public class EventRdv {

    private EventBroker brokerAcceptor;
    private EventBroker brokerConnector;
    private EventChannel acceptingChannel;
    private EventChannel connectingChannel;
    private BrokerListener brokerListener;

    public EventRdv(boolean isAcceptor, EventBroker broker, int port, BrokerListener brokerListener2) {
        if (isAcceptor) {
            this.brokerAcceptor = broker;
        } else {
            this.brokerConnector = broker;
        }
        this.brokerListener = (BrokerListener) brokerListener2;
    }

    public Channel accept(EventBroker broker, int port) {
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
        return acceptingChannel;
    }

    public Channel connect(EventBroker bm, int port) {
        this.brokerConnector = bm;
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

        return connectingChannel;
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

}
