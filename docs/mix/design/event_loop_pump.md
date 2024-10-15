## Brief Explanation of Event Loop and Event Pump in the System

The Event Pump serves as the core of the Event-Based World in the system. It continuously checks the Event Queue for new events and dispatches them to appropriate handlers (such as the Channel or MixedMessageQueue) via an Event Loop. The Event Loop ensures that events are processed sequentially and efficiently, without causing the system to block.

**Non-blocking interactions**(e.g., send operations) are handled by the Event Pump. It uses an event-driven approach to ensure the system remains responsive by dispatching events without waiting for completion of other operations.

**Blocking interactions** occur in the threaded world, particularly during read/write operations with the Circular Buffer or when managing client-server communication through channels. These operations are handled by separate threads to prevent them from blocking the Event Pump.

The **Event Pump's role** is to manage the processing of events by continuously monitoring the Event Queue for new actions. It ensures that each event is dispatched to the correct handler, allowing the system to react in real-time to various situations, such as new connections, incoming messages, or disconnections.
Event Pump Thread:

### Why use a single thread for the Event Pump?
The Event Pump Thread is the dedicated thread that runs the Event Loop and handles the processing of events in the Event Pump. It manages the Event Queue, ensuring that events are dequeued and processed one at a time, enabling the non-blocking execution of event-driven components. This thread allows the system to remain responsive by offloading event management to a continuous, independent process.