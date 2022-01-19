package ie.gmit.dip;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;	
import java.io.PrintWriter;
import java.net.Socket;
/**
 * @author Aiden Desmond g00398273@gmit.ie
 * 
 * ChatClient is the counterpart of ChatServer and allows 
 * the user to connect to a remote or local host to a set
 * port.
 * 
 * Spawns two threads, one to read the console, one to write
 * to the socket.
 * 
 *
 */
public class ChatClient {

	private int port;
	private String server;
	private String user;
	
	private Utils u = new Utils();
	
	/**
	 * @param port		the port to bind to on the remote server
	 * @param server	the ip address of the server
	 * @param user		the user's desired username
	 */
	public ChatClient(int port, String server, String user) {
		this.port = port;
		this.server = server;
		this.user = user;
	}
	
	public void start() {
		u.notice("Connecting client to " + server + ":" + port + " as user " + user);
		
		try {
			Socket socket = new Socket(server, port);
			u.menuItem("Connected to " + server);

			// These are the two threads. Looks simple!
			Thread cread = new ChatReader(socket);
			Thread cwrite = new ChatWriter(socket, user);
			cwrite.start();
			cread.start();	
			
		} catch (IOException e) {
			u.warning("Alert in ChatClient - " + e.getMessage());
		}
		
	}
	/**
	 * @author Aiden Desmond g00398273@gmit.ie
	 * 
	 * ChatReader Thread reads all the data which is sent through the Socket.
	 * It then writes that text to the user's screen.
	 *
	 */	
	private class ChatReader extends Thread {
		private Socket socket;
		
		ChatReader(Socket socket) {
			this.socket = socket;
		}
		

		@Override
		public void run() {
			try {		
				InputStream input = this.socket.getInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(input));
				Boolean readRunner = true;
				while (readRunner) 
					{
						String comms = reader.readLine().trim();
						// This needs a nested check for different types of data.
						if (comms.isEmpty() || comms.isBlank()) {
						} else {
								if(comms.equals(this.toString())) {
									System.out.println("Received Close Command");
									readRunner = false;
									return;
							 } else {
								u.msgRecd(comms);
							 }
						}
					}
				
			} catch (IOException e) {
				u.warning("Problem in ChatReader");
			}
		}
	}
	/**
	 * @author Aiden Desmond g00398273@gmit.ie
	 *
	 * ChatWriter deals with the user's input, taken from the console, and
	 * then sent over the socket to the ChatServer.
	 * 
	 */	
	private class ChatWriter extends Thread {
		private Socket socket;
		private String user;
		
		ChatWriter(Socket socket, String user) {
			this.socket = socket;
			this.user = user;
			
		}
		
		@Override
		public void run() {
			try {
				PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
				String text = user;
				
				out.println(text + "\r\n");
				
				
				Boolean keepRunning = true;
					
				while (keepRunning) {
					Console console = System.console();
					text = console.readLine();
					if (!text.equals("\\q")) {
						String newMessage = user + ": " + text + "\r\n";
						out.println(newMessage);
						
					} else {
						String closeMessage = "!Bye";
						System.out.println("Sending Bye...");
						out.println(closeMessage);
						
						keepRunning = false;
					}
				}
			} catch (IOException e){
				u.warning("Error in Chat Writer");
			}
			
		}
		
	}
}
