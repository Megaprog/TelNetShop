package proba;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * User: Tomas
 * Date: 22.10.12
 * Time: 20:06
 * To change this template use File | Settings | File Templates.
 */
public class ThreadEchoHandler implements Runnable {
	private Socket incoming;
	private static volatile int counter = 0;
	
	public ThreadEchoHandler(Socket incoming) {
		super();
		this.incoming = incoming;
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
