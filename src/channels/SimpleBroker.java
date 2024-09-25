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
        this.rdvPoints = new HashMap<Integer, Rdv>();
    }

    @Override
    public Channel connect(String name, int port) {
        Broker b = manager.getBrokerFromBM(name); // This should work now with a shared manager
        if (b == null) {
            throw new IllegalArgumentException("Broker with name " + name + " does not exist.");
        }
        return this.sub_connect(b, port);
    }

    @Override
    public Channel accept(int port) {
        Rdv rdv;
        synchronized (rdvPoints) {
            rdv = rdvPoints.get(port);
            if (rdv == null) {
                rdv = new Rdv();
                rdvPoints.put(port, rdv);
                rdvPoints.notifyAll();
            }
        }
        synchronized (rdv) {
            return rdv.accept(this, port);
        }
    }

    private Channel sub_connect(Broker b, int port) {
        Rdv rdv;
        synchronized (rdvPoints) {
            rdv = rdvPoints.get(port);
            while (rdv == null) {
                try {
                    rdvPoints.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                rdv = rdvPoints.get(port);
            }
            rdvPoints.remove(port);
        }
        return rdv.connect(b, port);
    }
}
