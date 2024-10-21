package fullevent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;


public class EventBrokerManager {

    private static EventBrokerManager instance;  // Singleton instance
    private final Map<String, EventBroker> brokers;
    private final Map<String, Consumer<EventBroker>> pendingRequests;

    public EventBrokerManager() {
        this.brokers = new HashMap<>();
        this.pendingRequests = new HashMap<>();
    }

    // Singleton method to get the instance of BrokerManager
    public static EventBrokerManager getInstance() {
        if (instance == null) {
            synchronized (EventBrokerManager.class) {
                if (instance == null) {
                    instance = new EventBrokerManager();
                }
            }
        }
        return instance;
    }

    public EventBroker getBrokerFromBM(String name) {
        EventBroker broker = brokers.get(name);
        if (broker == null) {
            System.out.println("Broker with name " + name + " not found.");
        }
        return broker;
    }
    
    public void registerBroker(EventBroker broker) {
        System.out.println("Registering broker: " + broker.getName());
        brokers.put(broker.getName(), broker);
        
        // If there are pending requests for this broker, execute the callback
        if (pendingRequests.containsKey(broker.getName())) {
            Consumer<EventBroker> callback = pendingRequests.remove(broker.getName());
            if (callback != null) {
                callback.accept(broker);
            }
        }
    }

    public void deregisterBroker(EventBroker broker) {
        brokers.remove(broker.getName());
        System.out.println("Deregistered broker: " + broker.getName());
    }
}