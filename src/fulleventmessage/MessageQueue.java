package fulleventmessage;

import hybrid.Message;

public abstract class MessageQueue {

    public abstract void setMessageListener(MessageListener listener);

    public abstract void close();

    public abstract boolean closed();

	public abstract boolean send(Message message);

    public interface MessageListener {
        public void sent(Message message);
        public void received(byte[] bytes);
        public void closed();
    }
}
