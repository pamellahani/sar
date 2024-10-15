### Design Documentation for Message Queue System with Mixed Event-Based and Threaded Tasks

---

#### **Overview:**

This design outlines a **Message Queue Communication System** that merges two primary execution models:
1. **Event-Based:** Event-driven mechanisms rely on an **Event Pump** for managing events, such as connections and message reception.
2. **Thread-Based:** Thread-based execution utilizes parallel threads to handle blocking operations, primarily within message handling and communication channels.

The key feature of this system is the interaction between **event-based tasks** and **thread-oriented tasks**, combining the flexibility of event-based systems with the performance benefits of threads in blocking operations. Central to the system is the **MessageQueue** that interfaces both execution worlds.

#### **Core Components:**

1. **MixedQueueBroker**:
   - Manages the lifecycle of connections between clients (Requestor) and servers (Receiver).
   - Provides event-driven listeners for:
     - **AcceptListener interface:** Handles accepted connections.
     - **ConnectListener interface:** Manages client-side connections.
   - **Binding** (Server-side): Servers bind to a port to accept connections.
   - **Connection** (Client-side): Clients initiate connections to a specific port.
   - Operations in this object are primarily non-blocking, driven by event notifications when a connection is accepted or established.

2. **MixedMessageQueue**:
   - Acts as the core communication channel between Requestor and Receiver tasks, encapsulating message transmission and reception.
   - Supports two major operations:
     - **send(Message msg):** Sends a message over the channel using a `Sender` thread.
     - **sub_send(Message msg):** Handles message sending by breaking it down into sending the message size and content.
   - Uses the **Listener interface** for event-based callbacks such as message reception and channel closure events.
   - Operations (Thread-based and Non-blocking):
     - **send** pushes messages into the **Message Queue** for asynchronous processing by a sender thread.
     - **sub_send** is called by the sender thread to handle the actual message transmission to the channel.

3. **Channel and Circular Buffer**: See [Link To Channel Spec](../L1-ChannelSpecification.md)

4. **Sender Thread**:
   - Handles message transmission in the **MixedMessageQueue**.
   - Operates on messages in a separate thread to avoid blocking the main event flow.
   - Ensures that messages are sent correctly by calling `sub_send()` on the **MixedMessageQueue**.

5. **Event Pump**:
   - Central to the **Event-Based World**, the Event Pump processes event notifications such as connections, disconnections, and message arrivals.
   - Non-blocking: The event pump loops over an **Event Queue** (FIFO), continually processing new events.
   - Used to dispatch events to **QueueBroker**, **MixedMessageQueue**, and **Listeners**.

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

### **Ownership Transfer and Aliasing**
When the **send()** method is invoked, **ownership transfer** happens through **aliasing**. This allows the system to pass a reference to the message (byte array) instead of creating a deep copy, ensuring efficient memory usage. Once the **Event Pump** handles the event, the message is moved between the **Requestor** and **Receiver** tasks through the **Channel** and **Circular Buffer**.

---

#### **Blocking vs Non-Blocking Interactions:**

1. **Blocking Operations:**
   - **Channel’s `write()` and `read()` methods** may block if there is insufficient buffer space or data.
   - **Circular Buffer** interactions can also block when waiting for space to free up (for write operations) or data to be available (for read operations).

2. **Non-Blocking Operations:**
   - **Event-driven listeners**: These operate asynchronously and do not block the main flow of the application.
   - **QueueBroker’s `connect()` and `bind()` methods**: Connection establishment and binding are non-blocking, with the listeners handling success or failure asynchronously.
   - **MixedMessageQueue’s `send()`** method: Non-blocking as it queues messages for dispatch by a separate sender thread.

#### **Execution Flow and Interactions:**

- **Requestor (Client Task)**:
  1. **connect()**: Requestor connects to the server via **QueueBroker**.
  2. **Message Sending**: Once connected, the client can **send()** data through **MixedMessageQueue**.
  3. **Non-blocking Reads**: When data is ready, the **Listener** on the client side gets triggered to handle the incoming message.

- **Receiver (Server Task)**:
  1. **bind()**: The server binds to a port and waits for connections.
  2. **Message Reception**: Once a connection is accepted, the server listens for messages via **MixedMessageQueue**.
  3. **Blocking Writes**: When receiving data, the server might push responses back to the client, potentially blocking if the **Circular Buffer** is full.

#### **Detailed Flow:**

- **Client Connects**:
  - Client invokes `connect()` on QueueBroker.
  - **ConnectListener** (linked to Channel) triggers `connected()`, notifying the client that the connection is successful.

- **Server Accepts**:
  - Server is bound to a port using `bind()` on QueueBroker.
  - **AcceptListener** triggers `accepted()`, indicating that the server has accepted a connection and a **MixedMessageQueue** is ready.

- **Data Exchange**:
  - Client sends data using `send()` on the **MixedMessageQueue**.
  - Server’s Listener (linked to **MixedMessageQueue**) triggers `received()` when data arrives, allowing the server to process the message.

## What is an Event Pump?

In summary, the Event Pump and Event Loop ensure that all events are handled efficiently and in a non-blocking manner, allowing the system to remain responsive while processing events asynchronously. The Event Queue serves as a buffer, and the Event Loop continuously processes each event, ensuring smooth communication between the Requestor and Receiver tasks.