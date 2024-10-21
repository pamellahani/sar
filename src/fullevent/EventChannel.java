package fullevent;

import channels.DisconnectedException;
import channels.CircularBuffer;

public class EventChannel extends Channel {

    
    private ChannelListener listener;
    private CircularBuffer inBuffer;
    private CircularBuffer outBuffer;
    private boolean disconnected;
    private int port;


    public interface ChannelListener {
        void onBufferFull(EventChannel channel);
        void onBufferNotFull(EventChannel channel);
        void onBufferEmpty(EventChannel channel);
        void onBufferNotEmpty(EventChannel channel);
    }

    public EventChannel(int bufferSize, int port, fullevent.Broker broker) {
        this.inBuffer = new CircularBuffer(bufferSize);
        this.outBuffer = new CircularBuffer(bufferSize);
        this.disconnected = false;
        this.port = port;
    }

    public void setChannelListener(ChannelListener listener) {
        this.listener = listener;
    }

    @Override
    public int read(byte[] bytes, int offset, int length) throws DisconnectedException {
        if (disconnected) {
            throw new DisconnectedException("Channel is disconnected");
        }

        if (inBuffer.empty()) {
            if (listener != null) {
                listener.onBufferEmpty(this);
            }
            return -1; // No data to read
        }

        int bytesRead = 0;
        try {
            while (bytesRead < length && !inBuffer.empty()) {
                bytes[offset + bytesRead] = inBuffer.pull();
                bytesRead++;
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }

        if (listener != null && bytesRead > 0) {
            listener.onBufferNotEmpty(this);
        }

        return bytesRead;
    }

    @Override
    public int write(byte[] bytes, int offset, int length) throws DisconnectedException {
        if (disconnected) {
            throw new DisconnectedException("Channel is disconnected");
        }

        int bytesWritten = 0;
        try {
            while (bytesWritten < length) {
                if (outBuffer.full()) {
                    if (listener != null) {
                        listener.onBufferFull(this);
                    }
                    break; // Buffer is full
                }
                outBuffer.push(bytes[offset + bytesWritten]);
                bytesWritten++;
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }

        if (listener != null) {
            if (bytesWritten > 0) {
                listener.onBufferNotFull(this);
            }
            if (!outBuffer.empty()) {
                listener.onBufferNotEmpty(this);
            }
        }

        return bytesWritten;
    }

    @Override
    public void disconnect() {
        disconnected = true;
        if (listener != null) {
            listener.onBufferEmpty(this);
        }
    }

    @Override
    public boolean disconnected() {
        return disconnected;
    }

    public void connectChannels(EventChannel other, String brokerName) {
        // Set listeners to facilitate data transfer between channels
        this.setChannelListener(new ChannelListener() {
            @Override
            public void onBufferFull(EventChannel channel) {
                System.out.println("Buffer full on channel: " + brokerName);
            }

            @Override
            public void onBufferNotFull(EventChannel channel) {
                System.out.println("Buffer not full on channel: " + brokerName);
            }

            @Override
            public void onBufferEmpty(EventChannel channel) {
                System.out.println("Buffer empty on channel: " + brokerName);
            }

            @Override
            public void onBufferNotEmpty(EventChannel channel) {
                transferData(EventChannel.this, other);
            }
        });

        other.setChannelListener(new ChannelListener() {
            @Override
            public void onBufferFull(EventChannel channel) {
                System.out.println("Buffer full on accepting channel: " + brokerName);
            }

            @Override
            public void onBufferNotFull(EventChannel channel) {
                System.out.println("Buffer not full on accepting channel: " + brokerName);
            }

            @Override
            public void onBufferEmpty(EventChannel channel) {
                System.out.println("Buffer empty on accepting channel: " + brokerName);
            }

            @Override
            public void onBufferNotEmpty(EventChannel channel) {
                transferData(other, EventChannel.this);
            }
        });

        System.out.println("Channels successfully connected: " + brokerName + " via port " + this.port);
    }

    private void transferData(EventChannel sourceChannel, EventChannel destinationChannel) {
        byte[] buffer = new byte[256]; // Example buffer size
        int bytesRead;

        while (!sourceChannel.outBuffer.empty()) {
            bytesRead = 0;
            try {
                // Pull data from source channel's outBuffer
                while (bytesRead < buffer.length && !sourceChannel.outBuffer.empty()) {
                    buffer[bytesRead] = sourceChannel.outBuffer.pull();
                    bytesRead++;
                }

                // Write data to destination channel's inBuffer
                if (bytesRead > 0) {
                    for (int i = 0; i < bytesRead; i++) {
                        destinationChannel.inBuffer.push(buffer[i]);
                    }
                }

            } catch (Exception e) {
                System.err.println("Error during channel transfer: " + e.getMessage());
            }
        }
    }
}
