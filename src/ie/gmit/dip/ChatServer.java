package ie.gmit.dip;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * @author Aiden Desmond g00398273@gmit.ie
 * 
 * ChatServer is the primary class to handle the server implementation.
 * It acts as a broker between the different ServerThreads launched as
 * new users connect to the chatServer.
 * 
 *
 */
public class ChatServer {

	private int port;
	private Set<String> users = new HashSet<>();
	private Set<ServerThread> uThreads = new HashSet<>();
	private Utils u = new Utils();
	
	/**
	 * @param port The port to bind to, obtained from the menu system
	 */
	public ChatServer(int port) {
		this.port = port;
	}
	
	public void start() {
		
		try ( ServerSocket servSocket = new ServerSocket(port)) {
			u.menuItem("Server is Listening on " + port);
			u.menuItem("Use Ctrl-C to exit.");
			
			while (true) {
				Socket socket = servSocket.accept();
				u.menuItem("New user detected.");
				
				// Add new user to the list, and start a thread for them
				ServerThread newUser = new ServerThread(socket, this);
				uThreads.add(newUser);
				newUser.start();
			}
		} catch (IOException e) {
			u.warning("Alert! Something went wrong: " + e.getMessage());
		}
	}

	/**
	 * addUser adds a user
	 * @param uName The name of the user to add to the HashSet of Users
	 */
	public void addUser(String uName) {
		users.add(uName);
	}

	/**
	 * delUser removes users
	 * @param uName The name of the user to delete from the HashSet of Users
	 * @param user  The Thread currently occupied by that user
	 */
	public void delUser(String uName, ServerThread user) {
		users.remove(uName);
		uThreads.remove(user);
	}
	
	/**
	 * sendMessage sends messages to the group
	 * @param message		Message to be sent to the group
	 * @param sender		Thread of the user which sent it
	 * @throws IOException
	 */
	public void sendMessage(String message, ServerThread sender) throws IOException {
		for (ServerThread user : uThreads) {
			if (user != sender) {
				user.sendMessage(message);
			}
		}
	}

	/**
	 * closeMessage sends a shutdown code to a departing user's ChatRead thread
	 * @param message		Message to be sent to the user
	 * @param sender		Thread of the user who is departing
	 * @throws IOException
	 */
	public void closeMessage(ServerThread sender) throws IOException {
		
		//System.out.println("close from " + sender + ": " + message);
		for (ServerThread user: uThreads) {
			if (user == sender) {
				user.sendMessage(sender.toString());
			}
		}
	}
	
	/**
	 * hasUsers checks if there are users
	 * @return boolean yes/no as to if there are other users in the chat
	 */
	public boolean hasUsers() {
		return !users.isEmpty();
	}

	/**
	 * get a pretty list of users already connected
	 * @return String of usernames currently in the chat
	 */
	public String getUsers() {
		String names = new String();
		for (String user : users) {
			names += " [" + user + "] ";
		}
		return names;
	}
}
