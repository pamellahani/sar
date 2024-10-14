package hybrid;
import java.util.LinkedList;
import java.util.Queue;
import java.nio.ByteBuffer;

import channels.DisconnectedException;
import channels.SimpleChannel;

public class MixedMessageQueue extends MessageQueue {

    private boolean isClosed;
    private Queue<Message> messages_linked_list;
	private SimpleChannel mq_channel;
	private Listener message_listener;
	private Sender sender;

    // Accept EventPump from MixedQueueBroker
    public MixedMessageQueue(SimpleChannel ch) {
        super();
        mq_channel = ch;
        isClosed = false;
        messages_linked_list = new LinkedList<Message>();
        sender = new Sender(this);
        sub_recieve();
        sender.start();
    }

    /**
     * Sets the listener for this message queue to the given listener.
     * 
     * @param l the listener to be set
     */
    @Override
    public void setListener(Listener l) {
        this.message_listener = l;
    }

    /**
     * Sends a message 
     *
     * @param msg the message to be sent to the post event
     * FYI: this method is synchronized because it is called by the sender thread in the 
     * Threaded World 
     * @return true if the message was successfully sent, false otherwise
     */
    @Override
    public boolean send(Message msg) {
        synchronized (sender) {
            messages_linked_list.add(msg);
            sender.notifySender();
        }
        return true;
    }

    @Override
    public void close() {
        synchronized (sender) {
            mq_channel.disconnect();
            isClosed = true;
            sender.notifyAll();  // Notify the sender thread that it should stop
            System.out.println("MessageQueue is closing, notifying sender thread.");
        }
        sender.stopSender();  // Explicitly stop the sender
    }
    
    @Override
    public boolean closed() {
        return mq_channel.disconnected();
    }

    private void sub_recieve() {
		new Thread(new Runnable() {
			@Override
			public void run() {
                // Receiver thread to be implemented
            }
		}).start();
	}

    public void sub_send(Message msg) {
        // Convert the length of the message into a byte array without manual bit shifting
        byte[] size = ByteBuffer.allocate(4).putInt(msg.getLength()).array();
    
        // Send size of the message
        sendBytes(size, 0, size.length);
    
        // Send the actual message
        sendBytes(msg.getBytes(), msg.getOffset(), msg.getLength());
    
        // Notify listener that the message has been sent
        EventTask task = new EventTask();
        task.post(() -> message_listener.sent(msg));
    }
    
    private void sendBytes(byte[] data, int offset, int length) {
        int nbSentBytes = 0;
        while (nbSentBytes < length) {
            try {
                nbSentBytes += mq_channel.write(data, offset + nbSentBytes, length - nbSentBytes);
            } catch (DisconnectedException e) {
                e.printStackTrace();
                break;
            }
        }
    }
    
    public Queue<Message> getMessagesQueue() {
        return messages_linked_list;
    }

    public boolean isClosed() {
        return isClosed;
    }
}
