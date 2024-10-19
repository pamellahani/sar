package fullevent.tests.channel_listeners;

import java.nio.charset.StandardCharsets;

import channels.DisconnectedException;
import fullevent.EventChannel;

public class EchoClientChannelListener implements EventChannel.ChannelListener {


    private final EventChannel channel;
    private final String messageToSend;

    public EchoClientChannelListener(EventChannel channel, String messageToSend) throws DisconnectedException {
        this.channel = channel;
        this.messageToSend  = messageToSend;
        this.channel.setChannelListener(this);
        sendInitialMessage();
    }

    private void sendInitialMessage() throws DisconnectedException {
        byte[] messageBytes = messageToSend.getBytes(StandardCharsets.UTF_8);
        channel.write(messageBytes, 0, messageBytes.length);
        System.out.println("Client sent message: " + messageToSend);
    }

    @Override
    public void onBufferFull(EventChannel channel) {
        System.out.println("Client buffer is full, waiting to send more data.");
    }

    @Override
    public void onBufferNotFull(EventChannel channel) {
        System.out.println("Client buffer has space, can continue writing.");
    }

    @Override
    public void onBufferEmpty(EventChannel channel) {
        System.out.println("Client buffer is empty, waiting for new data.");
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
            String receivedMessage = new String(buffer, 0, bytesRead, StandardCharsets.UTF_8);
            System.out.println("Client received message: " + receivedMessage);
        }
    }
    
    
}
