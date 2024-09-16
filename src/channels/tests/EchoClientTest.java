package channels.tests;

import channels.Task;

import channels.Broker;
import channels.Channel;
import channels.SimpleBroker;
import channels.SimpleTask;

public class EchoClientTest {
    
    private Broker clientBroker; 
    private Task clientTask;

    private String echoedMessage;
    private String originalMessage;

    public EchoClientTest(String message, int port) {
        this.originalMessage = message;
        // Create a new client broker 
        this.clientBroker = new SimpleBroker("EchoClient");
       
        // Define the client task
        this.clientTask = new SimpleTask(clientBroker, () -> {
            Channel clientChannel = clientBroker.connect("EchoServer", port);
            byte[] data = message.getBytes();
            clientChannel.write(data, 0, data.length);

            // Client waits for the response from the server and reads the returned data
            byte[] response = new byte[256];
            int bytesRead = clientChannel.read(response, 0, response.length);
            if (bytesRead > 0) {
                echoedMessage = new String(response, 0, bytesRead);
            }
            clientChannel.disconnect();
        });
    }

    //Start the client task
    public void startClient() {
        this.clientTask.start();
    }

    //NOT YET USED IN THE TEST CLASS - MAY BE USED IN THE FUTURE
    //check if the echoed message is the same as the original message
    public boolean checkEcho() {
        return originalMessage.equals(echoedMessage);
    }

}