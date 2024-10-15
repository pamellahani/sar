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
                        this.wait();  // Wait for new messages to be added or for queue to close
                    } catch (InterruptedException e) {
                        System.out.println("Sender thread interrupted, stopping.");
                        return;
                    }
                }
            }
    
            if (messageQueue.isClosed() || !isRunning) {
                System.out.println("Message queue closed or sender is stopped, stopping sender thread.");
                return;
            }
    
            Message msg = messageQueue.getMessagesQueue().poll();
            if (msg != null) {
                messageQueue.sub_send(msg);
            }
        }
        System.out.println("Sender thread stopping as all tasks are done or queue is closed.");
    }
    
    public void stopSender() {
        isRunning = false;
        synchronized (this) {
            this.notifyAll();  // Notify in case it's waiting
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
