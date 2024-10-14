package hybrid;

public class Sender extends Thread {
    private final MixedMessageQueue messageQueue;
    private boolean isRunning;

    public Sender(MixedMessageQueue queue) {
        this.messageQueue = queue;
        this.isRunning = true;
    }

    @Override
    public void run() {
        while (isRunning) {
            synchronized (this) {
                while (messageQueue.getMessagesQueue().isEmpty() && !messageQueue.isClosed()) {
                    try {
                        this.wait(); 
                    } catch (InterruptedException e) {
                        System.out.println("Sender thread interrupted, stopping.");
                        return;
                    }
                }
            }
    
            // If the queue is closed, exit the sender thread
            if (messageQueue.isClosed()) {
                System.out.println("Message queue closed, stopping sender thread.");
                return;
            }
    
            Message msg = messageQueue.getMessagesQueue().poll();
            if (msg != null) {
                messageQueue.sub_send(msg);
            }
        }
    }


    public void notifySender() {
        synchronized (this) {
            this.notify();
        }
    }

    public void interrupt() {
        isRunning = false;
        this.interrupt();
    }
}
