package events;

public class Task {

    private boolean killed = false;

    /**
     * Post a runnable task. 
     * @param r
     */
    void post(Runnable r){
        r.run();
    }

    /**
     * Create a new Task.
     * @return a new Task instance.
     */
    static Task task(){
        return new Task();
    }

    /**
     * Kill the task.
     */
    void kill(){
        killed = true;
    }

    /**
     * Check if the task is killed.
     * @return true if the task is killed, false otherwise.
     */
    boolean killed(){
        return killed;
    }
    
}
