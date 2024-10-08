package events;

import channels.*;

public class MixedMessageQueue extends MessageQueue {

    protected Channel channel;
    protected Listener listener;
    private final EventPump eventPump;  

    // Accept EventPump from MixedQueueBroker
    public MixedMessageQueue(Channel channel, EventPump eventPump) {
        this.channel = channel;
        this.eventPump = eventPump; 
    }

    /**
     * Sets the listener for this message queue and starts an event pump to listen for incoming messages.
     * 
     * @param l the listener to be set
     */
    @Override
    public void setListener(Listener l) {
        this.listener = l;
        // Post an event to continuously listen for incoming messages
        eventPump.push(() -> {
            byte[] buffer = new byte[1024];
            while (!channel.disconnected()) {
                try {
                    int bytesRead = channel.read(buffer, 0, buffer.length);
                    if (bytesRead > 0) {
                        listener.received(buffer);
                    }
                } catch (DisconnectedException e) {
                    listener.closed();
                    break;
                }
            }
        });
    }

    /**
     * Sends a message by pushing a task to the event pump that writes the message
     * to the channel and notifies the listener. If a DisconnectedException occurs,
     * the listener is notified of the closure.
     *
     * @param msg the message to be sent
     * @return true if the message was successfully pushed to the event pump
     */
    @Override
    public boolean send(Message msg) {
        eventPump.push(() -> {
            try {
                channel.write(msg.bytes, msg.offset, msg.length);
                listener.sent(msg);
            } catch (DisconnectedException e) {
                listener.closed();
            }
        });
        return true;
    }

    @Override
    public void close() {
        // Post an event to close the channel
        eventPump.push(() -> channel.disconnect());
    }

    @Override
    public boolean closed() {
        return channel.disconnected();
    }
}
