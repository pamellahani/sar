
# Use Cases
# Use case 1 : Establish a Communication Channel Between Two Tasks

## Precondition :
2 `Task` instances are running, with each task having a reference to its associated `Broker`. These `Brokers` are managed by a `BrokerManager`, which maintains the state of the communication channels.
## Postconditions :
A communication channel is established between the two `Task`, allowing them to exchange messages (bytes) through the `Channel`. This is done by reading from and writing to the `CircularBuffer` instances.
## Main Success Scenario (Happy Path) :
- **Step 1:** Task A and Task B  are created, each starting as a `Thread` that contains` Runnable` instances.
- **Step 2:** Task A calls `getBroker()` to obtain a reference to its associated `Broker`. Then, Task A invokes `Broker.accept(port)` to wait for an incoming connection. This is a blocking operation, and an `Rdv `(rendez-vous) instance is created.
- **Step 3:** Task B calls `getBroker()` to obtain a reference to its associated `Broker`. Then, Task B invokes `Broker.connect()` to establish a connection with Task A. The` Broker` checks for an existing `Rdv` associated with the connection request. If not, a new one is created.
- **Step 4:** RdV is matched, and a `Channel` is created to facilitate communication between Task A and Task B. Two `CircularBuffer` instances are created for reading and writing messages.
- **Step 5:** Task A and Task B can now use the `Channel` for bidirectional communication:

    - Task A calls `write()` to send data through the outBuffer.
    - Task B calls `read()` to retrieve data from the inBuffer.

Similarly, Task B can send data to Task A using the reverse buffer operations.  

- **Step 6:** The communication channel remains active until one of the tasks calls `disconnect()`.

## Alternative Scenarios:
1. **Scenario 1: Multiple tasks attempt to connect simultaneously:**

    If Task B and Task C both try to connect to Task A simultaneously, and there is no existing `Rdv`, separate `Rdv` instances are created for each connection request.
    Task A will handle each `Rdv` independently, establishing separate `Channel` instances for each `connect` request.

2. **Scenario 2: Task B connects before Task A accepts:**

    If Task B attempts to connect before Task A has called accept, a new Rdv instance is created, and Task B waits for an accept operation from Task A.
    When Task A eventually calls accept, the Rdv is matched, and the connection is completed as in the main scenario.

# Use case 2 : Handling Multiple Tasks on a Single RdV Point

## Preconditions :
Multiple `Task` instances are attempting to connect to a `Broker` that has not yet called `accept()` method on a given port numbert.
## Postconditions :
Each connection request is handled independently, with each `Task ` obtaining a separate `Rdv` if the initial `Rdv` is already occupied. 
## Main Success Scenario (Happy Path) :
- **Step 1:** let's assume that Task A already called `accept()` on a given port number, and Task B and Task C are attempting to connect to Task A by calling `connect()`.
  
- **Step 2:** The `Broker` checks for an existing `Rdv` associated with the connection request. If an Rdv is already present (e.g., created by Task A's `accept()`), Task B or Task C waits for the rendez-vous to complete. Otherwise, the `Broker` creates a new RdV. 
- **Step 3:** 2 new `Rdv` instances are created for Task B and Task C, respectively. Each `Rdv` is associated with a separate `Channel` instance. Task A’s `Broker` handles each connect request independently, ensuring that each Task gets its own communication channel once the `accept()` operation is called.
- **Step 4:** Task A, can now communicate with Task B and Task C independently, using the respective `Channel` instances. the communication channels remain active until one of the tasks calls `disconnect()`. 
  
## Alternative Scenarios :
1. **Scenario 1: Task A disconnects while Task B and C are still connected**
   
   In this case, Task A's `Channel` is closed, and the associated `Rdv` instances to Task B and C are removed by the `BrokerManager`. Task B and C are notified of the disconnection, and their respective `Channel` instances are closed. 
2. **Scenario 2: Task B attempts to connect before Task A calls `accept()` method**
   
   In this case, the `BrokerManager` does not find any existing `RdV` for the port associated with Task A's `accept()` method. Task B waits for the `Rdv` to be created by Task A's `accept()` method. Once the `Rdv` is created, a `Channel` is created.

# Use Case 3: Two Tasks Attempt to Disconnect Simultaneously

## Preconditions:
- Two `Task` instances (Task A and Task B) are actively communicating through a `Channel` that was established by a previous `accept()` and `connect()` operation.
- Both tasks are running as `Thread`s and are capable of reading and writing data through the `CircularBuffer` instances linked to the `Channel`.

## Postconditions:
- Both `Task` instances (Task A and Task B) successfully disconnect from the `Channel`.
- The associated `Channel` and its `CircularBuffer` instances are closed and cleared, preventing any further data exchange.
- The `Rdv` instance managing the communication between the two `Tasks` is removed by the `BrokerManager`, releasing any resources associated with the `Channel`.

## Main Success Scenario (Happy Path):
- **Step 1:** Task A initiates the disconnection by calling the `disconnect()` method on its `Channel`. Task A signals that it no longer intends to send or receive messages.
  
- **Step 2:** Simultaneously, Task B also calls the `disconnect()` method on its `Channel`. Task B signals that it no longer intends to communicate.

- **Step 3:** The `disconnect()` method in the `Channel` sets the `isDisconnected` flag to `true` for both tasks, signaling that the `Channel` is no longer active.

- **Step 4:** The `BrokerManager` detects that both Task A and Task B have disconnected from the `Rdv`. It removes the `Rdv` instance associated with the communication between the two tasks, cleaning up any remaining resources.

- **Step 5:** Both Task A and Task B confirm that they are disconnected by calling the `disconnected()` method on the `Channel`. This method returns `true`, confirming that the connection has been successfully terminated.

- **Step 6:** The system concludes the disconnection process, and no further communication is possible between Task A and Task B through the previously established `Channel`.

## Alternative Scenarios:

1. **Scenario 1: One Task Disconnects Before the Other Task:**
   - **Step 1:** Task A calls `disconnect()` and successfully terminates its connection to the `Channel`.
   - **Step 2:** Task B continues to attempt to communicate but detects that the `Channel` has been disconnected by Task A.
   - **Step 3:** Task B receives a message or error indicating that the `Channel` has been closed.
   - **Step 4:** Task B then calls `disconnect()` on its end, and the `BrokerManager` cleans up the associated `Rdv` and `Channel`.

2. **Scenario 2: A Task Disconnects, But the Other Task Fails to Disconnect:**
   - **Step 1:** Task A successfully calls `disconnect()` and terminates its communication.
   - **Step 2:** Task B does not call `disconnect()` due to a system error or mismanagement.
   - **Step 3:** The `BrokerManager` detects that Task A has disconnected and waits for a disconnection from Task B.
   - **Step 4:** After a timeout or system intervention, Task B's connection is forcibly terminated by the `BrokerManager`, and the resources associated with the `Rdv` and `Channel` are cleaned up.
