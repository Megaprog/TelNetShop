package store;

import util.Supplier;

import java.io.File;
import java.sql.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * User: Tomas
 * Date: 22.10.12
 * Time: 21:27
 * To change this template use File | Settings | File Templates.
 */
public class Storage {

    private final File itemsFile;
    private final ConnectionProvider connProvider;
    private final ConnectionWorker worker;

    public Storage(final File itemsFile, final ConnectionProvider connProvider) {
        this.itemsFile = itemsFile;
        this.connProvider = connProvider;

        this.worker = new ConnectionWorker() {
            @Override
            public void executeWithConnection(ConnectionExecutor executor) {
                //open db
                Connection conn = null;
                try {
                    conn = connProvider.getConnection();
                } catch (SQLException ex) {
                    handleSQLException(ex);
                }

                //execute something
                if (conn != null) {
                    executor.execute(conn);

                    //close db
                    try {
                        conn.close();
                    } catch (SQLException ex) {
                        handleSQLException(ex);
                    }
                }
            }
        };
    }

    public ConnectionWorker getWorker() {
        return worker;
    }

    public Set<String> loadPlayers() {
        final Set<String> players = new HashSet<String>();

        getWorker().executeWithConnection(new ConnectionExecutor() {
            @Override
            public void execute(Connection conn) {
                Statement stmt = null;
                ResultSet rs = null;
                try {
                    stmt = conn.createStatement();
                    rs = stmt.executeQuery("SELECT name FROM players");
                    while (rs.next()) {
                        players.add(rs.getString(1));
                    }
                }
                catch (SQLException ex){
                    handleSQLException(ex);
                }
                finally {
                    if (stmt != null) {
                        try {
                            stmt.close();
                        }
                        catch (SQLException ex) {
                            handleSQLException(ex);
                        }
                    }
                }
            }
        });

        return Collections.unmodifiableSet(players);
    }

    public Map<String, Item> loadItems() {
        return null;
    }

    public static void handleSQLException(SQLException ex) {
        // handle any errors
        System.out.println("SQLException: " + ex.getMessage());
        System.out.println("SQLState: " + ex.getSQLState());
        System.out.println("VendorError: " + ex.getErrorCode());
    }
}
