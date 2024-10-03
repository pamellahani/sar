package channels.tests;

import channels.*;

public class BrokerManagerTest {
    public static void main(String[] args) {
        // Test 1: Register and retrieve broker
        BrokerManager manager = new BrokerManager();
        Broker broker = new SimpleBroker("TestBroker", manager);
        manager.registerBroker(broker);

        Broker retrievedBroker = manager.getBrokerFromBM("TestBroker");
        assert retrievedBroker != null : "Retrieved broker should not be null";

        // Test 2: Deregister broker
        manager.deregisterBroker(broker);
        Broker afterDeregister = manager.getBrokerFromBM("TestBroker");
        assert afterDeregister != null : "Broker should be not null after deregistration";
    }
}
