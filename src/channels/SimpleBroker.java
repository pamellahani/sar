package channels;

import java.util.HashMap;

/**
 * The {@code SimpleBroker} class provides concrete implementations of
 * the abstract methods in {@code Broker}. It uses {@code BrokerManager} to
 * manage the connections.
 */
public class SimpleBroker extends Broker {

    BrokerManager manager; 
    HashMap<Integer, Rdv> rdvPoints;

    public SimpleBroker(String name) {
        super(name);
        if (manager == null) {
            this.manager = new BrokerManager();
        } else {
            this.manager = this.manager.getManager();
        }
        this.rdvPoints = new HashMap<Integer,Rdv>(); // Stores all the brokers that called accept on this broker 
    }

    @Override
    public Channel connect(String name, int port) {
        Broker b = manager.getBroker(name);
        if (b == null) {
            throw new IllegalArgumentException("Broker with name " + name + " does not exist.");
        }
        // Create and return a SimpleChannel instead of relying on sub_connect
        return this.sub_connect(b, port);
    }


    @Override
    public Channel accept(int port) {
        Rdv rdv = null; 
        synchronized(rdvPoints){
            rdv = rdvPoints.get(port);
            if(rdv == null){
                rdv = new Rdv();
                rdvPoints.put(port, rdv);
                rdvPoints.notifyAll();
            }
        }
        
        // Block until both brokerAcceptor and brokerConnector are registered
        synchronized(rdv) {
            return rdv.accept(this, port);
        }
    }


    private Channel sub_connect(Broker b, int port) {
        Rdv rdv = null;
        synchronized(rdvPoints){
            rdv = rdvPoints.get(port);
            while(rdv == null){
                try{
                    rdvPoints.wait();
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
                rdv = rdvPoints.get(port);
            }
            rdvPoints.remove(port);
        }
        Channel channel = rdv.connect(b , port);
        return channel;
    }
}
