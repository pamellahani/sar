### Design Documentation for Message Queue System with Mixed Event-Based and Threaded Tasks

---

#### **Overview:**

This design outlines a **Message Queue Communication System** that merges two primary execution models:
1. **Event-Based:** Event-driven mechanisms rely on an **Event Pump** for managing events, such as connections and message reception.
2. **Thread-Based:** Thread-based execution utilizes parallel threads to handle blocking operations, primarily within message handling and communication channels.

The key feature of this system is the interaction between **event-based tasks** and **thread-oriented tasks**, combining the flexibility of event-based systems with the performance benefits of threads in blocking operations. Central to the system is the **MessageQueue** that interfaces both execution worlds.

#### **Core Components:**

1. **QueueBroker**:
   - Manages the lifecycle of connections between clients (Requestor) and servers (Receiver).
   - Provides event-driven listeners for:
     - **AcceptListener interface:** Handles accepted connections.
     - **ConnectListener interface:** Manages client-side connections.
   - **Binding** (Server-side): Servers bind to a port to accept connections.
   - **Connection** (Client-side): Clients initiate connections to a specific port.
 
        Operations in this object are primarily non-blocking, driven by event notifications when a connection is accepted or established.

2. **MessageQueue**:
   - Acts as the core communication channel between Requestor and Receiver tasks, encapsulating message transmission and reception.
   - Supports two major operations:
     - **send(byte[] bytes):** Sends data over the channel.
     - **receive(byte[] msg):** Receives data from the channel.
   - This class is Event-based through **Listener interfcace**: The system leverages event-based callbacks for message arrival and channel closure events.
   - Operations (non-blocking): 
     - **send** pushes messages directly into the **Message Queue**.
     - **receive** operates in a non-blocking manner, due to its association with the event loop.
   
3. **Channel and Circular Buffer**: See [Link To Channel Spec](../L1-ChannelSpecification.md)


5. **Event Pump**:
   - Central to the **Event-Based World**, the Event Pump processes event notifications such as connections, disconnections, and message arrivals.
   - Non-blocking: The event pump loops over an **Event Queue** (FIFO), continually processing new events.
   - Used to dispatch events to **QueueBroker**, **MessageQueue**, and **Listeners**.

6. **Event Queue**:
   - A FIFO queue that the **Event Pump** uses to keep track of pending events.
   - Non-blocking in event-driven processing: It ensures that events are processed as they are dispatched without any delay, maintaining the flow of the application.

7. **BrokerManager**:
   - Responsible for managing all brokers in a given session.
   - Provides a **connect()** and **accept()** mechanism to establish or accept connections between clients and servers.

8. **Listeners**:
   - Interfaces that define how the system reacts to specific events, such as when a message is received or a connection is closed.
   - Listeners are always triggered by the **Event Pump** and handle the incoming events without blocking.

#### **Ownership Transfer and Aliasing in the Send Method:**

### **4. Ownership Transfer and Aliasing:**
When the **send()** method is invoked, **ownership transfer** happens through **aliasing**. This allows the system to pass a reference to the message (byte array) instead of creating a deep copy, ensuring efficient memory usage. Once the **Event Pump** handles the event, the message is moved between the **Requestor** and **Receiver** tasks through the **Channel** and **Circular Buffer**.

---

#### **Blocking vs Non-Blocking Interactions:**

1. **Blocking Operations:**
   - **Channel’s `write()` and `read()` methods** may block if there is insufficient buffer space or data.
   - **Circular Buffer** interactions can also block when waiting for space to free up (for write operations) or data to be available (for read operations).

2. **Non-Blocking Operations:**
   - **Event-driven listeners**: These operate asynchronously and do not block the main flow of the application.
   - **QueueBroker’s `connect()` and `bind()` methods**: Connection establishment and binding are non-blocking, with the listeners handling success or failure asynchronously.
   - **MessageQueue’s `send()`** method: Non-blocking as it queues messages for dispatch.

#### **Execution Flow and Interactions:**

- **Requestor (Client Task)**:
  1. **connect()**: Requestor connects to the server via **QueueBroker**.
  2. **Message Sending**: Once connected, the client can **send()** data through **MessageQueue**.
  3. **Non-blocking Reads**: When data is ready, the **Listener** on the client side gets triggered to handle the incoming message.

- **Receiver (Server Task)**:
  1. **bind()**: The server binds to a port and waits for connections.
  2. **Message Reception**: Once a connection is accepted, the server listens for messages via **MessageQueue**.
  3. **Blocking Writes**: When receiving data, the server might push responses back to the client, potentially blocking if the **Circular Buffer** is full.


## What is an Event Pump?

In summary, the Event Pump and Event Loop ensure that all events are handled efficiently and in a non-blocking manner, allowing the system to remain responsive while processing events asynchronously. The Event Queue serves as a buffer, and the Event Loop continuously processes each event, ensuring smooth communication between the Requestor and Receiver tasks.
