package fulleventmessage.tests;

import fulleventchannel.Broker.AcceptListener;
import fulleventchannel.Channel;
import fulleventchannel.EventChannel;
import fulleventmessage.MessageQueue;
import fulleventmessage.tests.message_listeners.EchoServerMessageListener;
import fulleventmessage.EventMessageQueue;

public class EchoAcceptListener implements AcceptListener {

    @Override
    public void accepted(Channel channel) {
        System.out.println("Server accepted client connection.");

        MessageQueue mq = new EventMessageQueue((EventChannel) channel);
        mq.setMessageListener(new EchoServerMessageListener());
    }
}
