# Specification  for Channel/Broker Communication Layer Between Tasks

This document specifies the design and functionality of a distributed task communication system, designed to facilitate byte-oriented data transfer across diverse computing environments. The system supports inter-process communication both within the same machine and across distributed networks, making it an essential tool for modern software applications requiring robust and efficient network communication capabilities.


The communication mechanism uses **byte-oriented circular buffers**, which provides a memory-conscious solution for task communication. Implemented in Java, this layer functions as a distributed system, facilitating seamless communication across various environments.

## 1. System Overview

The communication layer facilitates the exchange of bytes between independent tasks via **channels**, which are managed by a **broker**. **Rdv points** (rdv = rendez-vous) are established between tasks to enable data transmission with minimal overhead and without data loss. 
These channels can be used to establish a **bidirectional communication** flow between two tasks. 

In this distributed task communication system, a task initiates communication by requesting a channel through a broker, specifying connection details. The broker checks for a corresponding partner through a Broker Manager and establishes a rendezvous point once matching partners are found. A channel is then created, enabling the tasks to begin transmitting data using circular buffers that ensure FIFO and lossless communication. Data transmission is managed through read and write operations that handle blocking conditions effectively. Finally, either party can initiate disconnection, triggering a cleanup process that ensures the channel is closed properly without data loss or resource leakage.

## 2. Functional Requirements

This communication layer provides the following core functionalities:

**Channel Management**: Tasks can create or connect to channels using the broker. Channels provide a medium for transmitting byte sequences between tasks.

**Efficient Data Transfer**: Channels utilize circular buffers to handle byte streams efficiently, with minimal memory overhead.

**Concurrency**: The system allows multiple tasks to communicate simultaneously. The broker can manage multiple channels, enabling concurrent communication between different tasks without interference or data loss.

**Error Handling**: Robust error handling, including management of disconnections and buffer overflows.

**Safe Disconnection**: Supports safe disconnection of the system without data corruption or loss.

## 3. Components

### 3.1. Broker

The **Broker** class is a service responsible for managing channels between tasks in a distributed system. A task can request a broker to create a new channel or to connect to an existing one.

#### Broker Properties and Behavior:

The broker maintains a list of active channels and allows tasks to either create new channels or connect to existing ones, facilitating communication across different brokers (e.g., on different machines). The system supports multiple brokers (one per entity, i.e., client/server) allowing tasks to communicate across different brokers.

The broker interface provides the following key methods:

**`Channel accept(int port)`**: Listens on the specified port and accepts incoming channel requests. Returns a `Channel` object, which the task can use to send and receive data. The server side will call this method to establish a channel.

**`Channel connect(String host, int port)`**: Allows a client task to connect to a channel by specifying the server's name and port number. Returns a `Channel` object that the client will use to communicate with the server task. This method is used by clients to initiate communication.

#### Key considerations

Each broker should have its **unique name** and **port number**, which can be used to identify and connect to the broker. For example, we can have:

- `Broker server = new Broker("server", 8080);`
- `Broker client = new Broker("client", 8081);`

The broker is **multi-threaded**, meaning that it can handle multiple tasks concurrently.

### 3.2. Channel

The **Channel** service provides a medium through which tasks communicate. A channel abstracts the underlying circular buffer and provides methods for reading from and writing to the circular buffer.

#### Channel Properties and Behavior:

**FIFO Lossless**: Data is transmitted in the same order it was sent, with no data loss due to the use of TCP connections.

**Bidirectional**: A channel allows simultaneous two-way communication between tasks, supporting full-duplex operations.

**Byte-Oriented**: The communication is based on raw byte streams, providing precise control over how data is packed, transmitted, and interpreted. It allows tasks to transmit and receive data incrementally without needing to buffer large chunks of structured data at once.

#### Channel Interface Methods

The channel interface provides the following key methods:

**`int read(byte[] bytes, int offset, int length)`**: Reads bytes from the circular buffer into the provided byte array. Starts reading from the given offset until the specified length. Returns the number of bytes read, -1 if disconnected. This method is a blocking operation and waits until data is available to read.

**`int write(byte[] bytes, int offset, int length)`**: Writes bytes from the provided byte array into the circular buffer, starting from the given offset and writing up to the specified length. Returns the number of bytes written, -1 if disconnected. This method is a blocking operation and waits until there is space in the buffer.

**`void disconnect()`**: Disconnects the channel, preventing further communication.

**`boolean disconnected()`**: Checks if the channel has been disconnected, indicating no further data transmission is possible. Returns `true` if disconnected, `false` otherwise.

**Note:** Read and write operations are ***mutually exclusive***. This means that only one operation (read or write) can happen at a time per channel. Tasks must manage the synchronization of these operations.

### 3.3. Task

