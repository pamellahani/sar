# Full-Event Communication Layer Design Documentation

## Overview
This document describes the architecture and interactions between different components
within the full-event communication layer. The system utilizes event-driven mechanisms 
to achieve efficient and scalable message handling among distributed tasks. The main 
components include the `EventPump`, `Listeners`, `EventTask`, `QueueBroker`, `Broker`, 
`MessageQueue`, `Channels`, and `CircularBuffer`, each playing a specific role in ensuring 
asynchronous and non-blocking communication.

## Key Components
1. **EventPump**: The `EventPump` is the central event dispatcher. It maintains a queue of
runnable tasks and sequentially processes them in a separate thread, enabling a non-blocking 
event-driven approach to handling system tasks.
2. **EventTask**: An `EventTask` represents an action in the system that may trigger an 
event or series of events. Tasks can post new actions to the `EventPump` for further execution.
3. **Listeners**: Listeners are interfaces implemented to handle various types of events 
in the communication system. These include:
   - **AcceptListener**: Handles client connection requests on the server side.
   - **ConnectListener**: Handles the process of connecting clients to a server.
   - **MessageListener**: Handles incoming and outgoing messages within message queues.
   - **ChannelListener**: Monitors channel availability, specifically when the circular 
  buffer is full or empty, enabling efficient read and write operations.
1. **QueueBroker**: The `QueueBroker` is an abstraction that manages the binding and unbinding 
of ports, as well as the connection to remote brokers. It provides listeners (`AcceptListener` 
and `ConnectListener`) to handle incoming and outgoing connections, ensuring smooth 
communication between clients and servers.
2. **Broker**: The `Broker` is responsible for managing and routing messages between
 different channels. In this architecture, the `Broker` serves as the foundation for 
 managing the communication layer, helping facilitate the establishment of channels 
 between different components.
3. **MessageQueue**: A `MessageQueue` handles the temporary storage of messages to ensure 
they are sent and received in the correct order. It uses listeners (`MessageListener`) to 
handle messages, supporting asynchronous read and write operations.
4. **Channels**: Channels are used for logical communication between different brokers, 
allowing data to flow between connected tasks.
5. **CircularBuffer**: A data structure used for efficient buffering of messages within 
channels, supporting non-blocking read and write operations.

## Interactions Between Components
### 1. EventPump and EventTask
- The `EventPump` is responsible for managing and dispatching tasks throughout the system. 
Tasks that need to be executed asynchronously are posted to the `EventPump`.
- The `EventTask` is an encapsulation of a runnable operation. Whenever an event occurs, 
such as a message being received, an `EventTask` is created and posted to the `EventPump`.
- For example, in `EchoServerMessageListener`, when a message is received, an `EventTask` 
is created and posted to the `EventPump` to echo the message back to the client.

### 2. EventPump and Listeners
- Listeners interact with the `EventPump` by posting new tasks to handle specific events. 
For instance, when the `EchoConnectListener` receives a connection, it sets up a new `MessageListener` and sends a message.
- The `EventPump` ensures that each listener’s operation is carried out in an asynchronous, 
non-blocking manner.

### 3. QueueBroker, Broker, and Channels
- The `QueueBroker` (`MixedQueueBroker` in this design) is responsible for managing 
connections. It provides methods for binding to a port and accepting clients (`bind`),
as well as connecting to a remote broker (`connect`). It uses listeners like `AcceptListener` 
and `ConnectListener` to manage connection events.
- The `QueueBroker` interacts with the `Broker` (`SimpleBroker`), which handles the actual 
process of accepting or initiating connections through channels. The `Broker` ensures that 
channels are properly set up for communication.
- **Channels** serve as the conduit for communication between brokers, and they handle data 
transfer between clients and servers. Channels interact with `MessageQueue` instances to 
manage the flow of messages.

### 4. Event Listeners
- **AcceptListener**: When the `QueueBroker` accepts a connection request, the 
`AcceptListener` sets up a `MessageQueue` to handle communication between the client and 
server. In `EchoAcceptListener`, it listens for connections and, once accepted, registers
 an `EchoServerMessageListener` to handle message flow.
- **ConnectListener** (`EchoConnectListener`): When a client initiates a connection, the 
  `ConnectListener` is triggered. The `EchoConnectListener` sets up a `MessageQueue` for 
  the client and sends a random message to the server. The message is handled asynchronously,
   thanks to the `EventPump`.
- **MessageListener** (`EchoServerMessageListener`, `EchoClientMessageListener`): The 
`MessageListener` manages incoming and outgoing messages. When a message is received by the
server, the `EchoServerMessageListener` posts an event to the `EventPump` to echo the 
message back. On the client side, `EchoClientMessageListener` ensures the response matches 
the sent message.
- **ChannelListener** (Future Implementation): A `ChannelListener` can be added to monitor channel states. 
This listener can be notified when the `CircularBuffer` is full or empty, prompting actions 
like halting writes when full or resuming reads when data is available.

## Event Flow Example: Echo Server
- **Client Connection**: The `EchoClient` initiates a connection to the `EchoServer` using
 the `QueueBroker`. The connection request is handled by the `EchoAcceptListener` on the 
 server side, which accepts the request and sets up communication channels.
- **Message Exchange**: After establishing the connection, the `EchoConnectListener` on 
the client side sends a random message to the server. The server receives the message and 
the `EchoServerMessageListener` posts an `EventTask` to the `EventPump` to echo the message back.
- **Echo Back**: The client, upon receiving the echoed message, uses `EchoClientMessageListener` 
to validate the received message. Once validated, the `EventPump` stops if no further
tasks are available.

## Channel Listener for CircularBuffer Management
The `ChannelListener` plays a critical role in monitoring the state of the `CircularBuffer`. 
It makes sure that operations on the buffer are handled optimally:
- **On Buffer Full**: When the `CircularBuffer` is full, the `ChannelListener` can trigger 
an event to stop any further write operations to the channel until space is available.
- **On Buffer Not Full**: Once space is available in the buffer, the `ChannelListener` 
notifies the channel to resume writing, thereby avoiding unnecessary idle time.
- **On Buffer Empty**: If the buffer becomes empty, the `ChannelListener` halts read 
  operations until data is available, improving the efficiency of channel operations.
- **On Buffer Not Empty**: When new data is added to the buffer, the `ChannelListener` 
triggers the channel to resume reading, ensuring timely processing of available data.

## Summary
The full-event communication layer leverages an event-driven design to achieve efficient 
and asynchronous communication among various components, such as tasks, brokers, and 
channels. The `EventPump` ensures that all operations are handled non-blocking and 
sequentially, while `Listeners` manage specific aspects of communication, including 
connections, messages, and buffer states.

The `QueueBroker` manages connections and binds ports, interacting closely with `Brokers` 
to establish and maintain communication channels. `MessageQueue` instances ensure message 
integrity and asynchronous processing, while the introduction of a `ChannelListener` would
further enhance the efficiency of the communication system by effectively managing buffer 
states and making sure that read and write operations occur only when appropriate, 
thus achieving a more responsive and well-coordinated system.

