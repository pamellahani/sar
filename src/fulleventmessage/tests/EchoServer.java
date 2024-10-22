package fulleventmessage.tests;


import fulleventmessage.EventQueueBroker;


public class EchoServer implements Runnable {

    private EventQueueBroker eventBroker;

    public EchoServer(EventQueueBroker serverBroker) {
        this.eventBroker = serverBroker;
    }

    @Override
    public void run() {
        eventBroker.bind(8080, new EchoAcceptListener());
    }
}
