package channels;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class BrokerManager {
    
    // Stores all the brokers with their associated names
    private final Map<String, Broker> brokers;
    
    // Stores the rendez-vous (Rdv) points for connections
    private final Map<Integer, Rdv> rdvPoints; // Keyed by port number for each connection

    // Constructor
    public BrokerManager() {
        this.brokers = new ConcurrentHashMap<>();
        this.rdvPoints = new ConcurrentHashMap<>();
    }

    // Synchronized method to retrieve a broker by name
    public synchronized Broker getBroker(String name) {
        return brokers.get(name);
    }

    // Synchronized method for the accept operation - called by a broker to accept a connection on a specific port
    public synchronized Rdv accept(int port, Broker broker) {
        Rdv rdv = rdvPoints.get(port);
        if (rdv == null) {
            rdv = new Rdv();
            rdvPoints.put(port, rdv); 
        }
        rdv.accept(broker); 
        return rdv;
    }

    // Synchronized method for the connect operation - called by a broker to connect to a specific port
    public synchronized Rdv connect(int port, Broker broker) {
        Rdv rdv = rdvPoints.get(port);
        if (rdv == null) {
            rdv = new Rdv();
            rdvPoints.put(port, rdv); 
        }
        rdv.connect(broker);  
        return rdv;
    }

    // Synchronized method to deregister a broker from the manager when no longer needed
    public synchronized void deregisterBroker(String name) {
        brokers.remove(name);
    }

    // Synchronized method to remove an Rdv when it is no longer needed (e.g., after disconnection)
    public synchronized void removeRdv(int port) {
        rdvPoints.remove(port);
    }

   
}
