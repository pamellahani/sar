package events;

import channels.Broker;

public abstract class QueueBroker {

    /**
     * Listener interface for accepting incoming connections
     */
    public interface AcceptListener {
        void accepted(MessageQueue q);
    }

    /**
     * Bind to a port and listen for incoming connections.
     * @param port
     * @param acceptListener
     * @return true if the bind was successful, false otherwise.
     */
    public abstract boolean bind (int port, AcceptListener l) ; 


    /**
     * Unbind from a port.
     * @param port
     * @return true if the unbind was successful, false otherwise.
     */
    public abstract boolean unbind(int port);

    /**
     * Listener interface for connecting to a remote host.
     */
    public interface ConnectListener {

        /**
         * Called when the connection is established.
         * @param queue
         */
        void connected(MessageQueue q);

        /**
         * Called when the connection is refused.
         */
        void refused();
    }

    /**
     * Connect to a remote host.
     * @param name of Broker
     * @param port
     * @param connectListener
     * @return
     */ 
    public abstract boolean connect(String name, int port, ConnectListener l);
    

    /**
     * Get the Broker object
     * @return Broker object
     */
    public abstract Broker getBroker();
}
