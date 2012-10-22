package proba;

import java.io.*;
import java.net.*;

/**
 * User: Tomas
 * Date: 22.10.12
 * Time: 20:06
 * To change this template use File | Settings | File Templates.
 */
public class ThreadedEchoServer {
	
	public static void main (String[] args) {
		try {
			int i=1;
			ServerSocket s = new ServerSocket(8189);
			
			while (true) {
				Socket incoming = s.accept();
				//System.out.println("Spawning "+i);
				
				Runnable r = new ThreadEchoHandler(incoming);
				Thread t = new Thread(r);
				t.start();
				i++;
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
