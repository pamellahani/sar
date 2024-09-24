package channels;

public class SimpleChannel extends Channel{

    public SimpleChannel(CircularBuffer buffer) {
        this.inBuffer = new CircularBuffer(1024); 
        this.outBuffer = new CircularBuffer(1024);
        this.isDisconnected = false;  // Initially, the channel is connected
    }

    @Override
    public synchronized int read(byte[] bytes, int offset, int length) {
        if (isDisconnected) {
            return -1;
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
    public synchronized int write(byte[] bytes, int offset, int length) {
        if (isDisconnected) {
            return -1;
        }
    
        int bytesWritten = 0;
        for (int i = offset; i < offset + length && !outBuffer.full(); i++) {
            outBuffer.push(bytes[i]);
            bytesWritten++;
        }
    
        System.out.println("Channel wrote " + bytesWritten + " bytes to the outBuffer.");
        return bytesWritten;
    }
    

    @Override
    public void disconnect() {
        isDisconnected = true; 
    }

    @Override
    public boolean disconnected() {
       return isDisconnected;
    }
    
}
