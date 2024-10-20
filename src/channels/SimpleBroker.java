package channels;

import java.util.HashMap;

public class SimpleBroker extends Broker {
    private HashMap<Integer, Rdv> accepts; // Manages rendezvous points by port

    public SimpleBroker(String name) {
        super(name);
        this.accepts = new HashMap<>();
        
        // Register this broker with the singleton BrokerManager
        BrokerManager.getInstance().registerBroker(this);
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
            accepts.put(port, rdvPoint); // Register the new Rdv
            accepts.notifyAll(); // Notify any threads waiting for this Rdv to be created
        }

        Channel ch; 
        synchronized (rdvPoint) {
            ch = rdvPoint.accept(this, port); // Proceed with accepting the connection
        }

        synchronized (accepts) {
            accepts.remove(port); // Remove the Rdv after connection
        }

        return ch;
    }

    @Override
    public Channel connect(String brokerName, int port) {
        SimpleBroker bm = (SimpleBroker)BrokerManager.getInstance().getBrokerFromBM(brokerName);
        if (bm == null) {
            throw new IllegalArgumentException("Broker with name " + brokerName + " not found.");
        }
        return bm.aux_connect(bm, port);
    }

    private Channel aux_connect(Broker bm, int port) {
        Rdv rdvPoint;
        synchronized (accepts) {
            while ((rdvPoint = accepts.get(port)) == null) {
                try {
                    accepts.wait(); // Wait for the Rdv to be created
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return null;
                }
            }
        }

        Channel ch;
        synchronized (rdvPoint) {
            ch = rdvPoint.connect(bm, port); // Proceed with connecting to the server
        }

        synchronized (accepts) {
            accepts.remove(port); // Remove the Rdv after connection
        }

        return ch;
    }
}
