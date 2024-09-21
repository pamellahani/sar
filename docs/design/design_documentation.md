## Design: Communication Layer Between Tasks

### Overview

This project implements a communication layer between `Task` instances to facilitate message exchange.

The design employs thread-safe structures to ensure smooth communication between tasks in a multithreaded environment.

### Components and Interactions

#### 1. **Task**
- A `Task` is represented by a `Thread` that contains `Runnable` instances. Each `Task` starts its communication by calling the `getBroker()` method.
- A `Task` uses its associated `Broker` to initiate communication channels. The broker handles connection requests (`connect`) and accepts incoming connections (`accept`).

#### 2. **Broker**
- The `Broker` class is designed to be thread-safe to handle multiple connection requests (`connect`) and incoming connections (`accept`) simultaneously.
-  The `Broker` constructor requires a reference to a `BrokerManager`, which is also thread-safe. This manager oversees the state of the communication layer.
- `Broker` synchronizes on its `connect` and `accept` operations to ensure that no data races occur during the channel setup.
- The `accept(int port)` method is a **blocking operation** that waits for an incoming connection request (`connect`). This method creates an `Rdv` (rendez-vous) point to coordinate the connection.
- `Broker` maintains a list of `Rdv` instances. Each `Rdv` acts as a meeting point for `accept` and `connect` operations.

#### 3. **BrokerManager**
- The `BrokerManager` oversees multiple `Broker` instances. This is done by referencing the BrokerManager in each Broker constructor. It is thread-safe, providing a consistent and synchronized state of the communication environment.
- The `BrokerManager` handles the allocation and tracking of `Rdv` instances to facilitate communication between `Task` instances.

#### 4. **Rdv (Rendez-vous)**
- The `Rdv` class is a **synchronized** structure representing a meeting point for `accept` and `connect` operations. It manages two states:
  - **Opening of an Rdv** -> Triggered by an `accept` operation.
  - **Closing of an Rdv** -> Triggered by a `connect` operation.

* If multiple `connect` operations attempt to access a non-existent `Rdv`, separate `Rdv` instances are created for each `connect`.

* Upon completing the rendez-vous (both `connect` and `accept` have been called), the `Rdv` instance creates a `Channel` and two `CircularBuffer` objects to manage the communication.

#### 5. **Channel**
- A `Channel` represents the communication link between two `Tasks`. 
  
  Each `Channel` has **2 `CircularBuffer` instances:** 
    - One for reading (`inBuffer`) 
    - One for writing (`outBuffer`). 

- **Operations:**
  - **`read()` and `write()`:** Interact with the `CircularBuffers` to exchange messages between tasks.
- 
  The `disconnect()` method is handled **asynchronously**, allowing the `Channel` to complete any ongoing `read` or `write` operations before marking itself as disconnected. This ensures a smooth shutdown of communication.
  
#### 6. **CircularBuffer**
- Each `CircularBuffer` has two ends:
  - **`in`:** For reading data.
  - **`out`:** For writing data.
- The buffers support first-in, first-out operations, ensuring that messages are delivered in the order they were sent. Therefore, we can say that the `CircularBuffer` is a **FIFO Lossless buffer**. Furhtermore, the `CircularBuffer` class is designed to handle concurrent access safely, providing thread-safe operations for pushing (writing) and pulling (reading) bytes.

### Detailed Flow of Communication

#### **Connection Establishment**
1. A `Task` starts by calling `getBroker()` to obtain a reference to its associated `Broker`.
2. The `Task` uses `Broker.connect()` or `Broker.accept()` to set up a communication channel:
   - **`accept(port)`:** Creates an `Rdv` instance in the `Broker`. This operation is blocking and waits for a corresponding `connect` operation.
   - **`connect()`**: Looks for an existing `Rdv`. If none exists, it creates a new one. Multiple `connect` operations generate new `Rdv` instances if required.
   
3.  The `Rdv` coordinates between `connect` and `accept`:
   - If an `accept` is called first, it transitions the `Rdv` state to "opening."
   - If a `connect` is called, it checks for a matching `Rdv`. 
   - If found, it transitions the `Rdv` state to "closing," creating the `Channel` and the associated `CircularBuffers`.


#### **Data Exchange**
1. The `Channel` created by the `Rdv` is used for communication between the `Tasks`.
2. The `Channel` manages two `CircularBuffers` for bidirectional data transfer:
   - **`read()` method:** Retrieves data from the `inBuffer`.
   - **`write()` method:** Pushes data into the `outBuffer`.

#### **Asynchronous Disconnection**
1. When a `Task` initiates a disconnection, the `Channel` transitions to a `disconnecting` state.
2. The `Channel` performs an asynchronous disconnection, it waits for the buffers to complete any ongoing read or write operations. Once the buffers are empty and no further operations are pending, it sets the channel's state to `disconnected`.
1. The `Rdv` and `Broker` are informed of the disconnection. The `Rdv` is removed, and the `Broker` is updated accordingly.

### Thread-Safety Considerations
- **Broker and BrokerManager:** These are thread-safe, ensuring that concurrent `connect` and `accept` operations do not cause data races or inconsistencies.
- **Rdv:** Synchronization within `Rdv` ensures that only one `connect` and one `accept` can complete the rendez-vous, avoiding race conditions.
- **CircularBuffer:** The buffer implements synchronized push and pull methods to prevent concurrent access issues during read and write operations.

