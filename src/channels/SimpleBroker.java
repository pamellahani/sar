package channels;

public class SimpleBroker extends Broker {
    
    public SimpleBroker(String name) {
        super(name);
    }

    @Override
    Channel accept(int port) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'accept'");
    }

    @Override
    Channel connect(String name, int port) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'connect'");
    }


    
}
