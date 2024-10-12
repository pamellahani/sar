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
        while (isRunning && !messageQueue.isClosed()) {
            synchronized (this) {
                while (messageQueue.getMessagesQueue().isEmpty() && !messageQueue.closed()) {
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        return; // Channel is now closed
                    }
                }
            }
            if (messageQueue.isClosed()) {
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
