package hybrid.tests;

import hybrid.Message;
import hybrid.MessageQueue;
import hybrid.QueueBroker;
import hybrid.tests.message_listeners.EchoClientMessageListener;

import java.nio.charset.StandardCharsets;
import java.util.Random;

public class EchoConnectListener implements QueueBroker.ConnectListener {

    @Override
    public void connected(MessageQueue messageQueue) {
        // Generate a random message instead of a static "Hello Server" message
        String randomMessage = generateRandomMessage();
        Message message = new Message(randomMessage.getBytes(StandardCharsets.UTF_8), 0, randomMessage.length());

        // Set the listener and send the random message
        messageQueue.setListener(new EchoClientMessageListener(messageQueue, message));
        System.out.println("Connected to the server");
        messageQueue.send(message);
        System.out.println("Message was sent");
    }

    @Override
    public void refused() {
        System.out.println("Connection refused by the server.");
    }

    // Method to generate a random string of alphabetic characters
    private String generateRandomMessage() {
        Random random = new Random();
        int length = random.nextInt(4991) + 10;  // Random length between 10 and 5000
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder messageBuilder = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            messageBuilder.append(characters.charAt(random.nextInt(characters.length())));
        }

        return messageBuilder.toString();
    }
}

