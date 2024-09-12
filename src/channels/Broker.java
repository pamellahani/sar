package channels;

public abstract class Broker {
    String name;

    public Broker(String name) {
        this.name = name;
    }

    public abstract Channel accept(int port);
    public abstract Channel connect(String name, int port);
}

