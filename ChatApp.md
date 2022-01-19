# ChatApp
## Galway-Mayo Institute of Technology
## Higher Diploma in Science (Software Development)
## Network Programming Project

## Submission of Aiden Desmond

### Design Brief:-

> To create a network-based chat application in Java, using the Java Socket API

### Usage

Create file `app.config` of the following structure:-

```text
SERVER_PORT=10000 # The Port the app is to use.
SERVER_IP=0.0.0.0 # The IP address of the Server to Connect to
USERNAME=Bob      # Desired username
```

Once app.config is populated, the program is entirely menu-driven, and should be self-explanatory.

### Resources 
In designing an application to meet this brief, I primarily relied on the JavaDocs documentation for `java.net.socket` [^1] `java.io.InputStream` [^2] and `java.lang.Thread` [^3]. Some other websites were consulted, most importantly:

`Why is `thread.stop()` deprecated`[^4], `How to read a config file in java` [^5], and `MultiThreading in Java Tutorial with Program and Examples` [^6].

### Design Concepts:-

I wanted to provide a multi-threaded program which permitted any number of users to communicate simply and effectively, with a minimum of configuration. To accomplish this, and to provide a flexible and robust program, I decided to implement the following structural design:-

1) `Runner` class - announces the program, and loads the menu
2) `Menu` class - allows the user to decide if they wish to run the app as a *server* or as a *client*.
3) `ChatServer` - Opens the requisite port to listen to, acts as an information broker between the users.
4) `ServerThread` - One thread opened for each user, to act as information broker between the client and the primary server.
5) `ChatClient` - Single class which _simply_ sends and receives information from its own ServerThread.

### Design Complexities

1) Server Side

I wanted the ChatServer application to perform the following functions:-

a) Launch a new user when they connected, and tell the user who was already connected, and tell existing users that a new user had joined.
b) Receive messages from that user
c) Send messages to **other users** but not the one which sent the message
d) Acknowledge when users left the Server and inform other users

Drawing on experience in the Advanced OOP program this semester, I decided to create two different HashSets as follows:

```java
	private Set<String> users = new HashSet<>();
	private Set<ServerThread> uThreads = new HashSet<>();
```

**users** is a HashSet of user's names, as set in their configuration files.
**uThreads** is a record of the *Threads* spawned for each user.

This allows for a speedy lookup of users and Thread identities. This is used when sending a message from Threads in the method sendMessage:-

```java
	public void sendMessage(String message, ServerThread sender) throws IOException {
		System.out.println("msg: " + message);
		for (ServerThread user : uThreads) {
			if (user != sender) {
				user.sendMessage(message);
			}
		}
	}
```

By using this system, the Server is given the identity of the senderThread, then sends the message to all userThreads, *except* the senderThread. This simple functionality also assisted in setting up the method to close connections, using similar logic.

2) Client Side

Reading and Writing messages bi-directionally is a resource-intensive process, and relies heavily on i/o which can be laggy. As a result the Writer and Reader could block each other in the Client. I decided to break those functions of the Client into two internal Threads - `ChatReader` and `ChatWriter` - both of which deal solely with one of the Streams.

### Shutting down gracefully

One of the requirements of the Design Brief was that the Client app should shut down gracefully on the issuing of the command code `\q`. I decided to implement this solely in the ChatClient, which sends a close code to the Server and shuts down the Thread Writer; the Server then responds with a different shutdown code which closes the Thread Reader; and the Server deletes the User from its records.

### Other design decisions

Initially, the code was replete with far more `try/catch` blocks, caused by InputStreamWriter's exception throwing abilities. I found a solution to this in the `PrintWriter` class [^7] which, while a more primitive form of Writer has the singular feature:-

> Methods in this class never throw I/O exceptions, although some of its constructors may.

### Conclusions

The code is fully commented, and is robust and loosely coupled. The project was fun to complete, and I thank you for all your work with the class this semester.

[^1]: https://docs.oracle.com/en/java/javase/15/docs/api/java.base/java/net/Socket.html
[^2]: https://docs.oracle.com/en/java/javase/15/docs/api/java.base/java/io/InputStream.html
[^3]: https://docs.oracle.com/en/java/javase/15/docs/api/java.base/java/lang/Thread.html
[^4]: https://docs.oracle.com/en/java/javase/15/docs/api/java.base/java/lang/doc-files/threadPrimitiveDeprecation.html
[^5]: https://stackoverflow.com/questions/16273174/how-to-read-a-configuration-file-in-java
[^6]: https://www.guru99.com/multithreading-java.html
[^7]: https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/io/PrintWriter.html
