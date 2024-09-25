# Message Queue Specification

## Introduction

Most of us have at some point had to use the services of a broker when executing transactions. They act as the intermediary between the buyer and the seller. Whether it is buying shares from a securities exchange, buying real estate, or even purchasing a car, in everyday transactions brokers are prevalent across many business domains.

In computing, tasks often need to communicate with each other asynchronously. A common solution to this is the use of a Message Queue, which acts as an intermediary between different tasks, enabling them to exchange information efficiently and without the need for direct interaction. Message Queues facilitate smooth communication between distributed components of a system, making it possible for tasks to operate independently of one another.

## System Overview

The broker’s main role is to make it easier for a transaction to occur. They can represent the buyer or the seller, but not both at the same time. For instance, a mortgage broker helps match borrowers to financial institutions offering the most affordable loans based on the borrower’s financial situation and interest-rate needs.

The broker concept is also applicable when integrating enterprise application systems that are made up of many heterogeneous software components that need to exchange information such as transactions and notification events. The broker in this case is used to implement or facilitate the round-the-clock messaging of data between the consumers and the producers.

The Message Broker achieves this by using a Message Queue to provide a persistent mechanism. A queue of messages is placed between two parts of the system in order for them to communicate with each other. The message is the data transported between the sender and the receiver. For example, one part of the system tells another part to start processing a task.

The architectural pattern of Message Brokers is often used in between microservices and/or for long-running tasks. The basic architecture is simple, producers create messages and deliver them to the Message Queue. Consumers connect to the Message Queue and subscribe to messages from the queue. Messages placed on the queue are stored until the consumer acknowledges them (i.e. consumer tells the message broker that the message has been received and handled).

In a nutshell, this way of handling messages:
- Facilitates asynchronous communication between applications.
- Minimizes the mutual awareness between the microservices.
- Decouples the sender from the receiver of a message.


## What is a Message Queue used for?
Java Message Queue has multiple use cases.

1. The Java Message Queue can be used to develop distributed applications that follow an asynchronous messaging design pattern. It defines an Event Bus that buffers the messages between the sending and receiving process. This allows for non-blocking IO, where the current processes of the client application are not blocked while waiting for the downstream consumer to process the message. 

2. It can be used as a Message-Oriented Middleware (MOM) that allows for transactions or event notifications to be sent and received over distributed software applications that are spread across multiple operating systems and networking protocols.


