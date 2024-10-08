### Brief Explanation of Event Loop and Event Pump in the System

The **Event Pump** is the core of the **Event-Based World** in the system. It continuously checks the **Event Queue** for new events and dispatches them to the appropriate handlers (such as the **Channel** or **MessageQueue**) via an **Event Loop**. The **Event Loop** ensures that events are processed sequentially but efficiently, without blocking the system.

- **Non-blocking interactions** (e.g., send operations) are handled by the **Event Pump**, keeping the system responsive.
- **Blocking interactions** occur in the threaded world, mainly during read/write operations with the **Circular Buffer**.

The **Event Pump**'s role is to manage the processing of events by continually checking the **Event Queue** for new actions. It ensures that each event is dispatched to the correct handler, allowing the system to react in real time to various conditions, such as new connections, incoming messages, or disconnections.


#### **Event Queue Thread:**
The **Event Queue Thread** is a dedicated thread that handles the processing of events in the **Event Pump**. It manages the **Event Queue**, ensuring that events are dequeued and processed one at a time, enabling non-blocking execution of event-driven components. This thread allows the system to remain responsive, as it offloads event management to a separate, continuous process.