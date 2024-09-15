# Project Overview

This project implements a communication layer between tasks using brokers, channels, and circular buffers for byte-based data transfer.

## How to Run

1. Compile all Java files:
   ```bash
   javac channels/*.java channels/tests/*.java
   ```

*NOTE: Currently, only the `Test` class is used to demonstrate the communication layer, due to TDD (Test-Driven-Development) constraints.*

2. Run the `Test` class to start the server and clients:
   ```bash
   java channels.tests.Test
   ```
   
This will start an echo server and multiple clients that send messages to the server and receive echoed responses. Adjust the `Test` class for different scenarios if needed.

