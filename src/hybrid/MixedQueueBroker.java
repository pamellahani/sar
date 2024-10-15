package hybrid;

import java.util.HashMap;
import java.util.Map;

import channels.*;

public class MixedQueueBroker extends QueueBroker {

    private SimpleBroker br;

    // this map contains the port number associated to the thread that is listening
    private Map<Integer, Thread> bindTh;

    public MixedQueueBroker(String name) {
		super(name);
		br = new SimpleBroker(name);
		bindTh = new HashMap<Integer, Thread>();
	}

    public boolean unbind(int port) {
        if (bindTh.containsKey(port)) {
            Thread t = bindTh.get(port);
            t.interrupt();
            bindTh.remove(port);
            return true;
        }
        return false;
    }

    public boolean bind(int port, AcceptListener listener) {
        if (bindTh.containsKey(port)) {
            return false;
        }

        Thread thread = new Thread();

        thread = new Thread(new Runnable() {

            @Override
            public void run() {
                do {

                    SimpleChannel channelAccept = (SimpleChannel) br.accept(port);
                    if (channelAccept != null) {
                        MessageQueue mq = new MixedMessageQueue(channelAccept);
                        EventTask task = new EventTask();
                        task.post(() -> listener.accepted(mq));
                    }

                } while (bindTh.containsKey(port));

            }
        });
        bindTh.put(port, thread);
        thread.start();
        return true;
    }

    public boolean connect(String name, int port, ConnectListener listener) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                SimpleChannel channelConnect = (SimpleChannel) br.connect(name, port);

                EventTask task = new EventTask();
                if (channelConnect == null) {
                    task.post(() -> listener.refused());
                } else {
                    MessageQueue mq = new MixedMessageQueue(channelConnect);
                    task.post(() -> listener.connected(mq));
                }

            }
        }).start();
        return true;
    }

    public String name() {
        return br.getName();
    }

    @Override
    public Broker getBroker() {
        return br;
    }

}
