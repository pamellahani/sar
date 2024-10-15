# Hybrid Thread-Event Based Message Queue System

## Overview

This repository features a hybrid message queue system that combines thread-based and event-driven programming for scalable, high-performance communication between clients and servers.

## Project Structure

### Specifications

The specification documents detailing system requirements and functionality can be found at:

```
/home/hani/info5/sar/docs/mix/spec
```

### Design Documentation

The design documents explaining how the event-driven and multithreaded models work together are located at:

```
/home/hani/info5/sar/docs/mix/design
```

### Implementation

The implementation files, containing core classes like `QueueBroker`, `MessageQueue`, and `EventPump`, are located at:

```
/home/hani/info5/sar/src/hybrid
```

### Tests

Test cases, including single-client and multi-client scenarios for validating system functionality, can be found at:

```
/home/hani/info5/sar/src/hybrid/tests
```

## How to Run

1. Compile the source code and tests.
2. Run the test classes, such as `OneClientOneServerTest` or `MultiClientOneServerTest`, to verify system functionality.

## Key Features

- **Hybrid Model**: Combines event-driven and multithreaded programming for balanced scalability and ease of state management.
- **Scalability**: Efficiently manages numerous client connections using non-blocking I/O.
- **Threaded Control Flow**: Threads simplify complex state management, such as retries and connection handling.

## Contact

For questions or contributions, please contact [me](mailto\:pamella.hani@etu.univ-grenoble-alpes.fr).
