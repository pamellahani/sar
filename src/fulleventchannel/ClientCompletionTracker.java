package fulleventchannel;

public class ClientCompletionTracker {

    private static int completedClients = 0;
    private static int totalClients = 0;

    // Method to set the total number of clients
    public static void setTotalClients(int total) {
        totalClients = total;
        System.out.println("Total clients set to: " + totalClients);
    }

    // Called by the completion event task when a client finishes
    public static void clientFinished() {
        completedClients++;
        System.out.println("Client finished, total completed: " + completedClients);

        // Check if all clients are done
        if (completedClients >= totalClients) {
            // Post the ShutdownEvent
            System.out.println("All clients finished, posting ShutdownEvent.");
            EventPump.getInstance().post(new ShutdownEvent());
        }
    }
}

