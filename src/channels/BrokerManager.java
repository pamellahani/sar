package channels;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BrokerManager {
    
    // Stores all the brokers with their associated names
    private final Map<String, Broker> brokers;
    

    public BrokerManager() {
        this.brokers = new ConcurrentHashMap<>();
    }

    BrokerManager getSelf() {
        return this;
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


    // Deregisters a broker from the broker manager
    public synchronized void deregisterBroker(Broker broker) {

        if (broker == null || broker.getName() == null || broker.getName().isEmpty()) {
            throw new IllegalArgumentException("Broker or broker name cannot be null or empty.");
        }
        if (!brokers.containsKey(broker.getName())) {
            throw new IllegalArgumentException("Broker with name " + broker.getName() + " does not exist.");
        }
        brokers.remove(broker.getName());
    }


    public BrokerManager getManager(){
        return this;
    }

   
}
