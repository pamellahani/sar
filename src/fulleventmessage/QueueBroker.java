package fulleventmessage;

import fulleventchannel.Broker.AcceptListener;
import fulleventchannel.Broker.ConnectListener;

public abstract class QueueBroker {

    public abstract boolean bind(int port, AcceptListener listener);
    public abstract boolean unbind(int port);
    
    public abstract boolean connect(String name, int port, ConnectListener listener);
    
    public abstract String getName();
}
