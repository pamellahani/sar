package messages;

import channels.*;

public class MessageQueueImpl extends MessageQueue {
    private final Channel channel; // The channel used for communication

    /**
     * Constructs a MessageQueue that interfaces with a specific channel.
     * @param channel the communication channel used for sending and receiving messages
     */
    public MessageQueueImpl(Channel channel) {
        this.channel = channel;
    }

    /**
     * Sends a byte array to the queue.
     * @param bytes the byte array to send
     * @param offset the start offset in the array
     * @param length the number of bytes to send
     * @throws DisconnectedException if the channel is disconnected during sending
     */
    @Override
    public void send(byte[] bytes, int offset, int length) {
        int bytesWritten = 0;
        while (bytesWritten < length) {
            while (channel.outBuffer.full()) {
                // Retry until there is space in the buffer
                if (channel.disconnected()) {
                    return; // Exit if the channel is disconnected
                }
            }
            try {
                bytesWritten += channel.write(bytes, offset + bytesWritten, length - bytesWritten);
            } catch (DisconnectedException e) {
                e.printStackTrace();
                return;
            }
        }
    }

    /**
     * Receives a byte array from the queue.
     * @return the byte array received
     * @throws DisconnectedException if the channel is disconnected
     */
    @Override
    public byte[] receive() {
        while (channel.inBuffer.empty()) {
            if (channel.disconnected()) {
                return null; // Exit if the channel is disconnected
            }
        }
        byte[] tempBytes = new byte[channel.inBuffer.size()]; // Temp buffer to read data
        int bytesRead = 0;

        try {
            bytesRead = channel.read(tempBytes, 0, tempBytes.length);
        } catch (DisconnectedException e) {
            e.printStackTrace();
        }

        byte[] actualBytes = new byte[bytesRead]; // Create an array of the correct size
        System.arraycopy(tempBytes, 0, actualBytes, 0, bytesRead);

        return actualBytes;
    }

    @Override
    public void close() {
        channel.disconnect();
    }

    @Override
    public boolean closed() {
        return channel.disconnected();
    }
}
