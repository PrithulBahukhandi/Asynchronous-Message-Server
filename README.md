# Asynchronous-Message-Server
Distributed Systems

I have implemented an asynchronous message service consisting of a server process and three clients.
Each client process will connect to the server over a socket. The server should be able to handle all three clients concurrently.

Clients will prompt the user for a username. When a client connects to the server, its username should be displayed by
the server in real time. Two or more clients may not use the same username simultaneously. Should the server detect a
concurrent conflict in username, the client’s connection should be rejected, and the client’s user should be prompted to
input a different name.
The server will keep a cumulative log of previously used usernames and display those names on its GUI. The server
will indicate which of those usernames represent clients presently connected to the server and which are not
connected. Clients may reuse usernames and a client reusing an extant username should not be treated as a duplicate
in the log.
When the server encounters a new username, a unique message queue will be created for that username. This queue
must be able to contain an arbitrary number of messages. 
When a message is received by the server, the server should place the message in the corresponding queue for that
intended recipient and mark that message with the time of its reception. Queues should be persistent i.e. the contents should not get get deleted when the
server restarts.
The message server supports 1-1, 1-n, 1-all messaging.
I am making use of apache mq message broker. One need to install apache mq binaries to run the program and store the messages in the queue.
