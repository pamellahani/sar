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
        messageQueue.send(message);
    }

    @Override
    public void refused() {
        System.out.println("Server: Connection refused by the server.");
    }

    // Method to generate a random string of alphabetic characters
    private String generateRandomMessage() {
        int length = 10; // Length of the random message
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder messageBuilder = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            messageBuilder.append(characters.charAt(random.nextInt(characters.length())));
        }

        return messageBuilder.toString();
    }
}

