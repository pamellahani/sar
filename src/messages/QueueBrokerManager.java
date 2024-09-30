package messages;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class QueueBrokerManager {

    private final Map<String, QueueBroker> brokers;

    public QueueBrokerManager() {
        brokers = new ConcurrentHashMap<>();
    }

    public synchronized QueueBroker getBroker(String name) {
        return brokers.get(name);
    }

    public synchronized void registerBroker(QueueBroker broker) {
        brokers.put(broker.broker.getName(), broker);
    }

    public synchronized void deregisterBroker(QueueBroker broker) {
        brokers.remove(broker.broker.getName());
    }
}
