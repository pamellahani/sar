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
        synchronized (rdvPoint) {
            ch = rdvPoint.accept(this, port); // Proceed with accepting the connection
        }
        
        synchronized (accepts) {
            accepts.remove(port); // Safely remove the Rdv from the list once the connection is made
        }
        
        return ch;
    }

    private Channel aux_connect(Broker bm, int port) {
        Rdv rdvPoint = null;
        synchronized (accepts) {
            while ((rdvPoint = accepts.get(port)) == null) {
                try {
                    accepts.wait(500); // Wait for the Rdv to be created
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // Handle interruption
                    return null;
                }
            }
        }
        
        Channel ch;
        synchronized (rdvPoint) {
            ch = rdvPoint.connect(bm, port); // Proceed with connecting to the server
        }

        synchronized (accepts) {
            accepts.remove(port); // Safely remove the Rdv from the list once the connection is made
        }
        
        return ch;
    }

    @Override
    public Channel connect(String brokerName, int port) {
        SimpleBroker bm = (SimpleBroker)manager.getBrokerFromBM(brokerName);
        if (bm == null) {
            throw new IllegalArgumentException("Broker with name " + brokerName + " not found.");
        }
        return bm.aux_connect(bm, port);
    }
}
