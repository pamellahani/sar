package fulleventchannel;

/**
 * Event to trigger the shutdown of the EventPump and the end of the program
 */
public class ShutdownEvent extends Event {

    public ShutdownEvent() {
        super(null, null, null);
        System.out.println("ShutdownEvent created");
    }

    @Override
    public void run() {
        System.out.println("ShutdownEvent triggered. Stopping EventPump.");
        // No specific action needed, the existence of this event triggers the pump to stop
    }
}
