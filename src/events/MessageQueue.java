package events;

public abstract class MessageQueue {
    
    /**
     * Listener interface for receiving messages from the MessageQueue.
     */
    public interface Listener {

        /**
         * Called when a message is received.
         * @param msg
         */
        void received (byte[] msg); 

        /**
         * Called when a message is sent.
         * @param msg
         */
        void sent(Message msg);

        /**
         * Called when the connection is closed.
         */
        void closed();  
    }

    /**
     * Set the listener for the MessageQueue.
     * @param l
     */
    abstract void setListener(Listener l); 

    /**
     * Send a message.
     * @param msg
     * @return true if the message was sent, false otherwise.
     */
    abstract boolean send(Message msg);

    /**
     * Close the connection.
     */
    abstract void close();

    /**
     * Check if the connection is closed.
     * @return true if the connection is closed, false otherwise.
     */
    abstract boolean closed();


}
