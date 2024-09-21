/**
 * The {@code Broker} class serves as an abstract base class for creating and managing
 * communication channels. It provides a framework for accepting and connecting to channels
 * using specified ports and names.
 * <p>
 * Subclasses of {@code Broker} must implement the {@code accept} and {@code connect} methods
 * to define the specific behavior for accepting and connecting to channels.
 * </p>
 */
package channels;

public abstract class Broker {
    String name;
    BrokerManager manager;

    public Broker(String name) {
        this.name = name;
    }

    public abstract Channel accept(int port);
    public abstract Channel connect(String name, int port);
    public abstract String getName();

}

