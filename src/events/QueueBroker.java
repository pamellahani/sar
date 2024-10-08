package events;

import channels.Broker;

public abstract class QueueBroker {

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
