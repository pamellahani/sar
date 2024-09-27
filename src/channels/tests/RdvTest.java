package channels.tests;

import channels.BrokerManager;
import channels.Channel;
import channels.Rdv;
import channels.SimpleBroker;

public class RdvTest {
    public static void main(String[] args) throws InterruptedException {
        // Test 1: Broker acceptor and connector
        BrokerManager manager = new BrokerManager();
        SimpleBroker acceptorBroker = new SimpleBroker("AcceptorBroker", manager);
        SimpleBroker connectorBroker = new SimpleBroker("ConnectorBroker", manager);
        Rdv rdv = new Rdv(true, acceptorBroker, 8080);

        // manager.registerBroker(acceptorBroker);
        // manager.registerBroker(connectorBroker);


        //TODO: bug here -> infinite loop on connect method
        Thread acceptorThread = new Thread(() -> {
            Channel acceptorChannel = rdv.accept(acceptorBroker, 8080);
            assert acceptorChannel != null : "Acceptor channel should not be null";
        });

        Thread connectorThread = new Thread(() -> {
            Channel connectorChannel = rdv.connect(connectorBroker, 8080);
            assert connectorChannel != null : "Connector channel should not be null";
        });

        acceptorThread.start();
        //Thread.sleep(100);  // Ensure the acceptor is ready before the connector starts
        connectorThread.start();

        acceptorThread.join();
        connectorThread.join();
    }
}
