package messages;

import channels.Channel;
import channels.DisconnectedException;

public abstract class MessageQueue {

    protected Channel channel;

    public MessageQueue(Channel channel) {
        this.channel = channel;
    }

    public void send(byte[] bytes, int offset, int length) throws DisconnectedException {
        channel.write(bytes, offset, length);
    }

    public byte[] receive() throws DisconnectedException {
        byte[] buffer = new byte[1024];
        int bytesRead = channel.read(buffer, 0, buffer.length);
        byte[] receivedData = new byte[bytesRead];
        System.arraycopy(buffer, 0, receivedData, 0, bytesRead);
        return receivedData;
    }

    public void close() {
        channel.disconnect();
    }

    public boolean closed() {
        return channel.disconnected();
    }
}
