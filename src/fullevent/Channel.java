package fullevent;

import channels.CircularBuffer;
import channels.DisconnectedException;

/**
 * The {@code Channel} class represents an abstract communication channel.
 * It provides methods for reading from and writing to the channel, as well as
 * methods for managing the connection state.
 * <p>
 * Subclasses of {@code Channel} must implement the {@code read}, {@code write}, {@code disconnect}, and {@code disconnected}
 */

public abstract class Channel {

    public CircularBuffer inBuffer;
    public CircularBuffer outBuffer;
    protected boolean isDisconnected;

    public abstract int read(byte[] bytes, int offset, int length) throws DisconnectedException; ;
    public abstract int write(byte[] bytes, int offset, int length) throws DisconnectedException ;
    public abstract void disconnect();
    public abstract boolean disconnected();
    
}
