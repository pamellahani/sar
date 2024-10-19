package fullevent;

import channels.Channel; //abstract class
import channels.DisconnectedException;
import channels.CircularBuffer;

public class EventChannel extends Channel {

    public interface ChannelListener {
        void onBufferFull(EventChannel channel);
        void onBufferNotFull(EventChannel channel);
        void onBufferEmpty(EventChannel channel);
        void onBufferNotEmpty(EventChannel channel);
    }

    private ChannelListener listener;
    private CircularBuffer buffer;
    private boolean disconnected;
    
    public EventChannel(int bufferSize) {
        this.buffer = new CircularBuffer(bufferSize);
        this.disconnected = false;
    }

    public void setChannelListener(ChannelListener listener) {
        this.listener = listener;
    }

    @Override
    public int read(byte[] bytes, int offset, int length) throws DisconnectedException {
        if (disconnected) {
            throw new DisconnectedException("Channel is disconnected");
        }

        if (buffer.empty()) {
            if (listener != null) {
                listener.onBufferEmpty(this);
            }
            return -1; // No data to read
        }

        int bytesRead = 0;
        try {
            while (bytesRead < length && !buffer.empty()) {
                bytes[offset + bytesRead] = buffer.pull();
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
                if (buffer.full()) {
                    if (listener != null) {
                        listener.onBufferFull(this);
                    }
                    break; // Buffer is full
                }
                buffer.push(bytes[offset + bytesWritten]);
                bytesWritten++;
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }

        if (listener != null) {
            if (bytesWritten > 0) {
                listener.onBufferNotFull(this);
            }
            if (!buffer.empty()) {
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
    
}
