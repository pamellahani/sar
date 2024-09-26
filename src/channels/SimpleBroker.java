package channels;

import java.util.HashMap;

public class SimpleBroker extends Broker {
    private BrokerManager manager;
    private HashMap<Integer, Rdv> accepts; // HashMap to manage rendezvous points by port

    public SimpleBroker(String name, BrokerManager manager) {
        super(name);
        if (manager == null) {
            throw new IllegalArgumentException("BrokerManager cannot be null");
        }
        this.manager = manager.getSelf();
        this.manager.registerBroker(this);
        this.accepts = new HashMap<Integer, Rdv>();
    }

    @Override
    public Channel accept(int port) {
        Rdv rdvPoint;
        synchronized (accepts) {
            rdvPoint = accepts.get(port);
            if (rdvPoint != null) {
                throw new IllegalArgumentException("Rendezvous point for port " + port + " already exists.");
            }
            rdvPoint = new Rdv(true, this, port);
            accepts.put(port, rdvPoint); // Create and register a new Rdv for this port
            accepts.notifyAll(); // Notify any threads waiting for this Rdv to be created
        }
        
        Channel ch; 
        ch = rdvPoint.accept(this, port); // Proceed with accepting the connectionaccepts.remove(port);
        return ch;
    }

    private Channel aux_connect(Broker bm, int port) {
        Rdv rdvPoint = null;
        synchronized (accepts) {
            rdvPoint = accepts.get(port);
            while (rdvPoint == null) {
                try {
                    accepts.wait(); // Wait for the Rdv to be created
                    //rdv = accepts.get(port);
                } catch (InterruptedException e) {
                }
            }
            accepts.remove(port); // Remove the Rdv from the list
            notifyAll();
        }
        Channel ch = rdvPoint.connect(bm, port);
        return ch;
    }

    @Override
    public Channel connect(String brokerName, int port) {
        Broker bm = manager.getBrokerFromBM(brokerName);
        if (bm == null) {
            throw new IllegalArgumentException("Broker with name " + brokerName + " not found.");
        }
        return aux_connect(bm, port);
    }
}
