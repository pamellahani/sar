package channels;

public class SimpleChannel extends Channel {

    private Broker clientBroker;
    //private Broker serverBroker;
    private CircularBuffer clientInBuffer;
    private CircularBuffer serverInBuffer;
    private CircularBuffer clientOutBuffer;
    private CircularBuffer serverOutBuffer;
    private boolean isDisconnected;

    private static final int bufferSize = 1024;

    public SimpleChannel(Broker clientBroker, Broker serverBroker) {
        this.clientBroker = clientBroker;
        //this.serverBroker = serverBroker;
        this.clientInBuffer = new CircularBuffer(bufferSize); // Client's inBuffer
        this.serverInBuffer = new CircularBuffer(bufferSize); // Server's inBuffer
        this.clientOutBuffer = serverInBuffer; // Client's outBuffer is Server's inBuffer
        this.serverOutBuffer = clientInBuffer; // Server's outBuffer is Client's inBuffer
        this.isDisconnected = false; // Channel is initially connected
    }

    @Override
    public synchronized int read(byte[] bytes, int offset, int length) {
        CircularBuffer inBuffer = (clientBroker != null) ? clientInBuffer : serverInBuffer; // Determine if client or server buffer should be read
        int bytesRead = 0;

        try {
            for (int i = 0; i < length; i++) {
                if (inBuffer.empty()) break;
                bytes[offset + i] = inBuffer.pull();
                bytesRead++;
            }
        } catch (IllegalStateException e) {
            System.out.println("Buffer is empty, cannot read.");
        }

        return bytesRead;
    }

    @Override
    public synchronized int write(byte[] bytes, int offset, int length) {
        CircularBuffer outBuffer = (clientBroker != null) ? clientOutBuffer : serverOutBuffer; // Determine if client or server buffer should be written
        int bytesWritten = 0;

        try {
            for (int i = 0; i < length; i++) {
                outBuffer.push(bytes[offset + i]);
                bytesWritten++;
            }
        } catch (IllegalStateException e) {
            System.out.println("Buffer is full, cannot write.");
        }

        return bytesWritten;
    }

    @Override
    public void disconnect() {
        this.isDisconnected = true;
    }

    @Override
    public boolean disconnected() {
        return this.isDisconnected;
    }
}
