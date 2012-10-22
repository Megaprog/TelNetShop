package store;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

/**
 * User: Tomas
 * Date: 22.10.12
 * Time: 21:27
 * To change this template use File | Settings | File Templates.
 */
public class Storage {

    private final File itemsFile;
    private final ConnectionWorker connectionWorker;
    private final StatementWorker statementWorker;

    public Storage(final File itemsFile, final ConnectionProvider connProvider) {
        this.itemsFile = itemsFile;

        this.connectionWorker = new ConnectionWorker() {
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
                    try {
                        executor.execute(conn);
                    } catch (SQLException ex) {
                        handleSQLException(ex);
                    }

                    //close db
                    try {
                        conn.close();
                    } catch (SQLException ex) {
                        handleSQLException(ex);
                    }
                }
            }
        };

        this.statementWorker = new StatementWorker() {
            @Override
            public void executeWithStatement(final StatementProvider statementProvider, final StatementExecutor statementExecutor) {
                getConnectionWorker().executeWithConnection(new ConnectionExecutor() {
                    @Override
                    public void execute(Connection conn) {
                        Statement stmt = null;
                        try {
                            stmt = statementProvider.getStatement(conn);
                            statementExecutor.execute(conn, stmt);
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
            }
        };
    }

    public ConnectionWorker getConnectionWorker() {
        return connectionWorker;
    }

    public StatementWorker getStatementWorker() {
        return statementWorker;
    }

    public Map<String, Boolean> loadPlayers() {
        final Map<String, Boolean> players = new HashMap<String, Boolean>();

        getStatementWorker().executeWithStatement(new StatementProvider() {
                @Override
                public Statement getStatement(Connection conn) throws SQLException {
                    return conn.createStatement();
                }
            }, new StatementExecutor() {
                @Override
                public void execute(Connection conn, Statement statement) throws SQLException{
                    ResultSet rs = statement.executeQuery("SELECT name FROM players");
                    while (rs.next()) {
                        players.put(rs.getString(1), false);
                    }
                }
        });

        return players;
    }

    public Map<String, Item> loadItems() {
        final Map<String, Item> items = new HashMap<String, Item>();

        //parse XML
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = null;
        try {
            db = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        Document doc = null;
        if (db != null) {
            try {
                doc = db.parse(itemsFile);
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //analyse document
        if (doc != null) {
            Element root = doc.getDocumentElement();
            //check root element
            if (!root.getTagName().equals("items")) {
                throw new IllegalArgumentException("Items XML structure not valid in file: " + itemsFile);
            }
            //go through items children
            NodeList itemsList = root.getChildNodes();
            for (int i = 0; i < itemsList.getLength(); i++) {
                Node node1 = itemsList.item(i);
                //check if child is element
                if (node1 instanceof Element) {
                    Element item = (Element) node1;
                    //check child name
                    if (!item.getTagName().equals("item")) {
                        throw new IllegalArgumentException("Items XML structure not valid in file: " + itemsFile);
                    }
                    //go through item children
                    NodeList itemChildren = item.getChildNodes();
                    String name = null;
                    BigDecimal cost = null;
                    for (int j = 0; j < itemChildren.getLength(); j++) {
                        Node node2 = itemChildren.item(j);
                        //check if child is element
                        if (node2 instanceof Element) {
                            Element itemChild = (Element) node2;
                            //check child name
                            if (itemChild.getTagName().equals("name")) {
                                Text textNode = (Text) itemChild.getFirstChild();
                                name = textNode.getData().trim();
                            }
                            //check child cost
                            if (itemChild.getTagName().equals("cost")) {
                                Text textNode = (Text) itemChild.getFirstChild();
                                cost = new BigDecimal(textNode.getData().trim());
                            }
                        }
                    }
                    if (name == null || cost == null) {
                        throw new IllegalArgumentException("Items XML structure not valid in file: " + itemsFile);
                    }
                    items.put(name, new Item(name, cost));
                }
            }
        }

        return Collections.unmodifiableMap(items);
    }

    public static void handleSQLException(SQLException ex) {
        // handle any errors
        System.out.println("SQLException: " + ex.getMessage());
        System.out.println("SQLState: " + ex.getSQLState());
        System.out.println("VendorError: " + ex.getErrorCode());
    }
}
