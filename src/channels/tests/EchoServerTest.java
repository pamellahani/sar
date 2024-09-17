package channels.tests;

import channels.Broker;
import channels.Channel;
import channels.SimpleBroker;
import channels.SimpleTask;
import channels.Task;
import channels.CircularBuffer;

public class EchoServerTest {
    
    private Broker serverBroker;
    private Task serverTask;
    private CircularBuffer buffer;

    /**
     * 
     * Constructor for the EchoServerTest class.
     * 
     * @param port The port number to listen for client connections.
     */

    public EchoServerTest(int port) {

        this.buffer = new CircularBuffer(256);

        // Create a new server broker and accept a connection on the specified port
        this.serverBroker = new SimpleBroker("EchoServer");
        Channel serverChannel = this.serverBroker.accept(port);

        // Define the server task 
        this.serverTask = new SimpleTask(serverBroker, () -> {
            byte[] data = new byte[256];
            while  (!serverChannel.disconnected()) {
                int bytesRead = serverChannel.read(data, 0, data.length);
                if (bytesRead > 0) {

                    //Push data into the buffer
                    for (int i = 0; i < bytesRead; i++) {
                        buffer.push(data[i]);
                    }

                    //Pull data from the buffer to echo back to the client
                    byte echoedData[] = new byte[bytesRead];
                    for (int i = 0; i < bytesRead; i++) {
                        echoedData[i] = buffer.pull();
                    }

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
