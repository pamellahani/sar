package hybrid;

import channels.Broker;

public abstract class QueueBroker {

    /**
     * FYI:
     *  even though this variable is not used here, it is required in order to set the broker name 
     *  in Broker class (in the constructor)
    **/
    @SuppressWarnings("unused") 

    private String brokerName;

    //define constructor: QueueBroker(String name)
    protected QueueBroker(String name) {
        this.brokerName = name;
    }

    /**
     * Listener interface for accepting incoming connections
     */
    public interface AcceptListener {
        void accepted(MessageQueue q);
    }

    public abstract boolean bind (int port, AcceptListener l) ; 
    public abstract boolean unbind(int port);

    /**
     * Listener interface for connecting to a remote host.
     */
    public interface ConnectListener {


        void connected(MessageQueue q);
        void refused();
    }

    public abstract boolean connect(String name, int port, ConnectListener l);
    
    public abstract Broker getBroker();
}
