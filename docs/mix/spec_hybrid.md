# Queue Broker and Message Queue Specification

### Overview

This version of communication between Product and Consumer forms a hybrid asynchronous communication framework that combines event-driven and multithreaded models. By merging the flexibility of events with the simplicity of threads, this hybrid design achieves non-blocking communication while also retaining an intuitive, sequential flow of control. This approach is particularly effective for building scalable, massively concurrent systems without the downsides of each individual model.

### Classes and Interfaces

#### Event-Based Components
1. **QueueBroker** - An abstract class responsible for managing connections and binding to ports. It uses listeners to handle accepted connections asynchronously.

2. **MessageQueue** - An abstract class for a queue that supports sending messages, setting listeners for receiving messages, and managing the queue's lifecycle.
3. **Event Task** - A task that can be executed by the Event Pump. It is used to process the messages received by the QueueBroker.
4. **AcceptListener** - An interface for handling accepted connections.
5. **ConnectListener** - An interface for handling connection attempts.
6. **Listener** - An interface for handling received messages.
7. **Reader** - Reads Payload from the MessageQueue and sends it to the Consumer, hence transferring the ownership of the Payload to the Consumer's listener.
8. **PostEvent** - Recieves an event from either the QueueBroker's Runnable or from the Readable instance and posts an event to the Listener interface.

---

1. **Event Pump and Event Loop** - The Event Pump is responsible for managing the event loop and dispatching events to the appropriate handlers.
   
2.  **Event Queue** - A queue that holds events to be processed by the Event Pump. The Event Queue is thread-safe and allows for the posting of events from multiple threads.
----
#### Thread-Based Components
1. **Channel**: A channel contains 2 CircualrBuffers, enabling bidirectional communication between the Product and Consumer.For more details, refer to the [Channel Specification](./channel.md).
2. **Broker**: A broker is responsible for managing the connections and binding to ports. It uses listeners to handle accepted connections asynchronously.
3. **Task**: A task is a unit of work that can be executed by a thread. It is used to process the messages received by the Broker.
  

### Asynchronous Flow and Interaction Model

#### QueueBroker Class

- **Constructor**:
  - `QueueBroker(String name)`: Initializes a new instance of `QueueBroker` with a specified name for identification purposes.

- **Methods**:
  - `boolean bind(int port, AcceptListener listener)`: Binds the `QueueBroker` to a specified port and sets an `AcceptListener` to handle new connections. The method returns `true` if binding is successful and `false` otherwise.
  - `boolean unbind(int port)`: Unbinds the `QueueBroker` from a specified port. Returns `true` if the operation is successful.

- **AcceptListener Interface**:
  - `void accepted(MessageQueue queue)`: Invoked when a new connection is accepted. The `MessageQueue` associated with the connection is passed as an argument.

- **ConnectListener Interface**:
  - `void connected(MessageQueue queue)`: Called when a connection attempt is successfully established, passing the connected `MessageQueue`.
  - `void refused()`: Called if the connection attempt is refused.

- **Connection Handling**:
  - `boolean connect(String name, int port, AcceptListener listener)`: Attempts to connect to a `QueueBroker` identified by `name` at the specified `port`, setting an `AcceptListener` to handle the connection response. Returns `true` if the connection attempt starts successfully.

#### MessageQueue Class

- **Listener Interface**:
  - `void received(byte[] msg)`: Callback method triggered when a message is received. The received bytes are passed as an argument.

- **Methods**:
  - `void setListener(Listener listener)`: Sets a `Listener` to handle incoming messages asynchronously.
  - `boolean send(byte[] bytes)`: Sends a byte array message. Returns `true` if the message is successfully enqueued.
  - `boolean send(byte[] bytes, int offset, int length)`: Sends a portion of a byte array starting at `offset` and extending for `length` bytes.
  - `void close()`: Closes the `MessageQueue`. Subsequent send operations will be disallowed.
  - `boolean closed()`: Checks if the `MessageQueue` is closed.

### Thread-level Interaction

- **Non-blocking Operations**: All operations are designed to be non-blocking to ensure that no thread is indefinitely blocked waiting for an operation to complete.
- **Event-driven Callbacks**: Listeners are employed to handle events such as connections being accepted, connections being established, or messages being received.
- **Error Handling**: All methods that perform operations returning a boolean indicate the success or failure of the operation, enabling the calling thread to handle errors appropriately without blocking.
- **Concurrency**: Care should be taken to ensure that `MessageQueue` methods are thread-safe, particularly in modifying internal states like message buffers or connection status.

### Use Case Scenario

1. A server thread creates an instance of a derived `QueueBroker` and binds to a port.
2. It sets an `AcceptListener` to asynchronously accept incoming connections and instantiate a `MessageQueue` for each.
3. Clients use `QueueBroker` to connect, triggering `ConnectListener` callbacks.
4. Both clients and the server use their respective `MessageQueue` instances to send and receive messages through listener callbacks, managing flow control and connection state asynchronously.


### Why use a Hybrid Thread-Event Approach?
[Explanation the benefits of using a hybrid thread-event model for communication between Product and Consumer.](./hybrid_approach.md)