package fullevent.tests.channel_listeners;

import channels.DisconnectedException;
import fullevent.EventChannel;

public class EchoServerChannelListener implements EventChannel.ChannelListener {

    private final EventChannel channel;

    public EchoServerChannelListener(EventChannel channel) {
        this.channel = channel;
        this.channel.setChannelListener(this);
    }

    @Override
    public void onBufferFull(EventChannel channel) {
        System.out.println("Server buffer is full, cannot accept more data at the moment.");
    }

    @Override
    public void onBufferNotFull(EventChannel channel) {
        System.out.println("Server buffer has space, can resume accepting data.");
    }

    @Override
    public void onBufferEmpty(EventChannel channel) {
        System.out.println("Server buffer is empty, waiting for client to send data.");
    }

    @Override
    public void onBufferNotEmpty(EventChannel channel) {
        byte[] buffer = new byte[256];
        int bytesRead = -1;
        try {
            bytesRead = channel.read(buffer, 0, buffer.length);
        } catch (DisconnectedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (bytesRead > 0) {
            String message = new String(buffer, 0, bytesRead);
            System.out.println("Server received message: " + message);
            // Echo the message back to the client
            try {
                channel.write(buffer, 0, bytesRead);
            } catch (DisconnectedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            System.out.println("Server echoed message back to client.");
        }
    }

}
