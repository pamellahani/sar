package channels.tests;

import channels.Broker;
import channels.Channel;
import channels.SimpleBroker;
import channels.SimpleTask;
import channels.Task;


public class EchoServerTest {
    
    private Broker serverBroker;
    private Task serverTask;

    public EchoServerTest(int port) {

        // Create a new server broker and accept a connection on the specified port
        this.serverBroker = new SimpleBroker("EchoServer");
        Channel serverChannel = this.serverBroker.accept(port);

        // Define the server task 
        this.serverTask = new SimpleTask(serverBroker, () -> {
            byte[] data = new byte[256];
            while  (!serverChannel.disconnected()) {
                int bytesRead = serverChannel.read(data, 0, data.length);
                if (bytesRead > 0) {
                    //Write the data back to the client
                    serverChannel.write(data, 0, bytesRead);
                }
            }
        });
    }

    public void startServer() {
        this.serverTask.start();
    }
}
