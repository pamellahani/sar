# Use case 1 : Establish a Communication Channel Between Two Tasks

## Precondition :
2 `Tasks` instances are running, with each task having a reference to its associated `Broker`. These `Brokers` are managed by a `BrokerManager`, which maintains the state of the communication channels.
## Postconditions :
A communication channel is established between the two `Tasks`, allowing them to exchange messages (bytes) through the `Channel`. This is done by reading from and writing to the `CircularBuffer` instances.
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
1. **Scenario 1: Multiple Tasks Attempt to Connect Simultaneously:**

    If Task B and Task C both try to connect to Task A simultaneously, and there is no existing `Rdv`, separate `Rdv` instances are created for each connection request.
    Task A will handle each `Rdv` independently, establishing separate `Channel` instances for each `connect` request.

2. **Scenario 2: Task B Connects Before Task A Accepts:**

    If Task B attempts to connect before Task A has called accept, a new Rdv instance is created, and Task B waits for an accept operation from Task A.
    When Task A eventually calls accept, the Rdv is matched, and the connection is completed as in the main scenario.

# Use case 2 : Handling Multiple Tasks on a Single RdV Point

## Preconditions :
## Postconditions :
## Main Success Scenario (Happy Path) :
## Alternative Scenarios :

# Use case 3 : Two Tasks Attempt to Disconnect Simultaneously

## Preconditions :
## Postconditions :
## Main Success Scenario (Happy Path) :
## Alternative Scenarios :
