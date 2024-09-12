package channels;

public class SimpleChannel extends Channel{
    
    private CircularBuffer buffer;

    public SimpleChannel(CircularBuffer buffer) {
        this.buffer = buffer;
    }

    @Override
    int read(byte[] bytes, int offset, int length) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'read'");
    }

    @Override
    int write(byte[] bytes, int offset, int length) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'write'");
    }

    @Override
    void disconnect() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'disconnect'");
    }

    @Override
    boolean disconnected() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'disconnected'");
    }
    
}
