package channels;

import java.util.HashMap;
import java.util.Map;

public class SimpleChannel extends Channel {

    private Broker broker;  // The broker (either client or server) that owns this channel
    private CircularBuffer inBuffer;
    private CircularBuffer outBuffer;
    private boolean isDisconnected;
    private final int port;
    
    private static final int bufferSize = 1024;
    
    // Static map to keep track of channels by port, allowing them to find each other
    private static final Map<Integer, SimpleChannel> channelsByPort = new HashMap<>();

    public SimpleChannel(int port, Broker broker) {
        this.port = port;
        this.broker = broker;
        this.inBuffer = new CircularBuffer(bufferSize);  // Incoming data buffer
        this.outBuffer = new CircularBuffer(bufferSize);  // Outgoing data buffer
        this.isDisconnected = false;  // Initially connected

        // Register this channel for the given port
        synchronized (channelsByPort) {
            SimpleChannel existingChannel = channelsByPort.get(port);
            if (existingChannel == null) {
                channelsByPort.put(port, this);  // No channel on this port yet, register this one
            } else {
                // If another channel exists on the same port, connect them
                connectChannels(existingChannel, broker.getName());
                existingChannel.connectChannels(this, broker.getName());  // Establish a two-way connection
            }
        }
    }

    /**
     * Connects this channel with another one on the same port.
     * This allows two brokers to communicate with each other.
     */
    public synchronized void connectChannels(SimpleChannel otherChannel, String remoteBrokerName) {
        this.outBuffer = otherChannel.inBuffer;  // This channel's outgoing buffer is the other channel's incoming buffer
        otherChannel.outBuffer = this.inBuffer;  // The other channel's outgoing buffer is this channel's incoming buffer
    }

    @Override
    public synchronized int read(byte[] bytes, int offset, int length) {
        if (isDisconnected) {
            return -1;
        }

        // Check if the buffer is empty before reading
        if (inBuffer.empty()) {
            return 0;  // No data to read
        }

        // Read from the inBuffer and write to the bytes array
        int bytesRead = 0;
        for (int i = offset; i < offset + length && !inBuffer.empty(); i++) {
            bytes[i] = inBuffer.pull();
            bytesRead++;
        }
        return bytesRead;
    }

    @Override
    public synchronized int write(byte[] bytes, int offset, int length) {
        if (isDisconnected) {
            return -1;
        }

        // Write to the outBuffer from the bytes array
        int bytesWritten = 0;
        for (int i = offset; i < offset + length && !outBuffer.full(); i++) {
            outBuffer.push(bytes[i]);
            bytesWritten++;
        }
        return bytesWritten;
    }

    @Override
    public void disconnect() {
        this.isDisconnected = true;

        // Remove this channel from the static map when disconnected
        synchronized (channelsByPort) {
            channelsByPort.remove(port);
        }
    }

    @Override
    public boolean disconnected() {
        return this.isDisconnected;
    }
}
