package channels;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BrokerManager {
    
    // Stores all the brokers with their associated names
    private final Map<String, Broker> brokers;
    
    // Stores the rendez-vous (Rdv) points for connections
    private final Map<Integer, Rdv> rdvPoints; // Keyed by port number for each connection

    public BrokerManager() {
        this.brokers = new ConcurrentHashMap<>();
        this.rdvPoints = new ConcurrentHashMap<>();
    }


    public synchronized Broker getBroker(String name) {
        return brokers.get(name);
    }

    public synchronized Rdv accept(int port, Broker broker) {
        Rdv rdv = rdvPoints.get(port);
        if (rdv == null) {
            rdv = new Rdv();
            rdvPoints.put(port, rdv); 
        }
        rdv.accept(broker); 
        return rdv;
    }

    public synchronized Rdv connect(int port, Broker broker) {
        Rdv rdv = rdvPoints.get(port);
        if (rdv == null) {
            rdv = new Rdv();
            rdvPoints.put(port, rdv); 
        }
        rdv.connect(broker);  
        return rdv;
    }

    // register a broker with the broker manager
    public synchronized void registerBroker(Broker broker) {
        brokers.put(broker.getName(), broker);
    }

    // deregister a broker from the broker manager when no longer needed
    public synchronized void deregisterBroker(String name) {
        brokers.remove(name);
    }

    // remove an Rdv when it is no longer needed (e.g., after disconnection)
    public synchronized void removeRdv(int port) {
        rdvPoints.remove(port);
    }

   
}
