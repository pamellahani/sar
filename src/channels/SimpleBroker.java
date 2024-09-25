package channels;

import java.util.HashMap;

public class SimpleBroker extends Broker {

    private BrokerManager manager;
    private HashMap<Integer, Rdv> rdvPoints;

    // Constructor that accepts a shared BrokerManager
    public SimpleBroker(String name, BrokerManager manager) {
        super(name);
        if (manager == null) {
            throw new IllegalArgumentException("BrokerManager cannot be null");
        }
        this.manager = manager;
        this.rdvPoints = new HashMap<>();
    }

    @Override
    public Channel connect(String name, int port) {
        Broker otherBroker = manager.getBrokerFromBM(name);

        if (otherBroker == null) {
            throw new IllegalArgumentException("Broker with name " + name + " not found.");
        }
        
        // Check if the rendezvous point already exists for the specified port
        Rdv rdvPoint;
        synchronized (rdvPoints) {
            rdvPoint = rdvPoints.get(port);
            
            // If no existing Rdv for this port, create a new one
            if (rdvPoint == null) {
                rdvPoint = new Rdv();
                rdvPoints.put(port, rdvPoint); // Register the Rdv in the map
            }
        }

        // Use the Rdv to create a connection
        return rdvPoint.connect(this, port);
    }

    @Override
    public Channel accept(int port) {
        // Create or retrieve the rendezvous point for this port
        Rdv rdvPoint;
        synchronized (rdvPoints) {
            rdvPoint = rdvPoints.get(port);
            
            // If no existing Rdv for this port, create a new one
            if (rdvPoint == null) {
                rdvPoint = new Rdv();
                rdvPoints.put(port, rdvPoint); // Register the Rdv in the map
            }
        }

        // Use the Rdv to accept a connection
        return rdvPoint.accept(this, port);
    }
}
