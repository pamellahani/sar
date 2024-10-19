package fullevent;

import channels.Channel;
import channels.DisconnectedException;

public class EventChannel extends Channel {

    public interface ChannelListener {
        void onBufferFull(EventChannel channel);
        void onBufferNotFull(EventChannel channel);
        void onBufferEmpty(EventChannel channel);
        void onBufferNotEmpty(EventChannel channel);
    }


    private ChannelListener listener;
    private byte[] buffer;
    private int bufferSize;
    private int writePosition;
    private int readPosition;
    private boolean disconnected;

    public EventChannel(int bufferSize) {
        this.bufferSize = bufferSize;
        this.buffer = new byte[bufferSize];
        this.writePosition = 0;
        this.readPosition = 0;
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

        if (readPosition == writePosition) {
            if (listener != null) {
                listener.onBufferEmpty(this);
            }
            return -1; // No data to read
        }

        int bytesRead = 0;
        while (bytesRead < length && readPosition != writePosition) {
            bytes[offset + bytesRead] = buffer[readPosition];
            readPosition = (readPosition + 1) % bufferSize;
            bytesRead++;
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
        while (bytesWritten < length) {
            int nextWritePosition = (writePosition + 1) % bufferSize;
            if (nextWritePosition == readPosition) {
                if (listener != null) {
                    listener.onBufferFull(this);
                }
                break; // Buffer is full
            }

            buffer[writePosition] = bytes[offset + bytesWritten];
            writePosition = nextWritePosition;
            bytesWritten++;
        }

        if (listener != null) {
            if (bytesWritten > 0) {
                listener.onBufferNotFull(this);
            }
            if (readPosition != writePosition) {
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
