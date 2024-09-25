package channels;

import java.util.HashMap;

public class SimpleBroker extends Broker {
    private BrokerManager manager;
    private HashMap<Integer, Rdv> rdvPoints; // HashMap to manage rendezvous points by port

    public SimpleBroker(String name, BrokerManager manager) {
        super(name);
        if (manager == null) {
            throw new IllegalArgumentException("BrokerManager cannot be null");
        }
        this.manager = manager;
        this.rdvPoints = new HashMap<>();
    }

    @Override
    public Channel accept(int port) {
        synchronized (rdvPoints) {
            Rdv rdvPoint = rdvPoints.get(port);
            if (rdvPoint == null) {
                rdvPoint = new Rdv();
                rdvPoints.put(port, rdvPoint); // Create and register a new Rdv for this port if it does not exist
            }

           
            return rdvPoint.accept(this, port);
           
        }
    }

    @Override
    public Channel connect(String remoteBrokerName, int port) {
        Broker otherBroker = manager.getBrokerFromBM(remoteBrokerName);
        if (otherBroker == null) {
            throw new IllegalArgumentException("Broker with name " + remoteBrokerName + " not found.");
        }

        Rdv rdvPoint;
        synchronized (rdvPoints) {
            rdvPoint = rdvPoints.get(port);
            if (rdvPoint == null) {
                rdvPoint = new Rdv();
                rdvPoints.put(port, rdvPoint); // Create and register a new Rdv for this port if it does not exist
            }
        }

        return rdvPoint.connect(this, port);
    }
}
