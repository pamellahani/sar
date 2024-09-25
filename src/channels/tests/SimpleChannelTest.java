package channels.tests;

import  channels.*; 

public class SimpleChannelTest {
    public static void main(String[] args) {
        // Test 1: Writing and Reading through SimpleChannel
        Broker clientBroker = new SimpleBroker("ClientBroker", new BrokerManager());
        Broker serverBroker = new SimpleBroker("ServerBroker", new BrokerManager());

        SimpleChannel channel = new SimpleChannel(clientBroker, serverBroker);
        byte[] message = "Hello".getBytes();
        int bytesWritten = channel.write(message, 0, message.length);
        assert bytesWritten == message.length : "Bytes written should match the message length";

        byte[] buffer = new byte[5];
        int bytesRead = channel.read(buffer, 0, buffer.length);
        assert new String(buffer).equals("Hello") : "Received message should be 'Hello'";
        assert bytesRead == 5 : "Bytes read should be 5";
    }
}
