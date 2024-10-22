package fullevent.tests.channel_listeners;

import channels.DisconnectedException;
import fullevent.Channel;
import fullevent.Channel.ChannelListener;
import fullevent.EventTask;

public class EchoClientChannelListener implements ChannelListener {
	
	private byte[] bytesClient1;
	private byte[] bytesClient2;
	private byte[] bytesClient3;

	private Channel echoChannel;
	private int cpt = 0;
	
	public EchoClientChannelListener(byte[] bytes1, byte[] bytes2, byte[] bytes3, Channel channel) {
		bytesClient1 = bytes1;
		bytesClient2 = bytes2;
		bytesClient3 = bytes3;
		echoChannel = channel;
	}

	@Override
	public void read(byte[] bytes) {
		System.out.println("Client received message: " + new String(bytes));

		if (bytes[0] == 1) {
			for (int i = 0; i < bytesClient1.length; i++) {
				if (bytesClient1[i] != bytes[i]) {
					System.err.println("Data received different from the one sent at index: " + i);
				}
			}
		} else if (bytes[0] == 2) {
			for (int i = 0; i < bytesClient2.length; i++) {
				if (bytesClient2[i] != bytes[i]) {
					System.err.println("Data received different from the one sent at index: " + i);
				}
			}
		} else if (bytes[0] == 3) {
			for (int i = 0; i < bytesClient3.length; i++) {
				if (bytesClient3[i] != bytes[i]) {
					System.err.println("Data received different from the one sent at index: " + i);
				}
			}
		}

		if (cpt++ >= 2) {
			echoChannel.disconnect();
		}
	}

	@Override
	public void disconnected() {
		assert (echoChannel.disconnected() == true) : "Channel is not disconnected";
		System.out.println("Client disconnected.");
	}

	@Override
	public void wrote(byte[] bytes) {
		System.out.println("Client wrote message: " + new String(bytes));
		byte[] answer = new byte[bytes.length];
		new EventTask().post(() -> {
			try {
				echoChannel.read(answer);
			} catch (DisconnectedException e) {
				// e.printStackTrace();
			}
		});
	}
}
