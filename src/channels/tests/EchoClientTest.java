package channels.tests;

import channels.Task;

import channels.Broker;
import channels.BrokerManager;
import channels.Channel;
import channels.DisconnectedException;
import channels.SimpleBroker;
import channels.SimpleTask;

public class EchoClientTest {
    
    private Broker clientBroker; 
    private Task clientTask;

    private String echoedMessage;
    private String originalMessage;
    private BrokerManager brokerManager;

    public EchoClientTest(String message, int port) {
        this.originalMessage = message;
        this.brokerManager = new BrokerManager();
        // Create a new client broker 
        this.clientBroker = new SimpleBroker("EchoClient", brokerManager);
        brokerManager.registerBroker(clientBroker);
       
        // Define the client task
        this.clientTask = new SimpleTask(clientBroker, () -> {
            Channel clientChannel = clientBroker.connect("EchoServer", port);
            byte[] data = message.getBytes();
            try {
                clientChannel.write(data, 0, data.length);
            } catch (DisconnectedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            // Client waits for the response from the server and reads the returned data
            byte[] response = new byte[256];
            int bytesRead = 0;
            try {
                bytesRead = clientChannel.read(response, 0, response.length);
            } catch (DisconnectedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (bytesRead > 0) {
                echoedMessage = new String(response, 0, bytesRead);
            }
            clientChannel.disconnect();
            this.brokerManager.deregisterBroker(clientBroker);;
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
