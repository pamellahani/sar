package channels;

public class SimpleBroker extends Broker {
    
    public SimpleBroker(String name) {
        super(name);
    }

    @Override
    public Channel accept(int port) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'accept'");
    }

    @Override
    public Channel connect(String name, int port) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'connect'");
    }

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getName'");
    }

    public BrokerManager getManager() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getManager'");
    }


    
}
