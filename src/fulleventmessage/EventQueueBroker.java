package fulleventmessage;

import java.util.HashMap;
import java.util.Map;

import fulleventchannel.Broker.AcceptListener;
import fulleventchannel.Broker.ConnectListener;
import fulleventchannel.EventBroker;
import fulleventchannel.EventChannel;
import fulleventchannel.EventTask;

public class EventQueueBroker extends QueueBroker {

    private EventBroker br;
    private Map<Integer, Boolean> bindStatus;

    public EventQueueBroker(String name) {
        super();
        br = new EventBroker(name); 
        bindStatus = new HashMap<>();
    }

    @Override
    public boolean unbind(int port) {
        if (bindStatus.containsKey(port)) {
            bindStatus.put(port, false); // Mark the port as unbound
            br.unbind(port);
            return true;
        }
        return false;
    }

    @Override
    public boolean bind(int port, AcceptListener listener) {
        if (bindStatus.containsKey(port) && bindStatus.get(port)) {
            return false; // Already bound
        }

        // Mark the port as bound and process asynchronously using EventTask
        bindStatus.put(port, true);

        // Event-driven binding process using EventTask
        EventTask task = new EventTask();
        task.post(() -> {
            while (bindStatus.get(port)) {
                EventChannel channelAccept = new EventChannel();
                if (br.accept(port, listener)) { // Check if accept was successful
                    listener.accepted(channelAccept);
                  //  MessageQueue mq = new EventMessageQueue(channelAccept);
                 
                }
            }
        });

        return true;
    }

    @Override
    public boolean connect(String name, int port, ConnectListener listener) {

        EventTask task = new EventTask();
        task.post(() -> {
            EventChannel channelConnect = new EventChannel();
            if (br.connect(name, port, listener)) {
                listener.connected(channelConnect);  

                // to use the MessageQueue internally:
               // MessageQueue mq = new EventMessageQueue(channelConnect);
                // Handle further message queue logic as needed...
            } else {
                listener.refused();
            }
        });
        return true;
    }

    @Override
    public String getName() {
        return br.getName();
    }

}
