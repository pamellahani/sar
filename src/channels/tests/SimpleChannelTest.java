package channels.tests;

import  channels.*; 

public class SimpleChannelTest {
    public static void main(String[] args) {
        // Test 1: Writing and Reading through SimpleChannel
        Broker clientBroker = new SimpleBroker("ClientBroker");
       // Broker serverBroker = new SimpleBroker("ServerBroker");

        SimpleChannel channel = new SimpleChannel(8080, clientBroker);
        byte[] message = "Hello".getBytes();
        int bytesWritten = 0;
        try {
            bytesWritten = channel.write(message, 0, message.length);
        } catch (DisconnectedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        assert bytesWritten == message.length : "Bytes written should match the message length";

        byte[] buffer = new byte[5];
        int bytesRead = 0;
        try {
            bytesRead = channel.read(buffer, 0, buffer.length);
        } catch (DisconnectedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println("Received message: " + new String(buffer));
        System.out.println("Number of bytes recieved :"+ bytesRead); 
        assert new String(buffer).equals("Hello") : "Received message should be 'Hello'";
        assert bytesRead == 5 : "Bytes read should be 5";
    }
}
