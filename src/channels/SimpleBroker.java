package channels;

public class SimpleBroker extends Broker {
    
    public SimpleBroker(String name, BrokerManager manager) {
        super(name);
        this.manager = manager;
    }

    @Override
    public Channel accept(int port) {
        Rdv rdvPoint = manager.accept(port, this);
        return rdvPoint.getChannel();
    }

    @Override
    public Channel connect(String name, int port) {
        // check if broker exists in the broker manager
        Broker broker = manager.getBroker(name);
        if (broker == null) {
            throw new IllegalArgumentException("Broker not found: " + name);
        }
        Rdv rdvPoint = manager.connect(port, this);
        return rdvPoint.getChannel();
    }

    public BrokerManager getManager() {
        return manager;
    }


    
}
