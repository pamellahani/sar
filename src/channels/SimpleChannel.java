package channels;

public class SimpleChannel extends Channel{

    private Broker broker;
    private int port;

    public SimpleChannel(Broker b , int port){
        this.broker = b;
        this.port = port;
        this.inBuffer = new CircularBuffer(1024); 
        this.outBuffer = new CircularBuffer(1024);
        this.isDisconnected = false;  // Initially, the channel is connected
    }


    @Override
    public synchronized int read(byte[] bytes, int offset, int length) throws DisconnectedException {
        while (inBuffer.empty()) {  // Block if no bytes are available
            if (isDisconnected) {
                throw new DisconnectedException("Channel is disconnected.");
            }
    
            try {
                wait();  // Block until notified (e.g., when new bytes are written)
            } catch (InterruptedException e) {
                //throw new DisconnectedException("Thread was interrupted while waiting to read.");
            }
        }
    
        int bytesRead = 0;
        for (int i = offset; i < offset + length && !inBuffer.empty(); i++) {
            bytes[i] = inBuffer.pull();
            bytesRead++;
        }
    
        System.out.println("Channel read " + bytesRead + " bytes from the inBuffer.");
        return bytesRead;
    }
    
    
    @Override
    public synchronized int write(byte[] bytes, int offset, int length) throws DisconnectedException {
        if (isDisconnected) {
            throw new DisconnectedException("Channel is disconnected.");
        }

        int bytesWritten = 0;
        for (int i = offset; i < offset + length && !outBuffer.full(); i++) {
            outBuffer.push(bytes[i]);
            bytesWritten++;
        }

        notify();  // Notify waiting reader when bytes are written. we cannot have 2 readers at the same time
        System.out.println("Channel wrote " + bytesWritten + " bytes to the outBuffer.");
        return bytesWritten;
    }

    @Override
    public synchronized void disconnect() {
        isDisconnected = true;
        notifyAll();  // Notify all waiting threads that the channel is disconnected
    }

    @Override
    public boolean disconnected() {
       return isDisconnected;
    }
    
}
