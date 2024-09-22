package channels;

public class Main {

    public static void main(String[] args) {

        BrokerManager manager = new BrokerManager();
        Broker broker1 = new SimpleBroker("broker1", manager);
        Broker broker2 = new SimpleBroker("broker2", manager);

        Channel channel1 = broker1.accept(8080);
        Channel channel2 = broker2.connect("broker1", 8080);

        byte[] message = "Hello, World!".getBytes();
        channel2.write(message, 0, message.length);

        byte[] buffer = new byte[1024];
        int bytesRead = channel1.read(buffer, 0, buffer.length);
        String received = new String(buffer, 0, bytesRead);
        System.out.println(received);

        channel1.disconnect();
        channel2.disconnect();
    }


}
    

