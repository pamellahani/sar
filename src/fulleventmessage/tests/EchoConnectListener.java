package fulleventmessage.tests;

import fulleventchannel.Broker.ConnectListener;
import fulleventchannel.Channel;
import fulleventchannel.EventChannel;
import fulleventmessage.MessageQueue;
import fulleventmessage.tests.message_listeners.EchoClientMessageListener;
import fulleventmessage.EventMessageQueue;
import hybrid.Message;

public class EchoConnectListener implements ConnectListener {

    private EchoClientMessageListener messageListener;
    private Message message;

    public EchoConnectListener(EchoClientMessageListener listener, Message msg) {
        this.messageListener = listener;
        this.message = msg;
    }

    @Override
    public void connected(Channel channel) {
        System.out.println("Client connected to server.");

        EventChannel channel1 = (EventChannel) channel;
        MessageQueue mq = new EventMessageQueue(channel1);
        mq.setMessageListener(messageListener);
        mq.send(message);
    }

    @Override
    public void refused() {
        System.out.println("Connection refused.");
    }
}
