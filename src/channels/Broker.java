package channels;

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
    protected final String name;  // Made final as the broker's name should not change
   // protected final BrokerManager manager;

    public Broker(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Broker name cannot be null or empty.");
        }
        this.name = name;
    }

    public String getName() {
        return name;
    }

    // Abstract methods to be implemented by subclasses
    public abstract Channel accept(int port);
    public abstract Channel connect(String name, int port);
}
