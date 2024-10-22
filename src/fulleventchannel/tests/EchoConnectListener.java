package fulleventchannel.tests;

import java.nio.charset.StandardCharsets;
import java.util.Random;

import channels.DisconnectedException;
import fulleventchannel.*;
import fulleventchannel.Broker.ConnectListener;
import fulleventchannel.tests.channel_listeners.EchoClientChannelListener;

public class EchoConnectListener implements ConnectListener {

    private static final Random random = new Random();
    private static final int MIN_HEIGHT = 2;   // Minimum message length
    private static final int MAX_HEIGHT = 50;  // Maximum message length

    @Override
    public void connected(Channel channel) {

        // Generate random messages
        byte[] bytes1 = generateRandomMessage();
        byte[] bytes2 = generateRandomMessage();
        byte[] bytes3 = generateRandomMessage();

        // Set the channel listener
        channel.setChannelListener(new EchoClientChannelListener(bytes1, bytes2, bytes3, channel));

        // Array of byte arrays to choose from
        byte[][] messages = { bytes1, bytes2, bytes3 };

        // Post a task to randomly write one of the byte arrays
        new EventTask().post(() -> {
            try {
                // Randomly select a message
                byte[] randomMessage = messages[random.nextInt(messages.length)];
                System.out.println("Client sending message: " + new String(randomMessage, StandardCharsets.UTF_8));
                channel.write(randomMessage);
            } catch (DisconnectedException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void refused() {
        throw new IllegalStateException("Connection refused");
    }

    // Method to generate a random message with random height
    private byte[] generateRandomMessage() {
        // Generate a random height (length of the message)
        int height = MIN_HEIGHT + random.nextInt(MAX_HEIGHT - MIN_HEIGHT + 1);

        // Generate a random string of the specified length
        StringBuilder messageBuilder = new StringBuilder();
        for (int i = 0; i < height; i++) {
            // Generate a random character (ASCII range 33 to 126 for printable characters)
            char randomChar = (char) (33 + random.nextInt(94));
            messageBuilder.append(randomChar);
        }

        // Convert the message to bytes
        return messageBuilder.toString().getBytes(StandardCharsets.UTF_8);
    }
}
