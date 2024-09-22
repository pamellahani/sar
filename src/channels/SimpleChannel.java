package channels;

public class SimpleChannel extends Channel{

    public SimpleChannel(CircularBuffer buffer) {
        this.inBuffer = new CircularBuffer(1024); 
        this.outBuffer = new CircularBuffer(1024);
        this.isDisconnected = false;  // Initially, the channel is connected
    }

    @Override
    public int read(byte[] bytes, int offset, int length) {
        if (isDisconnected) {
            return -1;
        }

        // Read from the inBuffer and write to the bytes array
        int bytesRead = 0 ; 
        for (int i = offset; i < offset + length && !inBuffer.empty(); i++){  //and check if the inBuffer is empty
            bytes[i] = inBuffer.pull(); 
            bytesRead++;
        }
        return bytesRead;
    }

    @Override
    public int write(byte[] bytes, int offset, int length) {
        if (isDisconnected) {
            return -1;
        }

        // Write to the outBuffer from the bytes array
        int bytesWritten = 0;
        for (int i = offset; i < offset + length && !outBuffer.full(); i++){ //and check if the outBuffer is full
            outBuffer.push(bytes[i]);
            bytesWritten++;
        }
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
