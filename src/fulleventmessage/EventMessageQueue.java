package fulleventmessage;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.Queue;

import fulleventchannel.EventChannel;
import fulleventchannel.EventTask;
import hybrid.Message;

public class EventMessageQueue extends MessageQueue {

   private boolean isClosed;
    private Queue<Message> messagesQueue;
    private EventChannel mqChannel;
    private MessageListener messageListener;

    public EventMessageQueue(EventChannel ch) {
        mqChannel = ch;
        isClosed = false;
        messagesQueue = new LinkedList<>();
    }

    @Override
    public void setMessageListener(MessageListener listener) {
        this.messageListener = listener;
    }

    @Override
    public boolean send(Message message) {
        if (isClosed) {
            return false;
        }
        
        // Add message to the queue and trigger the sending process
        messagesQueue.add(message);
        processMessages();  // Trigger the event-based process
        return true;
    }

    @Override
    public void close() {
        isClosed = true;
        mqChannel.disconnect();
        System.out.println("MessageQueue is closing.");
    }

    @Override
    public boolean closed() {
        return mqChannel.disconnected();
    }

    private void processMessages() {
        if (!messagesQueue.isEmpty()) {
            // Use EventTask to handle sending in a non-blocking event-driven way
            EventTask task = new EventTask();
            task.post(() -> {
                Message message = messagesQueue.poll();
                if (message != null) {
                    subSend(message);
                }
            });
        }
    }

    private void subSend(Message msg) {
        // Convert the length of the message into a byte array
        byte[] size = ByteBuffer.allocate(4).putInt(msg.getLength()).array();
    
        // Send size of the message
        sendBytes(size, 0, size.length);
    
        // Send the actual message
        sendBytes(msg.getBytes(), msg.getOffset(), msg.getLength());
    
        // Notify listener that the message has been sent
        if (messageListener != null) {
            EventTask task = new EventTask();
            task.post(() -> messageListener.sent(msg));
        }
    }

    private void sendBytes(byte[] data, int offset, int length) {
        int nbSentBytes = 0;
        while (nbSentBytes < length) {
            if (mqChannel.write(data)) {
                nbSentBytes += data.length;
            } else {
                break;
            }
        }
    }
}
