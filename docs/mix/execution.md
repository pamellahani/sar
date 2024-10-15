## Execution Flow of the Asynchronous Messaging System (Hybrid Version)

### Step 1: Initialization and Binding
1. **QueueBroker Initialization**: A server thread creates an instance of `QueueBroker` by giving it a specific name.
2. **Binding to a Port**: The server calls the `bind` method of the `QueueBroker`, specifying a port and an `AcceptListener`. This method is non-blocking and returns immediately.

### Step 2: Client Connections
1. **Connection Initialization**: A client (or another server) wishing to establish a connection with the `QueueBroker` uses the `connect` method:
   - `boolean connect(String name, int port, ConnectListener listener)`: Attempts to connect to the `QueueBroker` using the specified `name` and `port`, registering a `ConnectListener` to handle the connection responses. This method is also non-blocking.
   - If the connection attempt is initiated successfully, `connect` returns `true`.
   - If the connection is successful, the client's `ConnectListener` is notified via `connected(MessageQueue queue)`, where `queue` is the `MessageQueue` assigned to this connection.
   - If the connection is refused, the `ConnectListener` is notified via the `refused` method.

### Step 3: Managing Incoming Connections
1. **Notification of Accepted Connections**: When an incoming connection is accepted by the server, the `AcceptListener` configured during the binding (`bind`) is invoked, receiving the `MessageQueue` associated with this new connection.

### Step 4: Asynchronous Communication
1. **Data Reception**: The clients' `MessageQueue` listen for incoming messages through configured `Listener`s. When data is received, the `received` method is called.
2. **Sending Messages**: Messages are sent using the `send` methods of `MessageQueue`, which are non-blocking and add the message to the queue for sending.

### Step 5: Closing and Cleanup
1. **Closing Connections**: The `MessageQueue` can be closed by calling `close`, after which `closed` can be used to check the state of the queue.