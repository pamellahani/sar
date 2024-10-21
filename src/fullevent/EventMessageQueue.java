package fullevent;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.Queue;

import channels.DisconnectedException;
import hybrid.Message;

public class EventMessageQueue extends MessageQueue {

    private boolean isClosed;
    private Queue<Message> messagesLinkedQueue;
    private EventChannel mqChannel;
    private MessageListener messageListener;

    // Accept EventChannel from QueueBroker
    public EventMessageQueue(EventChannel channel) {
        super();
        mqChannel = channel;
        isClosed = false;
        messagesLinkedQueue = new LinkedList<Message>();
        mqChannel = channel;
    }

    /**
     * Sets the listener for this message queue to the given listener.
     *
     * @param l the listener to be set
     */
    @Override
    public void setListener(MessageListener l) {
        this.messageListener = l;
    }

    /**
     * Sends a message
     *
     * @param msg the message to be sent to the post event
     * @return true if the message was successfully sent, false otherwise
     */
    @Override
    public boolean send(Message msg) {
        byte[] size = ByteBuffer.allocate(4).putInt(msg.getLength()).array();
        boolean sizeSent = sendBytes(size, 0, size.length);
        if (!sizeSent) {
            return false;
        }
        return sendBytes(msg.getBytes(), msg.getOffset(), msg.getLength());
    }

    @Override
    public void close() {
        isClosed = true;
        mqChannel.disconnect();
    }

    @Override
    public boolean closed() {
        return mqChannel.disconnected();
    }

    private boolean sendBytes(byte[] data, int offset, int length) {
        int nbSentBytes = 0;
        while (nbSentBytes < length) {
            try {
                int written = mqChannel.write(data, offset + nbSentBytes, length - nbSentBytes);
                if (written == -1) {
                    return false;
                }
                nbSentBytes += written;
            } catch (DisconnectedException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    public Queue<Message> getMessagesQueue() {
        return messagesLinkedQueue;
    }

    public boolean isClosed() {
        return isClosed;
    }

    @Override
    public MessageListener getMessageListener() {
        return messageListener;
    }
}
