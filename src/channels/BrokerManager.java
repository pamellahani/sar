package channels;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BrokerManager {
    
    // Stores all the brokers with their associated names
    private final Map<String, Broker> brokers;
    

    public BrokerManager() {
        this.brokers = new ConcurrentHashMap<>();
    }


    public synchronized Broker getBroker(String name) {
        return brokers.get(name);
    }

    // register a broker with the broker manager
    public synchronized void registerBroker(Broker broker) {

        if (broker == null || broker.getName() == null || broker.getName().isEmpty()) {
            throw new IllegalArgumentException("Broker or broker name cannot be null or empty.");
        }
        if (brokers.containsKey(broker.getName())) {
            throw new IllegalArgumentException("Broker with name " + broker.getName() + " already exists.");
        }
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
