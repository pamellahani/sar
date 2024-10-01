package messages;

import channels.*;

public class MessageQueue {
    private final Channel channel; // The channel used for communication
    private final Object lock = new Object(); // Lock object for synchronization

    /**
     * Constructs a MessageQueue that interfaces with a specific channel.
     * @param channel the communication channel used for sending and receiving messages
     */
    public MessageQueue(Channel channel) {
        this.channel = channel;
    }

    /**
     * Sends a byte array to the queue.
     * @param bytes the byte array to send
     * @param offset the start offset in the array
     * @param length the number of bytes to send
     * @throws DisconnectedException if the channel is disconnected during sending
     */
    public void send(byte[] bytes, int offset, int length) throws DisconnectedException {
        synchronized (lock) {
            int bytesWritten = 0;
            while (bytesWritten < length) {
                while (channel.outBuffer.full()) {
                    try {
                        lock.wait(); // Wait until there is space in the buffer
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt(); // Set the interrupt flag again
                        return;
                    }
                }
                bytesWritten += channel.write(bytes, offset + bytesWritten, length - bytesWritten);
            }
            lock.notifyAll(); // Notify any waiting receivers that there is now data in the buffer
        }
    }

    /**
     * Receives a byte array from the queue.
     * @return the byte array received
     * @throws DisconnectedException if the channel is disconnected
     */
    public byte[] receive() throws DisconnectedException {
        synchronized (lock) {
            while (channel.inBuffer.empty()) {
                try {
                    lock.wait(); // Wait until there is data in the buffer
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // Set the interrupt flag again
                    return null;
                }
            }
            byte[] tempBytes = new byte[channel.inBuffer.size()]; // Temp buffer to read data
            int bytesRead = channel.read(tempBytes, 0, tempBytes.length);
            byte[] actualBytes = new byte[bytesRead]; // Create an array of the correct size
            System.arraycopy(tempBytes, 0, actualBytes, 0, bytesRead); // Copy read bytes into the actual array
            lock.notifyAll(); // Notify any waiting senders that there is now space in the buffer
            return actualBytes;
        }
    }
}
