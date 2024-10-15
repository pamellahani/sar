package hybrid;

public abstract class MessageQueue {
    
    /**
     * Listener interface for receiving messages from the MessageQueue.
     */
    public interface Listener {

        void received (byte[] msg); 
        void sent(Message msg);
        void closed();  
    }
    
    public abstract void setListener(Listener l); 

    public abstract boolean send(Message msg);

    public abstract void close();

    public abstract boolean closed();

    public abstract Listener getMessageListener();


}
