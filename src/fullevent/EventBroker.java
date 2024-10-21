package fullevent;

import java.util.HashMap;
import java.util.function.Consumer;

import hybrid.EventTask;

public class EventBroker extends Broker {

    private HashMap<Integer, EventRdv> accepts;
    private BrokerListener brokerListener;

    public EventBroker(String name, BrokerListener listener) {
        super(name);
        this.accepts = new HashMap<>();
        this.brokerListener = listener;

        // Register this broker with the singleton BrokerManager
        EventBrokerManager.getInstance().registerBroker(this);
    }

    public interface BrokerListener {
        void onWait(Broker acceptorBroker, Broker connectorBroker);
        void onConnect(Channel connectingChannel, Channel acceptingChannel);
    }

    @Override
    public Channel accept(int port) {
        EventTask task = new EventTask();
        final Channel[] channelHolder = new Channel[1]; // Used to hold the result of the channel connection.

        task.post(() -> {
            if (accepts.containsKey(port)) {
                throw new IllegalArgumentException("Rendezvous point for port " + port + " already exists.");
            }

            EventRdv rdvPoint = new EventRdv(true, this, port, brokerListener);
            accepts.put(port, rdvPoint);
            brokerListener.onWait(this, null);
            channelHolder[0] = rdvPoint.accept(this, port);
        });

        // Wait for the task to complete before returning the Channel
        while (channelHolder[0] == null) {
            // Busy wait until the channel is assigned
            Thread.yield();
        }

        return channelHolder[0];
    }

    @Override
    public Channel connect(String brokerName, int port) {
        EventTask task = new EventTask();
        final Channel[] channelHolder = new Channel[1]; // Used to store the Channel result

        task.post(() -> {
            EventBroker targetBroker = (EventBroker) EventBrokerManager.getInstance().getBrokerFromBM(brokerName);
            if (targetBroker == null) {
                throw new IllegalArgumentException("Broker with name " + brokerName + " not found.");
            }
            channelHolder[0] = auxConnect(targetBroker, port);
        });

        // Wait for the task to complete before returning the Channel
        while (channelHolder[0] == null) {
            // Busy wait until the channel is assigned
            Thread.yield();
        }

        return channelHolder[0];
    }

    private Channel auxConnect(EventBroker bm, int port) {
        EventTask task = new EventTask();
        final Channel[] channelHolder = new Channel[1]; // Used to store the Channel result

        task.post(() -> {
            EventRdv rdvPoint;
            synchronized (accepts) {
                while ((rdvPoint = accepts.get(port)) == null) {
                    brokerListener.onWait(bm, this);
                }
            }
            channelHolder[0] = rdvPoint.connect(bm, port);
        });

        // Wait for the task to complete before returning the Channel
        while (channelHolder[0] == null) {
            // Busy wait until the channel is assigned
            Thread.yield();
        }

        return channelHolder[0];
    }
}
