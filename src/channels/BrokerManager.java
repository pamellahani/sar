package channels;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BrokerManager {
    
    private static BrokerManager instance;  // Singleton instance
    private final Map<String, Broker> brokers;

    private BrokerManager() {
        this.brokers = new ConcurrentHashMap<>();
    }

    // Singleton method to get the instance of BrokerManager
    public static BrokerManager getInstance() {
        if (instance == null) {
            instance = new BrokerManager();
        }
        return instance;
    }

    public synchronized Broker getBrokerFromBM(String name) {
        Broker broker = brokers.get(name);
        if (broker == null) {
            System.out.println("Broker with name " + name + " not found.");
        }
        return broker;
    }
    
    public synchronized void registerBroker(Broker broker) {
        System.out.println("Registering broker: " + broker.getName());
        brokers.put(broker.getName(), broker);
    }

    public synchronized void deregisterBroker(Broker broker) {
        brokers.remove(broker.getName());
    }
}
