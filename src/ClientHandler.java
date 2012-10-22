import store.ConnectionWorker;
import store.Item;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

/**
 * User: Tomas
 * Date: 22.10.12
 * Time: 20:06
 * To change this template use File | Settings | File Templates.
 */
public class ClientHandler implements Runnable {
    private static volatile int counter = 0;
    private Socket incoming;
    private Set<String> players;
    private Map<String, Item> items;
    private ConnectionWorker connWorker;

	public ClientHandler(Socket incoming, Set<String> players, Map<String, Item> items, ConnectionWorker connWorker) {
		super();
		this.incoming = incoming;
        this.players = players;
        this.items = items;
        this.connWorker = connWorker;
	}

	@Override
	public void run() {
		try {
			try {
				InputStream inStream = incoming.getInputStream();
				OutputStream outStream = incoming.getOutputStream();
				Scanner in = new Scanner(inStream);
				PrintWriter out = new PrintWriter(outStream, true);
				
				counter++;
				System.out.println("Thread "+counter+" opened");
				
				out.println("Hello! Enter BYE to exit.");
				boolean done=false;
				
				while (!done && in.hasNextLine()) {
					String line = in.nextLine();
					out.println("Echo: "+line);
					if (line.trim().equalsIgnoreCase("bye"))
						done=true;
				}
				
			} finally {
				System.out.println("Thread "+counter+" closed");
				counter--;
				incoming.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
