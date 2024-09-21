/**
 * The {@code Channel} class represents an abstract communication channel.
 * It provides methods for reading from and writing to the channel, as well as
 * methods for managing the connection state.
 * <p>
 * Subclasses of {@code Channel} must implement the {@code read}, {@code write}, {@code disconnect}, and {@code disconnected}
 */
package channels;

public abstract class Channel {

    public abstract int read(byte[] bytes, int offset, int length);
    public abstract int write(byte[] bytes, int offset, int length);
    public abstract void disconnect();
    public abstract boolean disconnected();
    
}
