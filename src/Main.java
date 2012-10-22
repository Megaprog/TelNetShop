import proba.ThreadEchoHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * User: Tomas
 * Date: 22.10.12
 * Time: 19:15
 * To change this template use File | Settings | File Templates.
 */
public class Main {

    public static int PORT = 8080;

    public static void main(String[] args) {
        int port = PORT;
        //try to get port from parameters
        if (args.length > 0) {
            port = Integer.valueOf(args[0]);
        }

        try {
            int i=1;
            ServerSocket s = new ServerSocket(port);

            while (true) {
                Socket incoming = s.accept();
                //System.out.println("Spawning "+i);

                Runnable r = new ClientHandler(incoming);
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

