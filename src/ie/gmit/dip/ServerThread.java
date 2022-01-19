package ie.gmit.dip;

import java.io.*;
import java.net.Socket;

/**
 * @author Aiden Desmond g00398273@gmit.ie 
 * 
 * ServerThread uses the Threads framework to operate a separate 
 * thread for each user who connects to the ChatServer
 * 
 * ServerThread is responsible for the two-way communication between
 * the "user", represented by the Socket, and the Server, the java
 * class which calls up a userThread as required.
 *
 */
public class ServerThread extends Thread {

	private Socket socket;
	private ChatServer chatServer;
	private PrintWriter out;
	private BufferedReader r;
	private Utils u = new Utils();
	
	/**
	 * Initialises the parameters needed to make the Thread
	 * interoperate with the socket and the Server
	 * 
	 * @param socket
	 * @param chatServer
	 */
	public ServerThread(Socket socket, ChatServer chatServer) {
		
		this.socket = socket;
		this.chatServer = chatServer;
	}

	/**
	 * The main functions of the Thread
	 * Sends information between the Socket and the Server
	 * 
	 * @throws IOException
	 */
	@Override
	public void run(){
		// Initialising the read and write Streams
		try {
			// Started ServerThread for new User"
			InputStream input = socket.getInputStream();
			r = new BufferedReader(new InputStreamReader(input));
			
			//out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			OutputStream output = socket.getOutputStream();
            out = new PrintWriter(output, true);
			
		} catch (IOException e) {
			u.warning("Error in User thread" + this + " - " + e.getMessage());
		}
		
		// Welcomes new user by Printing a list of existing users, if there are any.

			out.println("Welcome to the party!");
			try {
				listUsers();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				u.warning("Error in UserThread/Welcome " + e1.getMessage());
			}

		
		// Client MUST give a userName as the first communication
		String uName = null;
		
		try {
			uName = r.readLine();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			u.warning("Error in UserThread/UserName " + e.getMessage());
		}
		chatServer.addUser(uName);

		// Inform other users of the new user's arrival
		String serverMsg = new String();
		serverMsg = uName + " has connected";
		u.menuItem(serverMsg);
		try {
			
			chatServer.sendMessage(serverMsg, this);
		} catch (IOException e1) {

			u.warning("Error in UserThread/InformNew " + e1.getMessage());
		}
		
		// Now we set up a do loop to wait for client messages...
		String clientMsg = null;
		do {
			try {
				clientMsg = r.readLine();
				chatServer.sendMessage(clientMsg, this);
			} catch (IOException e) {
				u.warning("Error in Client Message Handler " + e.getMessage());
			}
		} while (!clientMsg.equals("!Bye")); // code \q closes the connection
		
		// If the do loop is closed, then the user is disconnecting
		try {
			chatServer.closeMessage(this);
			serverMsg = uName + " has left the building...";
			u.menuItem(serverMsg);
			chatServer.sendMessage(serverMsg, this);
			chatServer.delUser(uName, this);
			
		} catch (IOException e1) {
			u.warning("Problem Exiting Chat Thread: " + e1.getMessage());
		}
	} 

	
	/**
	 * Gets a list of all users from the ChatServer and send to the
	 * Client connected _via_ this thread.
	 * @throws IOException
	 */
	private void listUsers() throws IOException {
		if (chatServer.hasUsers()) {
			String userlist = chatServer.getUsers();
			String userMessage = "The ChatServer's users are: " + userlist;
			out.println(userMessage);

		} else {
			out.println("No other users detected. 😢");
		}
		
	}

	/**
	 * Sends a message from the ChatServer to the Client
	 * connected _via_ this thread.
	 * 
	 * @param message The message to be sent to the user.
	 * @throws IOException
	 */
	public void sendMessage(String message) {
		//System.out.println(Thread.currentThread().getId() + " says " + message);
		out.println(message);
		
	}
	
	
}
