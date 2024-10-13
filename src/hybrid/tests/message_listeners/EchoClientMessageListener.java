package hybrid.tests.message_listeners;

import hybrid.EventPump;
import hybrid.Message;
import hybrid.MessageQueue;
import hybrid.MessageQueue.Listener;

public class EchoClientMessageListener implements Listener{

	private MessageQueue private_queue;
	private Message private_message;
	private static int cpt = 0;


	public EchoClientMessageListener(MessageQueue queue, Message message) {
		private_queue = queue;
		private_message = message;
	}


    @Override
    public void received(byte[] msg) {
        //Tests
		for(int i = 0; i < private_message.getLength(); i++){
			assert(msg[i] == private_message.getByteAt(i)) : "Data recieved different from the one sent : " + i;
		}	

		assert(private_queue != null) : "Client Queue not initialized";
		assert(private_queue.closed() == true) : "Client Queue not disconnected";
		
		System.out.println("Client passed");
		
		//if(cpt++ >= 2) {
			EventPump.getInstance().stopPump();
		//}
    }

    @Override
    public void sent(Message msg) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'sent'");
    }

    @Override
    public void closed() {
        private_queue.close();
    }
    
}
