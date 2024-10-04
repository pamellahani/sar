package events;

import channels.*;

public class MixedMessageQueue extends MessageQueue {

    protected Channel channel;
    protected Listener listener;
    private final EventPump eventPump;  // Use EventPump for posting events

    public MixedMessageQueue(Channel channel) {
        this.channel = channel;
        this.eventPump = new EventPump();  // Initialize the EventPump
    }

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
