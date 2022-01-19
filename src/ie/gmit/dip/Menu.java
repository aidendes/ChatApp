package ie.gmit.dip;

import java.io.*;
import java.util.*;

public class Menu {

	// set up a scanner to acknowledge input
	private Scanner sc;
	private boolean keepRunning = true;
	private Properties prop = new Properties();
	private String config = "./app.config";

	private Utils u = new Utils();

	
	// Initialise the application
	public Menu() throws InterruptedException {
		sc = new Scanner(System.in);

		try (FileInputStream app = new FileInputStream(config)) {
			prop.load(app);
		} catch (FileNotFoundException ex) {
			System.out.println("config file not found");
		} catch (IOException ex) {
			System.out.println("Something went wrong");
		}
	}

	public void start() throws Exception {

		while (keepRunning) {
			showOptions();
			
			try {
				int choice = Integer.parseInt(sc.next());
				
				switch(choice) {
					case 1 -> serverLaunch();
					case 2 -> clientLaunch();
					case 3 -> shutdown();
					default -> throw new IllegalArgumentException("User Error: Value not in range: " + choice);
				}
				
			} catch (Exception e) {
				u.warning("Startup Error. Have you put app.config in the directory?");
			}
		}
	}
	




	private void serverLaunch() {
		ChatServer server = new ChatServer(Integer.parseInt(prop.getProperty("SERVER_PORT")));
		server.start();
		keepRunning = false;
	}
	
	private void clientLaunch() {
		Integer cPort = Integer.parseInt(prop.getProperty("SERVER_PORT"));
		String cServer = prop.getProperty("SERVER_IP");
		String cUser = prop.getProperty("USERNAME");
		
		ChatClient client = new ChatClient(cPort, cServer, cUser);
		client.start();

		keepRunning = false;

		
	}
	private void shutdown() {
		u.warning("User has told us to shut down.");
		u.warning("Shutting down now...\n\t\tAiden Desmond - G00398273@gmit.ie");
		keepRunning = false;
	}
	
	private void showOptions() {
		// Allows a cleaner presentation
		System.out.println();
		System.out.flush();
		
		serverPickerMenu();
		clientPickerMenu();
		quitPickerMenu();
		reqInput();
	}

	private void reqInput() {
		System.out.println();
		String[] thisMenu = {"Please select one of the above options",
							 "by entering a number."};
		u.menuActive(thisMenu, "1, 2 or 3? => ");
	}
		
	

	private void quitPickerMenu() {
		u.menuItem("3) Quit the Application");
		
	}

	private void clientPickerMenu() {
		u.menuItem("2) Start the Chat Client");
		
	}

	private void serverPickerMenu() {
		u.menuItem("1) Start the Chat Server");
		
	}
	
	
	
}
