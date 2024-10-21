package fullevent;

import channels.Broker;
import channels.Channel;
import hybrid.QueueBroker;
import hybrid.EventTask;

import java.util.HashMap;
import java.util.Map;

public class EventQueueBroker extends QueueBroker {

    private final EventBroker br;
    private final EventTask eventTask;
    private final Map<Integer, EventMessageQueue> boundPorts;  // Keeps track of bound ports and message queues

    public EventQueueBroker(String name) {
        super(name);
        this.boundPorts = new HashMap<>(); // Map to store active bindings
        br = new EventBroker(name, new EventBroker.BrokerListener() {
            @Override
            public void onWait(Broker acceptorBroker, Broker connectorBroker) {
                System.out.println("Waiting for connection between brokers...");
            }

            @Override
            public void onConnect(Channel connectingChannel, Channel acceptingChannel) {
                System.out.println("Connected channels between client and server.");
            }
        });
        eventTask = new EventTask(); // Keep a single instance of EventTask
    }

    @Override
    public boolean bind(int port, AcceptListener listener) {
        eventTask.post(() -> {
            System.out.println("Binding to port: " + port);
            try {
                if (boundPorts.containsKey(port)) {
                    System.err.println("Port " + port + " is already bound.");
                    return;
                }
                // Simulate the binding process
                EventMessageQueue messageQueue = new EventMessageQueue(new EventChannel(1024, port, this.getBroker()));
                boundPorts.put(port, messageQueue); 
                listener.accepted(messageQueue);
                System.out.println("Bound successfully to port: " + port);
            } catch (Exception e) {
                System.err.println("Failed to bind: " + e.getMessage());
            }
        });
        return true;
    }

    @Override
    public boolean unbind(int port) {
        eventTask.post(() -> {
            System.out.println("Unbinding from port: " + port);
            try {
                if (!boundPorts.containsKey(port)) {
                    System.err.println("Port " + port + " is not bound.");
                    return;
                }

                // Disconnect all channels associated with this port
                EventMessageQueue messageQueue = boundPorts.get(port);
                if (messageQueue != null) {
                    messageQueue.close();  // Gracefully close the message queue (disconnects channels)
                    System.out.println("Disconnected all channels on port: " + port);
                }

                // Remove the port from the boundPorts map
                boundPorts.remove(port);
                System.out.println("Unbound successfully from port: " + port);
            } catch (Exception e) {
                System.err.println("Failed to unbind: " + e.getMessage());
            }
        });
        return true;
    }

    @Override
    public boolean connect(String name, int port, ConnectListener listener) {
        eventTask.post(() -> {
            System.out.println("Connecting to: " + name + " on port: " + port);
            try {
                if (!boundPorts.containsKey(port)) {
                    System.err.println("Cannot connect, port " + port + " is not bound.");
                    listener.refused();
                    return;
                }

                // Simulate the connection process
                EventMessageQueue messageQueue = boundPorts.get(port);
                listener.connected(messageQueue);
                System.out.println("Connected successfully to: " + name);
            } catch (Exception e) {
                listener.refused();
                System.err.println("Connection refused: " + e.getMessage());
            }
        });
        return true;
    }

    @Override
    public Broker getBroker() {
        return br; 
    }
}
