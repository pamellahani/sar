package channels;

public class SimpleChannel extends Channel{

    public SimpleChannel(CircularBuffer buffer) {
        //TODO: super(buffer); -> buffer is not yet defined in the scope of this class
    }

    @Override
    public int read(byte[] bytes, int offset, int length) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'read'");
    }

    @Override
    public int write(byte[] bytes, int offset, int length) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'write'");
    }

    @Override
    public void disconnect() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'disconnect'");
    }

    @Override
    public boolean disconnected() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'disconnected'");
    }
    
}
