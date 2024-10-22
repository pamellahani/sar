package fullevent;

import java.util.HashMap;
import java.util.Map;

import hybrid.EventTask;

public class EventBroker extends Broker {
    
    private Map<Integer, AcceptListener> binds;
    private EventBrokerManager brokerManager;
    private String brokerName;

    public EventBroker(String name) {
        super(name);
        brokerManager = EventBrokerManager.getInstance();
        brokerName = name;
        brokerManager.registerBroker(this);
        binds = new HashMap<>();
        //System.out.println("EventBroker created with name: " + name);
    }

    @Override
    public boolean unbind(int port) {
        if (!binds.containsKey(port)) {
            return false;
        }
        binds.remove(port);
        //System.out.println("Unbind successful on port: " + port);
        return true;
    }

    @Override
    public boolean bind(int port, AcceptListener listener) {
        if (binds.containsKey(port)) {
            return false;
        }
        binds.put(port, listener);
       // System.out.println("Bind successful on port: " + port);
        return true;
    }

    @Override
    public boolean connect(String name, int port, ConnectListener listener) {
        EventBroker broker = brokerManager.getBroker(name);
        if (broker == null) {
            listener.refused();
            //System.out.println("Connection refused: broker not found");
            return false;
        } else {
            broker.aux_connect(port, listener);
           // System.out.println("Connection successful to broker: " + name + " on port: " + port);
            return true;
        }
    }

    private void aux_connect(int port, ConnectListener listener) {
        if (binds.containsKey(port)) {
            EventChannel channelAccept = new EventChannel();
            EventChannel channelConnect = new EventChannel();

            channelAccept.otherChannel = channelConnect;
            channelConnect.otherChannel = channelAccept;

            channelAccept.outBuffer = channelConnect.inBuffer;
            channelConnect.outBuffer = channelAccept.inBuffer;

            listener.connected(channelConnect);
            binds.get(port).accepted(channelAccept);
            //System.out.println("Aux connect successful on port: " + port);
        } else {
            new EventTask().post(() -> aux_connect(port, listener));
        }
    }

    public String getName() {
        return brokerName;
    }
}
