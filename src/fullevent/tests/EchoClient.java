package fullevent.tests;

import fullevent.Broker;
import fullevent.EventPump;
import fullevent.ShutdownEvent;

public class EchoClient implements Runnable {

    private static int completedClients = 0;
    private static final int TOTAL_CLIENTS = 3;
    
    private Broker broker;
    
    public EchoClient(Broker br) {
        broker = br;
    }

    @Override
    public void run() {
        broker.connect("serverBroker", 8080, new EchoConnectListener());
        
        // Simulate some task completion, then mark the client as done
        markClientFinished();
    }

    private synchronized void markClientFinished() {
        completedClients++;
        if (completedClients >= TOTAL_CLIENTS) {
            // Post ShutdownEvent once all clients have finished
            EventPump.getInstance().post(new ShutdownEvent());
        }
    }
}
