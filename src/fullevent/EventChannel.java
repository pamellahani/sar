package fullevent;

import java.util.LinkedList;
import java.util.Queue;

import channels.CircularBuffer;

public class EventChannel extends Channel {

    private static final int MAX_BUFFER_SIZE = 1024;
    public EventChannel otherChannel;
    private boolean disconnected;

    private Queue<byte[]> writerBuffer; 
    private Queue<byte[]> readerBuffer;

    public CircularBuffer inBuffer;
    public CircularBuffer outBuffer;

    private ChannelListener channel_listener;

    public boolean remaining;

    public EventChannel() {
        disconnected = false;
        remaining = false;
        inBuffer = new CircularBuffer(64);
        writerBuffer = new LinkedList<>();
        readerBuffer = new LinkedList<>();
       //System.out.println("New EventChannel created");
    }

    @Override
    public void setChannelListener(ChannelListener listener) {
        channel_listener = listener;
        //System.out.println("Channel listener set: " + listener);
    }

    @Override
    public boolean read(byte[] bytes) {
        //System.out.println("Reading bytes from channel");
        if (channel_listener == null) {
            throw new IllegalStateException("Listener not set");
        }
        if (getReadBufferSize() + bytes.length > MAX_BUFFER_SIZE) {
           // System.out.println("Read buffer is full");
            return false;
        }
        readerBuffer.add(bytes);
        if (readerBuffer.size() <= 1) {
            aux_read(bytes, 0, bytes.length);
        }
        return true;
    }

    private void aux_read(byte[] bytes, int offset, int length) {
        if (disconnected) {
            return;
        }
        if (inBuffer.empty()) {
            if (!remaining) {
                new EventTask().post(() -> aux_read(bytes, offset, length));
            } else {
                readerBuffer.clear();
            }
            return;
        }
        int bytesRead = 0;
        while (bytesRead < length - offset && !inBuffer.empty()) {                
            byte value = inBuffer.pull();
            bytes[offset + bytesRead] = value;
            bytesRead++;
        }
        if (bytesRead >= length - offset) {
            channel_listener.read(readerBuffer.poll());
            byte[] nextBytes = readerBuffer.peek();
            if (nextBytes != null) {
                new EventTask().post(() -> aux_read(nextBytes, 0, nextBytes.length));
            }
            return;
        }
        final int updatedOffset = offset + bytesRead;
        new EventTask().post(() -> aux_read(bytes, updatedOffset, length));
    }

    @Override
    public boolean write(byte[] bytes) {
        //System.out.println("Writing bytes to channel");
        if (channel_listener == null) {
            throw new IllegalStateException("Listener not set");
        }
        if (getWriteBufferSize() + bytes.length > MAX_BUFFER_SIZE) {
           // System.out.println("Write buffer is full");
            return false;
        }
        writerBuffer.add(bytes);
        if (writerBuffer.size() <= 1) {
            aux_write(bytes, 0, bytes.length);
        }
        return true;
    }

    private void aux_write(byte[] bytes, int offset, int length) {
        if (disconnected) {
            return;
        }
        if (remaining) {
            channel_listener.wrote(writerBuffer.poll());
            byte[] nextBytes = writerBuffer.peek();
            if (nextBytes != null) {
                new EventTask().post(() -> aux_write(nextBytes, 0, nextBytes.length));
            }
            return;
        }
        if (outBuffer.full()) {
            new EventTask().post(() -> aux_write(bytes, offset, length));
            return;
        }
        int bytesWritten = 0;
        while (bytesWritten < length - offset && !outBuffer.full()) {                
            outBuffer.push(bytes[offset + bytesWritten++]);
        }
        if (bytesWritten >= length - offset) {
            channel_listener.wrote(writerBuffer.poll());
            byte[] nextBytes = writerBuffer.peek();
            if (nextBytes != null) {
                new EventTask().post(() -> aux_write(nextBytes, 0, nextBytes.length));
            }
            return;
        }
        final int updatedOffset = offset + bytesWritten;
        new EventTask().post(() -> aux_write(bytes, updatedOffset, length));
    }

    @Override
    public void disconnect() {
        disconnected = true;
        otherChannel.remaining = true;
        if (channel_listener == null) {
            throw new IllegalStateException("Listener not set");
        }
        channel_listener.disconnected();
        System.out.println("Channel disconnected");
    }

    @Override
    public boolean disconnected() {
        return disconnected;
    }

    private int getBufferSize(Queue<byte[]> buffer) {
        int sum = 0;
        for (byte[] bytes : buffer) {
            sum += bytes.length;
        }
        return sum;
    }

    private int getWriteBufferSize() {
        return getBufferSize(writerBuffer);
    }

    private int getReadBufferSize() {
        return getBufferSize(readerBuffer);
    }
}
