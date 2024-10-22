package fulleventchannel.tests;

import channels.DisconnectedException;
import fulleventchannel.Channel;
import fulleventchannel.EventTask;
import fulleventchannel.Broker.AcceptListener;
import fulleventchannel.tests.channel_listeners.EchoServerChannelListener;

public class EchoAcceptListener implements AcceptListener {

    @Override
    public void accepted(Channel channel) {
        channel.setChannelListener(new EchoServerChannelListener(channel));
        
        byte[][] byteArrays = { new byte[1024], new byte[1024], new byte[1024] };
        
        EventTask task = new EventTask();
        
        // Post read operations for each byte array
        for (byte[] byteArray : byteArrays) {
            task.post(() -> readFromChannel(channel, byteArray));
        }
    }

    // Helper method to read from the channel and handle exceptions
    private void readFromChannel(Channel channel, byte[] byteArray) {
        try {
            channel.read(byteArray);
            System.out.println("Server writing message back: " + new String(byteArray));
        } catch (DisconnectedException e) {
            e.printStackTrace();
        }
    }
}
