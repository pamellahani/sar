### Updated Message Queue System Design Documentation

#### **Overview**
The Message Queue System facilitates communication between a client and a server (both represented by `Task` objects) through message brokers and channels. Messages are sent and received as byte arrays. The system includes asynchronous and blocking communication patterns and ensures thread safety through the use of request and response queues that extend `MessageQueue`.

#### **Key Classes and Components**

1. **QueueBroker**
   - Abstract class responsible for managing connections and communication channels between tasks (client and server).
   - **Methods**:
     - `String name()`: Returns the name of the broker.
     - `MessageQueue accept(int port)`: Blocks until a connection is established for incoming messages on a specified port.
     - `MessageQueue connect(String name, int port)`: Blocks until a connection is made to a remote queue on the specified port.

2. **MessageQueue (RequestQueue and ResponseQueue)**
   - Manages the sending and receiving of messages.
   - **RequestQueue**: Handles the client’s request data.
   - **ResponseQueue**: Handles the server’s response data.
   - **Methods**:
     - `void send(byte[] bytes, int offset, int length)`: Asynchronously sends bytes starting from a specific offset and length.
     - `byte[] receive()`: A blocking method that waits until a complete message is received.
     - `void close()`: Closes the queue for sending or receiving messages.
     - `boolean closed()`: Checks if the queue is closed.

3. **Task (Client and Server)**
   - Represents the active entities (client and server) participating in communication.
   - **Methods**:
     - `Task(Broker b, Runnable r)` and `Task(QueueBroker b, Runnable r)`: Constructors that associate a task with a broker or queue broker.
     - `Broker getBroker()`: Retrieves the broker managing the task.
     - `QueueBroker getQueueBroker()`: Retrieves the queue broker associated with the task.

4. **MessageBrokerManager**
   - Manages the lifecycle and retrieval of `MessageBroker` instances for both the client and server tasks.
   - Responsible for tracking both brokers and making sure that they are properly connected to each other.
   
5. **Channel**
   - Represents the communication pathway between client and server tasks.
   - Each channel contains:
     - **RequestQueue**: Where the client’s requests (messages) are placed.
     - **ResponseQueue**: Where the server’s responses are placed.
   - Facilitates communication by connecting both the client and server to their respective queues.

#### **Communication Flow and Object Interaction**

1. **Client and Server (Tasks)**
   - The client and server are represented by `Task` objects. Each task is linked to a `MessageBroker`, which handles the communication for that task. Both brokers are managed by the `MessageBrokerManager`.
   - **Client Task**:
     - The client task connects to its `Channel` via the `MessageBroker` and sends requests using the **RequestQueue**. It sends byte arrays asynchronously with the `send()` method of `MessageQueue`.
     - After sending the request, the client waits for a response using the blocking `receive()` method on the **ResponseQueue**.
   - **Server Task**:
     - The server task connects to the same `Channel` but listens on the **RequestQueue**. It receives client messages using the blocking `receive()` method.
     - After processing the message, the server sends a response through the **ResponseQueue**, which the client will retrieve.

2. **MessageBrokerManager**
   - The `MessageBrokerManager` manages and stores the brokers for both client and server. It ensures that the communication between tasks is properly initialized and managed by storing both brokers in the `QueueBroker`.

3. **QueueBroker**
   - The `QueueBroker` encapsulates the client and server's `MessageBroker` instances. It manages the lifecycle and connection setup between the client and server via the `accept()` and `connect()` methods.
   - Both methods are **blocking**, ensuring that the connection between the client and server is fully established before any message exchanges occur.

4. **Channel**
   - A `Channel` represents the communication link between the client and server.
   - **RequestQueue**: The client sends its requests through the request queue.
   - **ResponseQueue**: The server sends its responses through the response queue.
   - These queues extend `MessageQueue`, enabling asynchronous sending (via `send()`) and blocking reception (via `receive()`).



#### **Interaction Sequence**

Both client and server tasks are initialized with their respective `MessageBroker`, which is stored in the `MessageBrokerManager` for tracking and management. The client uses the `connect(String name, int port)` method of `QueueBroker` to initiate a connection to the server. This method is blocking, waiting for the server to accept the connection. The server listens using the `accept(int port)` method, which is also blocking, and establishes a connection when the client’s request arrives.
The client sends a message through the **RequestQueue** using the `send()` method, and the server retrieves the message using the `receive()` method. After processing the message, the server sends a response through the **ResponseQueue**, which the client retrieves using the `receive()` method.
For further details, the interaction sequence is as follows:
   - **Request Phase**:
     - The client sends a byte array through the **RequestQueue** using the `send()` method.
     - The server retrieves the message from the **RequestQueue** using the blocking `receive()` method.
   - **Response Phase**:
     - After processing the client’s message, the server sends a response through the **ResponseQueue**.
     - The client retrieves the server’s response by invoking the `receive()` method on the **ResponseQueue**.

Once communication is complete, both the client and server can close their message queues using the `close()` method.

#### **Key Communication Patterns**
- The `send()` method in the message queue is asynchronous, allowing the client to continue its operations without waiting for an acknowledgment from the server.
- The `receive()` method is blocking, ensuring that the task waits until a complete message is available before proceeding.

#### **Thread-Safety Considerations**
- The blocking nature of the `receive()` method ensures thread safety during message retrieval. Multiple threads accessing the same queue will not cause race conditions because the method only returns when a complete message is ready.
- The non-blocking `send()` method allows the sender to proceed immediately, providing better performance when handling multiple clients.

This system ensures a smooth and efficient message-passing mechanism between the client and server using `Task`, `MessageBroker`, `QueueBroker`, `MessageQueue`, and `Channel` instances.