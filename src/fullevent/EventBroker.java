package fullevent;

import java.util.HashMap;
import hybrid.EventTask;
import java.util.function.Consumer;

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

            if (channelHolder[0] != null) {
                onChannelReady(channelHolder[0]);
            }
        });

        return null; // Return null initially, callback will handle the channel once ready
    }

    @Override
    public Channel connect(String brokerName, int port) {
        EventTask task = new EventTask();
        final Channel[] channelHolder = new Channel[1]; // Used to store the Channel result

        task.post(() -> {
            EventBrokerManager.getInstance().getBrokerFromBM(brokerName, targetBroker -> {
                if (targetBroker == null) {
                    throw new IllegalArgumentException("Broker with name " + brokerName + " not found.");
                }
                auxConnect(targetBroker, port, channel -> {
                    channelHolder[0] = channel;
                    if (channelHolder[0] != null) {
                        onChannelReady(channelHolder[0]);
                    }
                });
            });
        });

        return null; // Return null initially, callback will handle the channel once ready
    }

    private void auxConnect(EventBroker bm, int port, Consumer<Channel> callback) {
        EventTask task = new EventTask();

        task.post(() -> {
            EventRdv rdvPoint;
            synchronized (accepts) {
                rdvPoint = accepts.get(port);
                if (rdvPoint == null) {
                    brokerListener.onWait(bm, this);
                    return;
                }
            }
            callback.accept(rdvPoint.connect(bm, port));
        });
    }

    private void onChannelReady(Channel channel) {
        System.out.println("Channel is ready: " + channel);
    }
}
