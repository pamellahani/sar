package events;

public abstract class MessageQueue {
    
    /**
     * Listener interface for receiving messages from the MessageQueue.
     */
    public interface Listener {

        void received (byte[] msg); 
        void sent(Message msg);
        void closed();  
    }


    abstract void setListener(Listener l); 

    abstract boolean send(Message msg);

    abstract void close();

    abstract boolean closed();


}