A **Task** is an independent unit of execution that communicates through channels. Tasks are represented as threads, allowing concurrent communication between multiple tasks (we can have n tasks running on 1 broker).

#### Task Key Methods:

The task interface provides the following key methods:

**`Task(Broker b, Runnable r)`**: A constructor that initializes a task with a specific `Broker` to manage its communication channels and a `Runnable` to define the work that the thread should execute.

**`static Broker getBroker()`**: Allows a task to retrieve the `Broker` it uses for managing channels.

### 3.4. Circular Buffer

The **Circular Buffer** is used within the channels to store byte sequences in a **FIFO** manner.

#### Circular Buffer Key Methods:

The circular buffer provides the following key methods:

**`CircularBuffer(byte[] r)`**: Initializes the circular buffer with the provided byte array.

**`boolean full()`**: Returns `true` if the buffer is full, indicating that no more data can be written.

**`boolean empty()`**: Returns `true` if the buffer is empty, indicating that no data is available for reading.

**`void push(byte b)`**: Adds a new element to the buffer. Throws an `IllegalStateException` if the buffer is full.

**`byte pull()`**: Retrieves and removes the next available byte. Returns the byte pulled from the buffer. Throws an `IllegalStateException` if the buffer is empty.

### 3.5. Broker Manager
The Broker Manager acts as the central registry for all brokers, managing their lifecycle and interactions.

It handles the registration and deregistration of brokers, maintaining a directory of active brokers.
The Broker Manager is also responsible for establishing connections between different brokers, supporting distributed operations across multiple machines.

#### Broker Manager Key Methods:

The Broker Manager provides the following key methods:

**`void register(Broker broker)`**: Registers a new broker with the manager, allowing it to participate in the communication network.

**`void deregister(Broker broker)`**: Deregisters a broker from the manager, removing it from the active broker list.

**`Broker findBroker(String name)`**: Retrieves a broker by its unique name, allowing tasks to connect to specific brokers.

### 3.6. Rdv 

Rdvs manage the synchronization points where channel connections are established, ensuring that connections are properly synchronized.

They manage the meeting points for channel connections, coordinating the accept and connect operations to prevent deadlocks and ensure timely connections.

A rdv point is put into place to support blocking operations where a task waits for a corresponding task to join the communication, enhancing the reliability of the connection setup.

#### Rdv Key Methods:

The Rdv provides the following key methods:

**`Channel connect (Broker cb, int port)`**: Initiates a connection request to the specified broker and port, establishing a rendezvous point for the channel connection.

**`Channel accept (int port)`**: Accepts an incoming connection request on the specified port, establishing a rendezvous point for the channel connection.

these methods are mutually exclusive, meaning that only one of them can be called at a time.



## 4. Multi-threading Considerations

**Broker Class**: The Broker is designed to handle multiple tasks concurrently, meaning it is thread-safe.

**Channel Class**: The Channel is not multi-threaded. It is up to the tasks to manage synchronization when performing read and write operations.

**Task Class**: Tasks can run in parallel using threads, but care must be taken when accessing shared resources or channels.

## 5. Asynchronous Operations (read and write)

**`connect(String host, int port)`**: Initiates a **non-blocking** connection request to the specified broker. Allows the task to continue operations while the broker processes the connection in the background. Returns a `Channel` object upon successful connection; can handle retries or notify of failure asynchronously.

**`disconnect()`**: Starts a **non-blocking** disconnection process. Marks the channel as "disconnected" and allows the task to proceed with other operations. Ongoing read/write operations receive an indication (e.g., return -1) once disconnection is complete.

## 6. Limitations

Due to design choices, certain concerns and limitations should be considered when using this communication layer:

**Fixed Buffer Size**: Circular buffer has a fixed size, which can lead to full or empty states. Implement buffer size monitoring and handle full/empty states gracefully. Consider dynamic resizing if applicable.

**Single-threaded Channels**: Read and write operations are not concurrent, limiting throughput. Use task-level synchronization to manage read/write operations or implement a more complex multi-threaded channel design.

**Blocking Operations**: `read` and `write` methods are blocking, causing tasks to wait for data availability or buffer space. Use non-blocking I/O operations or introduce asynchronous methods to improve responsiveness.

## 7. Conclusion

By providing a structured and high-level abstraction over the underlying network and threading complexities, this communication system significantly simplifies the development of networked applications. 

It allows developers to focus more on the strategic aspects of application functionality rather than the intricacies of network management. This shift not only accelerates the development cycle but also enhances the reliability and scalability of applications operating in distributed computing environments.

Furthermore, we will use this Channel communication system to  implement **Message Queues**, which will allow tasks to communicate asynchronously and decouple the sender and receiver, enabling more flexible and robust communication patterns.