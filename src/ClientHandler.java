import store.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.Socket;
import java.sql.*;
import java.util.HashSet;
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

    //commands
    public static final String LOGIN =    "login";
    public static final String LOGOUT =   "logout";
    public static final String VIEWSHOP = "view-shop";
    public static final String MYINFO =   "my-info";
    public static final String BUY =      "buy";
    public static final String SELL =     "sell";
    public static final String EXIT =     "exit";

    private Socket incoming;
    private Map<String, Boolean> players;
    private Map<String, Item> items;
    private StatementWorker statementWorker;

	public ClientHandler(Socket incoming, Map<String, Boolean> players, Map<String, Item> items, StatementWorker statementWorker) {
		super();
		this.incoming = incoming;
        this.players = players;
        this.items = items;
        this.statementWorker = statementWorker;
	}

    protected String loginName;
    protected PrintWriter out;

    @Override
	public void run() {
		try {

			try {
				InputStream inStream = incoming.getInputStream();
				OutputStream outStream = incoming.getOutputStream();
				Scanner in = new Scanner(inStream);
				out = new PrintWriter(outStream, true);

				out.println("Hello! Enter EXIT to exit.");
				boolean done = false;
				
				while (!done && in.hasNextLine()) {
					String command = in.nextLine().trim();
					//out.println("Echo: " + command);

                    //handle commands
					if (command.equalsIgnoreCase(EXIT)) {
                        out.println("disconnected");
                        done = true;
                    }
                    else if (command.regionMatches(true, 0, LOGIN, 0, LOGIN.length())) {
                        login(command.substring(command.length() <= LOGIN.length() ? command.length() : LOGIN.length() + 1));
                    }
                    else if (command.equalsIgnoreCase(LOGOUT)) {
                        logout();
                    }
                    else if (command.equalsIgnoreCase(VIEWSHOP)) {
                        viewshop();
                    }
                    else if (command.equalsIgnoreCase(MYINFO)) {
                        myinfo();
                    }
                    else if (command.regionMatches(true, 0, BUY, 0, BUY.length())) {
                        buy(command.substring(command.length() <= BUY.length() ? command.length() : BUY.length() + 1));
                    }
                    else if (command.regionMatches(true, 0, SELL, 0, SELL.length())) {
                        sell(command.substring(command.length() <= SELL.length() ? command.length() : SELL.length() + 1));
                    }
                    else {
                        out.println("command '" + command + "' is not recognized");
                    }
                }
				
			} finally {
				incoming.close();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

    protected void login(String args) {
        //check if we are logged in
        if (loginName == null) {
            synchronized (players) {
                Boolean isLogged = players.get(args);
                //if name is not found
                if (isLogged == null) {
                    out.println("The name '" + args + "' is not found in players table");
                }
                else if (isLogged) {
                    out.println("User with name '" + args + "' is logged in before");
                }
                else {
                    loginName = args;
                    players.put(loginName, true);
                    out.println("You are logged in as '" + loginName + "'");
                }
            }
        }
        else {
            out.println("You are already login with the name " + loginName + ". Logout first");
        }
    }

    protected void logout() {
        //check if we are logged in
        if (loginName != null) {
            synchronized (players) {
                players.put(loginName, false);
                loginName = null;
                out.println("You are logged out");
            }
        }
        else {
            out.println("You are not logged in");
        }
    }

    protected void viewshop() {
        out.println("Items in the shop:");
        for (Item item : items.values()) {
            out.println(item.getName() + " " + item.getCost());
        }
    }

    protected BigDecimal money;
    protected Set<String> playerItems;

    protected void myinfo() {
        //check if we are logged in
        if (loginName == null) {
            out.println("You are not logged in");
        }
        else {
            statementWorker.executeWithStatement(new StatementProvider() {
                  @Override
                  public Statement getStatement(Connection conn) throws SQLException {
                      return conn.prepareStatement("SELECT money FROM players WHERE name = ?");
                  }
              }, new StatementExecutor() {
                  @Override
                  public void execute(Connection conn, Statement statement) throws SQLException{
                      PreparedStatement pstmt = (PreparedStatement) statement;
                      pstmt.setString(1, loginName);
                      ResultSet rs = pstmt.executeQuery();
                      while (rs.next()) {
                          money = rs.getBigDecimal(1);
                      }
                  }
            });

            playerItems = new HashSet<String>();
            statementWorker.executeWithStatement(new StatementProvider() {
                @Override
                public Statement getStatement(Connection conn) throws SQLException {
                    return conn.prepareStatement("SELECT item_name FROM player_items WHERE player_name = ?");
                }
            }, new StatementExecutor() {
                 @Override
                 public void execute(Connection conn, Statement statement) throws SQLException {
                     PreparedStatement pstmt = (PreparedStatement) statement;
                     pstmt.setString(1, loginName);
                     ResultSet rs = pstmt.executeQuery();
                     while (rs.next()) {
                         playerItems.add(rs.getString(1));
                     }
                 }
            });

            out.println(loginName + " " + money);
            out.println("Your items are:");
            for (String itemName : playerItems) {
                out.println(itemName);
            }
        }
    }

    protected void buy(String args) {
    }

    protected void sell(String args) {
    }
}
