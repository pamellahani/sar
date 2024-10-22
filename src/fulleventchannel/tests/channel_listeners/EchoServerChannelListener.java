package fulleventchannel.tests.channel_listeners;

import channels.DisconnectedException;
import fulleventchannel.Channel;
import fulleventchannel.EventTask;
import fulleventchannel.Channel.ChannelListener;

public class EchoServerChannelListener implements ChannelListener {
	
	private static int counter = 0;
	private int writerCounter = 0;
	private Channel echoChannel;
	
	public EchoServerChannelListener(Channel channel) {
		echoChannel = channel;
	}

	@Override
	public void read(byte[] bytes) {
		System.out.println("Server received message: " + new String(bytes));

		new EventTask().post(() -> {
			try {
				System.out.println("Server writing message back: " + new String(bytes));
				echoChannel.write(bytes);
			} catch (DisconnectedException e) {
				e.printStackTrace();
			}
		});
	}

	@Override
	public void disconnected() {
		assert (echoChannel.disconnected() == true) : "Channel is still connected";
		if (counter++ >= 2) {
			System.out.println("Server passed.");
		}
	}

	@Override
	public void wrote(byte[] bytes) {
		if (writerCounter++ >= 2) {
			echoChannel.disconnect();
		}
	}
}
