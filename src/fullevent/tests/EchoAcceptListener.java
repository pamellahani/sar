package fullevent.tests;

import channels.DisconnectedException;
import fullevent.Broker.AcceptListener;
import fullevent.Channel;
import fullevent.EventTask;
import fullevent.tests.channel_listeners.EchoServerChannelListener;



public class EchoAcceptListener implements AcceptListener{

	@Override
	public void accepted(Channel channel) {
		
		channel.setChannelListener(new EchoServerChannelListener(channel));
		
		byte[] bytes1 = new byte[360];
		byte[] bytes2 = new byte[360];
		byte[] bytes3 = new byte[360];


		
		EventTask task = new EventTask();
		task.post(() -> {
			try {
				channel.read(bytes1);
			} catch (DisconnectedException e) {
				e.printStackTrace();
			}
		});
		task.post(() -> {
			try {
				channel.read(bytes2);
			} catch (DisconnectedException e) {
				e.printStackTrace();
			}
		});
		task.post(() -> {
			try {
				channel.read(bytes3);
			} catch (DisconnectedException e) {
				e.printStackTrace();
			}
		});

	}

}
	
