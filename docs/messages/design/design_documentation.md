## **Message Queue System Design Documentation**

### **Overview**
The Message Queue System enables efficient and thread-safe communication between tasks (clients and servers) by utilizing a centralized `BrokerManager` that manages `QueueBroker` instances. These brokers facilitate message transfer through `Channels` equipped with circular buffers.

### **Key Classes and Components**

1. **Task (Requestor and Receiver)**
   : Represents active entities (clients and servers) that perform communications.
    
    **Methods**:
      
     - `Broker getBroker()`: Retrieves the broker associated with the task for initiating connections.

2. **BrokerManager**: Central management of `QueueBroker` instances, ensuring each task can retrieve its specific broker.
  
    **Methods**:
    - `QueueBroker getBroker(String identifier)`: Retrieves the appropriate `QueueBroker` for a given task identifier, managing all broker instances.

3. **QueueBroker**
   - Facilitates connections between tasks and handles message queuing. This object internally uses a `Broker` to manage details specific to each connection.
  
     **Methods**:
     - `Broker getBroker()`: Retrieves the internal `Broker` that handles the connection specifics for the task.

4. **Channel**
   - Acts as the communication pathway between tasks, handling message passing through dedicated message queues.
   - Each channel incorporates circular buffers for read and write operations to ensure data integrity and thread safety.

    Methods implemented in task1

5. **MessageQueue**
   - A component within `Channel` that manages message traffic using circular buffers.
   - **Methods**:
     - `void write(byte[] data)`: Writes data into the circular buffer.
     - `byte[] read()`: Reads data from the circular buffer, ensuring FIFO delivery of messages.

#### **Communication Flow and Object Interaction**

1. **Initialization of Tasks**:
   - Tasks are instantiated with their respective `Runnable` clusters and retrieve their brokers using the `getBroker()` method provided by `QueueBroker`.

2. **Broker and QueueBroker Management**:
   - `BrokerManager` assigns and manages `QueueBroker` instances for tasks, which in turn utilize `Broker` for connection specifics.

3. **Channel and MessageQueue Operation**:
   - Communication between tasks is facilitated through channels where `write()` and `read()` operations on circular buffers manage the flow of messages.

### **Sequence of Interactions**

    FYI: In the Message Queue System, the sequence of interactions follows a structured pattern reminiscent of Java Messaging Queue mechanisms, ensuring reliable and thread-safe communication between tasks. 

Initially, each `Task`, whether it serves as a requester or a receiver, retrieves its associated broker by invoking the getBroker() method on its QueueBroker instance. This method call facilitates interaction with the internal Broker that manages specific connection details and channel assignments.

Following broker retrieval, the message transmission process begins. Requestor tasks, which initiate communication, write their messages into designated channels using the write() method. These channels, equipped with circular buffers, ensure that messages are queued in a first-in-first-out (FIFO) manner, preserving the order of messages as they are sent. On the other side, receiver tasks pull these messages from the channel by invoking the read() method on the circular buffers, retrieving the data exactly as it was sent.

The response handling phase mirrors the initial message sending process but with roles reversed. Receiver tasks become senders when they need to respond to messages received. They write their responses back into the channel using the same write() method, and the original requestor tasks read the responses using the read() method. This bidirectional communication ensures that both parties can continuously exchange messages and acknowledgments, maintaining a synchronous or asynchronous dialogue as required by their operational context.

### **Thread Safety and Performance Considerations**
- Circular buffers within channels ensure thread safety, preventing data corruption and race conditions.Also, the management of blocking and non-blocking buffer operations optimizes performance, especially in high-concurrency environments.
