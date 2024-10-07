# Queue Broker and Message Queue Specification - Mixed Event/Thread Model

### Overview
The provided classes and interfaces facilitate an asynchronous communication framework using non-blocking operations and event-driven callbacks. This model allows effective communication across different threads without interfering with the thread's ability to execute other tasks.

### Classes and Interfaces

1. **QueueBroker** - An abstract class responsible for managing connections and binding to ports. It uses listeners to handle accepted connections asynchronously.

2. **MessageQueue** - An abstract class for a queue that supports sending messages, setting listeners for receiving messages, and managing the queue's lifecycle.

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

### Ownership differences in `send` method

In programming, the concept of "ownership" often pertains to which part of the code is responsible for managing the memory and lifecycle of data objects. When discussing the two `send` methods in the context of Java (as inferred from the use of byte arrays and method signatures), the notion of ownership particularly involves who is responsible for the byte arrays being passed around and how they are managed after being sent. Here’s how it applies to each method:

#### 1. `boolean send(byte[] bytes)`

In this method, a complete byte array is passed to the `send` method. 

**Ownership Before the Call:**
The caller owns the byte array and is responsible for its creation and management.

**Ownership After the Call:**
The byte array is handed over to the `MessageQueue` for sending. However, in Java, actual ownership in terms of memory management does not change because Java uses a garbage-collected environment where memory deallocation is handled automatically.
  The `MessageQueue` might simply queue the reference to the byte array, not a copy. Therefore, it is crucial that the caller does not modify the array while it is still in use by the queue, unless such behavior is clearly safe and intentional.

#### 2. `boolean send(byte[] bytes, int offset, int length)`
This method involves sending a specific segment of a byte array, using `offset` and `length` parameters to determine the portion of the array to send.

**Ownership Before the Call:** As with the first method, the caller is responsible for managing the original byte array.

**Ownership After the Call:**
- In scenarios where a new byte array might be created (e.g., `Arrays.copyOfRange(bytes, offset, offset + length)`), the newly created byte array (the subset) is now managed by the `MessageQueue`.
- The original byte array remains under the ownership of the caller, who must ensure it is not improperly modified while the sent segment may still be in use—assuming a copy is made. If no copy is made and only a reference or slice is passed, similar caution applies as with the first method.

#### Memory Management Considerations: 
Since Java handles memory deallocation via its garbage collection mechanism, the concept of ownership here is more about the responsibility of not altering the content of the byte arrays while they are in use rather than deallocating memory. The critical aspect is ensuring that data integrity is maintained throughout the operation, especially in concurrent environments or multi-threaded applications where data race conditions might occur.
