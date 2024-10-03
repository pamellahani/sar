package channels.tests;

import channels.*; 

public class SimpleBrokerTest {
    public static void main(String[] args) {
        // Test 1: Broker registration and connection
        BrokerManager manager = new BrokerManager();
        SimpleBroker broker1 = new SimpleBroker("Broker1", manager);
        SimpleBroker broker2 = new SimpleBroker("Broker2", manager);

        manager.registerBroker(broker1);
        manager.registerBroker(broker2);

        //TODO: bug here -> infinite loop on connect method
        Channel channel = broker1.connect("Broker2", 8080);
        assert channel != null : "Channel should not be null after connecting brokers";
    }
}
