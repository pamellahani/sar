# Specification for EchoServer, a Communication Layer Between Tasks

This specification outlines the design and functionality of a communication layer between 
tasks, enabling byte-based data transfer. 

This layer is intended for use in scenarios 
where tasks may run in the same or different processes or even on different machines.

The communication mechanism is based on byte-oriented circular buffers, which provide an 
efficient and memory-conscious solution for inter-task communication.


## 1. System Overview

The communication layer facilitates the exchange of bytes between independent tasks via 
channels managed by a broker. 
These channels can be used to establish a bidirectional 
communication flow between two tasks. This layer allows tasks to communicate without regard
to whether they are on the same or different machines.

## 2. Functional Requirements
This communication layer provides the following core functionalities:

- Tasks can create or connect to channels using the broker. Channels provide a medium for transmitting byte sequences between tasks.

- Channels support sending and receiving byte streams using a circular buffer. The circular buffer allows for efficient handling of byte data, without excessive memory allocation.

- The system allows multiple tasks to communicate simultaneously. The broker can manage multiple channels, enabling concurrent communication between different tasks without interference or data loss.

- Robust error handling.

- Disconnection of the system without data corruption or loss.


## 3. Components

### 3.1. Broker
A service responsible for managing channels. A task can request a broker to create a new channel or connect to an existing one. 

The broker maintains a list of active channels and manages the communication between tasks.

### 3.2. Channel
The medium through which tasks communicate. A channel abstracts the underlying circular buffer and provides methods for reading from and writing to the circular buffer.

### 3.3. Task
Independent unit of execution that sends and receives byte sequences through communication channels. A task is represented as a thread.

### 3.4. Circular Buffer 
Used within the channels to store the transmitted byte sequences in a FIFO manner.


## 4. Communication Flow
The design assumes tasks operate within the same process, and the communication is facilitated through circular buffers. 

## 5. Error Handling

## 6. Performance Considerations

## 7. Limitations

## 8. Conclusion 