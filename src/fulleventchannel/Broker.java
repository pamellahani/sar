package fulleventchannel; 

/**
 * The {@code Broker} class serves as an abstract base class for creating and managing
 * communication channels. It provides a framework for accepting and connecting to channels
 * using specified ports and names.
 * <p>
 * Subclasses of {@code Broker} must implement the {@code accept} and {@code connect} methods
 * to define the specific behavior for accepting and connecting to channels.
 * </p>
 */
public abstract class Broker {

    protected final String name;

    public Broker(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Broker name cannot be null or empty.");
        }
        this.name = name;
        System.out.println("Broker created: " + name);
    }

    public String getName() {
        return name;
    }

    public abstract boolean connect(String name, int port, ConnectListener listener);
    public abstract boolean bind(int port, AcceptListener listener);
    public abstract boolean unbind(int port);

    public interface AcceptListener {
        void accepted(Channel channel);
    }

    public interface ConnectListener {
        void connected(Channel channel);
        void refused();
    }
}
