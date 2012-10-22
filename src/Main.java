import store.ConnectionProvider;
import store.Item;
import store.Storage;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

/**
 * User: Tomas
 * Date: 22.10.12
 * Time: 19:15
 * To change this template use File | Settings | File Templates.
 */
public class Main {

    public static int PORT = 8080;
    public static String CONNSTRING = "jdbc:mysql://localhost/test?user=root&password=root";
    public static String ITEMSXMLNAME = "items.xml";

    public static void main(String[] args) {
        //init parameters
        int port = PORT;
        if (args.length > 0) {
            port = Integer.valueOf(args[0]);
        }
        String connString = CONNSTRING;
        if (args.length > 1) {
            connString = args[1];
        }
        String itemsXMLName = ITEMSXMLNAME;
        if (args.length > 2) {
            itemsXMLName = args[2];
        }

        System.out.println("Port=" + port);
        System.out.println("Connection string=" + connString);
        System.out.println("Items file name=" + itemsXMLName);

        //creating Storage
        final String finalConnString = connString;
        Storage storage = new Storage(new File(itemsXMLName), new ConnectionProvider() {
            @Override
            public Connection getConnection() throws SQLException {
                return DriverManager.getConnection(finalConnString);
            }
        });

        //load players
        Map<String, Boolean> players = storage.loadPlayers();
        //load items
        Map<String, Item> items = storage.loadItems();

        try {
            ServerSocket s = new ServerSocket(port);

            while (true) {
                Socket incoming = s.accept();
                //System.out.println("Spawning "+i);

                Runnable r = new ClientHandler(incoming, players, items, storage.getConnectionWorker(), storage.getStatementWorker());
                Thread t = new Thread(r);
                t.start();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}

