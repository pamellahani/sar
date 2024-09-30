package channels;

import messages.RequestQueue;
import messages.ResponseQueue;

public class SimpleChannel extends Channel {

    private RequestQueue requestQueue;
    private ResponseQueue responseQueue;

    private boolean isDisconnected;
    private final int port;

    private static final int bufferSize = 1024;

    public SimpleChannel(int port, Broker broker) {
        this.port = port;
        this.inBuffer = new CircularBuffer(bufferSize);
        this.outBuffer = new CircularBuffer(bufferSize);
        this.isDisconnected = false;

        // Initialize the request and response queues
        this.requestQueue = new RequestQueue(this);
        this.responseQueue = new ResponseQueue(this);
    }

    public void connectChannels(SimpleChannel other, String brokerName) {
        this.inBuffer = other.outBuffer;
        this.outBuffer = other.inBuffer;
        System.out.println("Connecting to broker " + brokerName + " via port " + this.port);
    }

    public RequestQueue getRequestQueue() {
        return this.requestQueue;
    }

    public ResponseQueue getResponseQueue() {
        return this.responseQueue;
    }

    @Override
    public int read(byte[] bytes, int offset, int length) throws DisconnectedException {
        if (isDisconnected) throw new DisconnectedException("Channel is disconnected");
        int bytesRead = 0;
        while (bytesRead < length && !inBuffer.empty()) {
            bytes[offset + bytesRead++] = inBuffer.pull();
        }
        return bytesRead;
    }

    @Override
    public int write(byte[] bytes, int offset, int length) throws DisconnectedException {
        if (isDisconnected) throw new DisconnectedException("Channel is disconnected");
        int bytesWritten = 0;
        while (bytesWritten < length && !outBuffer.full()) {
            outBuffer.push(bytes[offset + bytesWritten++]);
        }
        return bytesWritten;
    }

    @Override
    public void disconnect() {
        new Thread(() -> {
            synchronized (this) {
                isDisconnected = true; 
            }
            System.out.println("Channel on port " + port + " has been disconnected asynchronously.");
        }).start();
    }

    @Override
    public boolean disconnected() {
        return this.isDisconnected;
    }
}
